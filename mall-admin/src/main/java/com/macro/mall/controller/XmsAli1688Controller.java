package com.macro.mall.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.rholder.retry.*;
import com.google.common.base.Predicates;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.exception.BizException;
import com.macro.mall.config.OneBoundConfig;
import com.macro.mall.domain.Ali1688Item;
import com.macro.mall.service.XmsAli1688Service;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author jack.luo
 */
@Api(tags = "XmsAli1688Controller", description = "oneBound调用接口")
@RestController
@Slf4j
@RequestMapping("/oneBound")
public class XmsAli1688Controller {

    private XmsAli1688Service ali1688Service;

    private OneBoundConfig oneBoundConfig;

    @Autowired
    public XmsAli1688Controller(XmsAli1688Service ali1688Service, OneBoundConfig oneBoundConfig) {

        this.ali1688Service = ali1688Service;
        this.oneBoundConfig = oneBoundConfig;
    }


    @GetMapping("/pids/{pids}")
    public List<JSONObject> pid(@PathVariable("pids") Long[] pids,
                                @RequestParam(value = "isCache", required = false, defaultValue = "false") boolean isCache) {

        if (!isRunnable(false)) {
            return null;
        }

        if (pids != null && pids.length == 1) {

            List<JSONObject> lstResult = new ArrayList<JSONObject>(1);
            lstResult.add(ali1688Service.getItem(pids[0], isCache));
            return lstResult;
        } else {
            return ali1688Service.getItems(pids, isCache);
        }
    }

    /**
     * alibaba国际站商品详情查询
     *
     * @param pid
     * @return
     */
    @GetMapping("/alibaba/details")
    public JSONObject getAlibabaDetail(@RequestParam("pid") Long pid, @RequestParam(value = "isCache", required = false, defaultValue = "true") boolean isCache) {

        return ali1688Service.getAlibabaDetail(pid, isCache);
    }

    /**
     * 速卖通商品详情查询
     *
     * @param pid
     * @return
     */
    @GetMapping("/aliexpress")
    public JSONObject getAliexpressDetail(@RequestParam("pid") Long pid, @RequestParam(value = "isCache", required = false, defaultValue = "false") boolean isCache) {

        return ali1688Service.getAliexpressDetail(pid, isCache);
    }

    @PostMapping("/aliSearch")
    @ResponseBody
    public CommonResult aliSearch(Integer page, String keyword, String start_price, String end_price, String sort,
                                   @RequestParam(value = "isCache", required = false, defaultValue = "true") boolean isCache) {
        return ali1688Service.getItemByKeyWord(page, keyword, start_price, end_price, sort, isCache);
    }


    @GetMapping("/shop/{shopid}")
    public CommonResult getItemsInShop(@PathVariable("shopid") String shopid, @RequestParam(value = "minSales", required = false, defaultValue = "10") int minSales) {

        if (!isRunnable(true)) {
            return CommonResult.failed("非运行期间");
        }

        if(minSales >= 0){
            log.info("setting minSales is [{}]",minSales);
            oneBoundConfig.minSales = minSales;
        }

        List<Ali1688Item> lstItems = null;

        Callable<List<Ali1688Item>> callable = new Callable<List<Ali1688Item>>() {

            @Override
            public List<Ali1688Item> call() {
                return ali1688Service.getItemsInShop(shopid);

            }
        };

        Retryer<List<Ali1688Item>> retryer = RetryerBuilder.<List<Ali1688Item>>newBuilder()
                .retryIfResult(Predicates.isNull())
                .retryIfExceptionOfType(IllegalStateException.class)
                .retryIfExceptionOfType(BizException.class)
                .withWaitStrategy(WaitStrategies.randomWait(1000, TimeUnit.MILLISECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(2))
                .build();
        try {
            return CommonResult.success(retryer.call(callable));
        } catch (RetryException | ExecutionException e) {
            log.error("getItemsInShop", e);
            return CommonResult.failed(e.getMessage());
        }
    }

    @GetMapping("/pids/clearNotExistItemInCache")
    public int clearNotExistItemInCache() {

        return ali1688Service.clearNotExistItemInCache();
    }

    @GetMapping("/pids/clearAllPidInCache")
    public int clearAllPidInCache() {

        return ali1688Service.clearAllPidInCache();
    }

    @GetMapping("/shop/clearAllShopInCache")
    public int clearAllShopInCache() {

        return ali1688Service.clearAllShopInCache();
    }

    @GetMapping("/pids/getNotExistItemInCache")
    public int getNotExistItemInCache() {

        return ali1688Service.getNotExistItemInCache();
    }

    @GetMapping("/pids/setItemsExpire")
    public CommonResult setItemsExpire(@Param("days") int days) {

        if (days <= 0) {
            return CommonResult.failed("input params is invalid.");
        } else {
            ali1688Service.setItemsExpire(days);
            return CommonResult.success("success");
        }
    }




    /**
     * 是否是可以运行的日期
     *
     * @param isShop
     * @return
     */
    private boolean isRunnable(boolean isShop) {
        String[] splitPid = StringUtils.split(oneBoundConfig.datesPid, ',');
        String[] splitShop = StringUtils.split(oneBoundConfig.datesShop, ',');
        Assert.notNull(splitPid, "config.datesPid is null");
        Assert.notNull(splitShop, "config.datesShop is null");
        Assert.isTrue(splitPid.length > 0, "config.datesPid is empty");
        Assert.isTrue(splitShop.length > 0, "config.splitShop is empty");

        boolean isRunPid = Arrays.asList(splitPid).contains(Integer.toString(LocalDate.now().getDayOfWeek().getValue()));
        boolean isRunShop = Arrays.asList(splitShop).contains(Integer.toString(LocalDate.now().getDayOfWeek().getValue()));

        if (isShop) {
            //控制店铺抓取
            if (isRunShop
                    && !(LocalDateTime.now().getHour() == 23 && LocalDateTime.now().getMinute() > 30)) {
                return true;
            } else {
                //23:30开始不执行程序
                return false;
            }
        } else {
            //控制PID抓取
            if (isRunPid
                    && !(LocalDateTime.now().getHour() == 23 && LocalDateTime.now().getMinute() > 30)) {
                return true;
            } else {
                //23:30开始不执行程序
                return false;
            }
        }

    }


}
