package com.macro.mall.portal.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.entity.*;
import com.macro.mall.portal.domain.*;
import com.macro.mall.portal.util.BeanCopyUtil;
import com.macro.mall.portal.util.BigDecimalUtil;
import com.macro.mall.portal.util.FbaFreightUtils;
import com.macro.mall.portal.util.TrafficFreightUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: 运费计算接口
 * @date:2021-04-14
 */
@RestController
@Api(tags = "XmsFreightCalculateController", description = "运费计算管理")
@RequestMapping("/freight")
@Slf4j
public class XmsFreightCalculateController {


    private final TrafficFreightUtils freightUtils;
    private final FbaFreightUtils fbaFreightUtils;

    @Autowired
    public XmsFreightCalculateController(TrafficFreightUtils freightUtils, FbaFreightUtils fbaFreightUtils) {
        this.freightUtils = freightUtils;
        this.fbaFreightUtils = fbaFreightUtils;
    }


    @ApiOperation(value = "普通运费计算")
    @RequestMapping(value = "/commonCalculate", method = RequestMethod.POST)
    public CommonResult commonCalculate(FreightParam freightParam) {
        Assert.notNull(freightParam, "freightParam null");
        Assert.isTrue(null != freightParam.getCountryId() && freightParam.getCountryId() > 0, "countryId null");
        Assert.isTrue(null != freightParam.getTotalWeight() && freightParam.getTotalWeight() > 0, "totalWeight null");

        try {

            FreightResult freightResult = new FreightResult();
            BeanUtil.copyProperties(freightParam, freightResult);
            freightResult.setTotalWeight(freightResult.getTotalWeight() * 1000);
            return this.freightUtils.commonCalculate(freightResult);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("commonCalculate,freightParam:[{}],error:", freightParam, e);
            return CommonResult.failed("commonCalculate error");
        }

    }

    @ApiOperation(value = "FBA运费计算")
    @RequestMapping(value = "/fbaCalculate", method = RequestMethod.POST)
    public CommonResult fbaCalculate(FbaFreightParam fbaFreightParam) {

        Assert.notNull(fbaFreightParam, "fbaFreightParam null");
        Assert.isTrue(null != fbaFreightParam.getCountryId() && fbaFreightParam.getCountryId() > 0, "countryId null");
        Assert.isTrue(null != fbaFreightParam.getModeOfTransport() && fbaFreightParam.getModeOfTransport() > 0 && fbaFreightParam.getModeOfTransport() < 4, "modeOfTransport null");
        Assert.isTrue(null != fbaFreightParam.getWeight() && fbaFreightParam.getWeight() > 0, "weight null");
        Assert.isTrue(null != fbaFreightParam.getVolume() && fbaFreightParam.getVolume() > 0, "volume null");

        try {
            XmsFbaFreightUnit freightUnit = new XmsFbaFreightUnit();
            BeanUtil.copyProperties(fbaFreightParam, freightUnit);

            if (freightUnit.getModeOfTransport() <= 2) {
                Map<Integer, List<XmsFbaFreightUnit>> fbaFreightUnitMap = this.fbaFreightUtils.getFbaFreightUnitMap();
                // 判断是否存在此国家
                if (fbaFreightUnitMap.containsKey(fbaFreightParam.getCountryId())) {
                    List<XmsFbaFreightUnit> fbaList = this.fbaFreightUtils.getProductShippingCost(freightUnit);
                    List<FbaFreightUnitResult> rsList = new ArrayList<>();
                    if (CollectionUtil.isNotEmpty(fbaList)) {
                        // 简化bean对象
                        fbaList.forEach(e -> {
                            FbaFreightUnitResult unitResult = new FbaFreightUnitResult();
                            BeanUtil.copyProperties(e, unitResult);
                            rsList.add(unitResult);
                        });
                        fbaList.clear();
                        return CommonResult.success(rsList);
                    }
                    return CommonResult.success(fbaList);
                }
                return CommonResult.failed("No Match for This CountryId");
            } else if (freightUnit.getModeOfTransport() == 3) {

                // 获取CIF列表和过滤数据
                List<XmsCifFreightUnit> cifFreightUnitList = this.freightUtils.getCifFreightUnitList();

                XmsCifFreightUnit cifFreightUnit = cifFreightUnitList.stream().filter(e -> e.getCountryId().equals(freightUnit.getCountryId()) && e.getPortName().equalsIgnoreCase(freightUnit.getPortName())).findFirst().orElse(null);

                if (null == cifFreightUnit) {
                    return CommonResult.failed("No Match for This CountryId or PortName");
                }

                // 拷贝原始数据，并且计算值
                XmsFbaFreightUnit rsCifUnit = new XmsFbaFreightUnit();
                rsCifUnit.setCountryId(freightUnit.getCountryId());
                rsCifUnit.setPortName(freightUnit.getPortName());
                rsCifUnit.setWeight(freightUnit.getWeight());
                rsCifUnit.setVolume(freightUnit.getVolume());
                rsCifUnit.setModeOfTransport(3);
                // 不满1方按照1方处理
                rsCifUnit.setTotalPrice(BigDecimalUtil.truncateDouble(cifFreightUnit.getFreightPerVolume() * Math.max(freightUnit.getVolume(), 1D), 2));
                List<XmsFbaFreightUnit> rsList = new ArrayList<>();
                rsList.add(rsCifUnit);
                return CommonResult.success(rsList);
            }
            return CommonResult.failed("No Match for This modeOfTransport");

        } catch (Exception e) {
            e.printStackTrace();
            log.error("fbaCalculate,fbaFreightParam:[{}],error:", fbaFreightParam, e);
            return CommonResult.failed("fbaCalculate error");
        }
    }


    @ApiOperation(value = "预估运费计算")
    @RequestMapping(value = "/estimateFreight", method = RequestMethod.POST)
    public CommonResult estimateFreight(EstimatedCostParam estimatedCostParam) {
        Assert.notNull(estimatedCostParam, "estimatedCostParam null");

        Assert.isTrue(null != estimatedCostParam.getCountryId() && estimatedCostParam.getCountryId() > 0, "countryId null");
        Assert.isTrue(null != estimatedCostParam.getWeight() && estimatedCostParam.getWeight() > 0, "weight null");
        Assert.isTrue(null != estimatedCostParam.getVolume() && estimatedCostParam.getVolume() > 0, "volume null");
        try {

            EstimatedCostResult estimatedCostResult = new EstimatedCostResult();
            if (StrUtil.isEmpty(estimatedCostParam.getOriginalProductPrice())) {
                estimatedCostParam.setOriginalProductPrice("0");
            }
            estimatedCostResult.setOriginalProductPrice(estimatedCostParam.getOriginalProductPrice());

            if (StrUtil.isEmpty(estimatedCostParam.getOriginalShippingFee())) {
                estimatedCostParam.setOriginalShippingFee("0");
            }
            estimatedCostResult.setOriginalShippingFee(estimatedCostParam.getOriginalShippingFee());

            estimatedCostResult.setOriginalWeight(estimatedCostParam.getWeight());
            estimatedCostResult.setOriginalVolume(estimatedCostParam.getVolume());

            // 处理原始价格
            String prefixSymbol = "";
            if (estimatedCostResult.getOriginalProductPrice().contains("CN¥")) {
                prefixSymbol = "CN¥";
            } else if (estimatedCostResult.getOriginalProductPrice().contains("€")) {
                prefixSymbol = "€";
            } else if (estimatedCostResult.getOriginalProductPrice().contains("A$")) {
                prefixSymbol = "A$";
            } else if (estimatedCostResult.getOriginalProductPrice().contains("£")) {
                prefixSymbol = "£";
            } else if (estimatedCostResult.getOriginalProductPrice().contains("$")) {
                prefixSymbol = "$";
            } else if (estimatedCostResult.getOriginalProductPrice().contains("¥")) {
                prefixSymbol = "¥";
            }

            if (StrUtil.isNotEmpty(prefixSymbol)) {
                estimatedCostResult.setOriginalProductPrice(estimatedCostResult.getOriginalProductPrice().replace(prefixSymbol, ""));
            }

            // 原商品的0.75
            estimatedCostResult.setEstimatedPrice(estimatedCostResult.getOriginalProductPrice());

            EstimatedCost importXStandard = new EstimatedCost();
            EstimatedCost importXPremium = new EstimatedCost();

            // 价格95折
            importXStandard.setEstimatedPrice(estimatedCostResult.getOriginalProductPrice());
            importXPremium.setEstimatedPrice(estimatedCostResult.getOriginalProductPrice());


            // importXStandard cost 照抄
            importXStandard.setCost(estimatedCostResult.getOriginalShippingFee());

            // importXPremium cost 集运价格-EUB
            double eubFreight = this.freightUtils.getEubFreight(estimatedCostParam.getWeight() * 1000);// EUB
            double centralizedFreight = this.freightUtils.getCentralizedTransportFreight(estimatedCostParam.getWeight());// 集运价格
            double rsFreight = centralizedFreight > eubFreight ? BigDecimalUtil.truncateDouble(centralizedFreight - eubFreight, 2) : 0;
            if (StrUtil.isNotEmpty(estimatedCostParam.getOriginalShippingFee())) {
                // 直接用集运价格
                importXPremium.setCost(BigDecimalUtil.truncateDoubleToString(centralizedFreight, 2));
            } else {
                importXPremium.setCost(BigDecimalUtil.truncateDoubleToString(rsFreight, 2));
            }


            estimatedCostResult.setImportXStandard(importXStandard);
            estimatedCostResult.setImportXPremium(importXPremium);

            // 获取到家的运费
            XmsFbaFreightUnit freightUnit = new XmsFbaFreightUnit();
            freightUnit.setModeOfTransport(2);// 默认到家
            freightUnit.setCountryId(estimatedCostParam.getCountryId());
            freightUnit.setVolume(estimatedCostParam.getVolume());
            freightUnit.setWeight(estimatedCostParam.getWeight());

            List<XmsFbaFreightUnit> fbaFreightUnitList = this.fbaFreightUtils.getProductShippingCost(freightUnit);
            if (CollectionUtil.isNotEmpty(fbaFreightUnitList)) {
                XmsFbaFreightUnit tempUtil = fbaFreightUnitList.get(0);
                fbaFreightUnitList.get(0).setTotalPrice(BigDecimalUtil.truncateDouble(tempUtil.getTotalPrice() / tempUtil.getRmbRate(), 2));
                estimatedCostResult.setFreightUnit(fbaFreightUnitList.get(0));
                fbaFreightUnitList.clear();
            }

            // 计算尾程运费
            XmsTailFreightResult tailFreightResult = fbaFreightUtils.getTailFreightResult(estimatedCostParam.getWeight() * 1000);
            estimatedCostResult.setTailFreight(tailFreightResult);

            return CommonResult.success(estimatedCostResult);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("estimateFreight,estimatedCostParam[{}],error:", estimatedCostParam, e);

            return CommonResult.failed("estimateFreight failed");
        }

    }


    @CrossOrigin("*")
    @ApiOperation(value = "全球国家列表", notes = "FBA运费")
    @GetMapping("/getCountriesList")
    public CommonResult getCountriesList() {
        try {
            List<XmsListOfCountries> countriesList = freightUtils.getCountriesList();
            return CommonResult.success(countriesList);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.failed(e.getMessage());
        }
    }


    @CrossOrigin("*")
    @ApiOperation(value = "fba国家列表", notes = "FBA运费")
    @GetMapping("/getListOfFbaCountries")
    public CommonResult getListOfFbaCountries() {
        try {
            List<XmsListOfFbaCountries> listOfFbaCountries = freightUtils.getListOfFbaCountries();

            Map<String, Map<String, List<XmsListOfFbaCountries>>> rsMap = new ConcurrentHashMap<>();

            if (CollectionUtil.isNotEmpty(listOfFbaCountries)) {
                listOfFbaCountries.forEach(e -> {
                    if (StrUtil.isBlank(e.getState())) {
                        e.setState("state");
                    }
                });

                // 过滤运费中没有国家的数据

                Map<Integer, List<XmsFbaFreightUnit>> fbaFreightUnitMap = fbaFreightUtils.getFbaFreightUnitMap();
                List<XmsListOfFbaCountries> rsList = listOfFbaCountries.stream().filter(e -> fbaFreightUnitMap.containsKey(e.getCountryId())).collect(Collectors.toList());

                rsList.forEach(e -> e.setCountryEn(e.getCountryEn() + "_" + e.getCountryId()));

                // 按照国家分组
                Map<String, List<XmsListOfFbaCountries>> countryEnMap = rsList.stream().collect(Collectors.groupingBy(XmsListOfFbaCountries::getCountryEn));

                countryEnMap.forEach((k, v) -> {
                    // city排序
                    v.sort(Comparator.comparing(XmsListOfFbaCountries::getCity));
                    // State分组
                    Map<String, List<XmsListOfFbaCountries>> stateMap = v.stream().collect(Collectors.groupingBy(XmsListOfFbaCountries::getState));
                    rsMap.put(k, stateMap);
                });

                rsMap.forEach((k, v) -> {
                    // 排序
                    Map<String, List<XmsListOfFbaCountries>> tmMap = v.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                    (oleValue, newValue) -> oleValue, LinkedHashMap::new));

                    // 排序
                    tmMap.forEach((ck, cv) -> cv.sort(Comparator.comparing(XmsListOfFbaCountries::getCity)));
                    rsMap.put(k, tmMap);
                });

                listOfFbaCountries.clear();
                rsList.clear();
                countryEnMap.clear();
            }

            return CommonResult.success(rsMap);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.failed(e.getMessage());
        }
    }


    @CrossOrigin("*")
    @ApiOperation(value = "fba-cif港口列表", notes = "FBA运费")
    @GetMapping("/getFreightPortList")
    public CommonResult getFreightPortList() {
        try {
            List<XmsCifFreightUnit> cifFreightUnitList = freightUtils.getCifFreightUnitList();
            Map<Integer, List<XmsFbaFreightUnit>> fbaFreightUnitMap = fbaFreightUtils.getFbaFreightUnitMap();
            // 过滤下没有FBA的国家
            List<XmsCifFreightUnit> rsList = cifFreightUnitList.stream().filter(e -> fbaFreightUnitMap.containsKey(e.getCountryId())).collect(Collectors.toList());

            rsList.forEach(e -> e.setCountryEn(e.getCountryEn() + "_" + e.getCountryId()));

            // 分组和港口排序
            Map<String, List<XmsCifFreightUnit>> portMap = rsList.stream().collect(Collectors.groupingBy(XmsCifFreightUnit::getCountryEn));

            portMap.forEach((k, v) -> v.sort(Comparator.comparing(XmsCifFreightUnit::getPortName)));
            cifFreightUnitList.clear();
            rsList.clear();

            return CommonResult.success(portMap);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.failed(e.getMessage());
        }
    }


    @CrossOrigin("*")
    @ApiOperation(value = "home delivery国家列表", notes = "DoorToDoor运费")
    @GetMapping("/getHomeDeliveryCountries")
    public CommonResult getHomeDeliveryCountries() {
        try {
            List<XmsListOfHomeDeliveryCountries> homeDeliveryCountries = fbaFreightUtils.getHomeDeliveryCountries();

            // 拷贝数据并且处理
            List<XmsListOfHomeDeliveryCountries> rsList = BeanCopyUtil.deepListCopy(homeDeliveryCountries);
            rsList.forEach(e -> e.setCountryEn(e.getCountryEn() + "_" + e.getCountryId()));
            // 分组
            Map<String, List<XmsListOfHomeDeliveryCountries>> listMap = rsList.stream().collect(Collectors.groupingBy(XmsListOfHomeDeliveryCountries::getCountryEn));

            // 按照州分组
            listMap.forEach((k, v) -> v.sort(Comparator.comparing(XmsListOfHomeDeliveryCountries::getStateEn)));
            rsList.clear();

            return CommonResult.success(listMap);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.failed(e.getMessage());
        }
    }


    @CrossOrigin("*")
    @ApiOperation(value = "刷新FBA运费相关列表")
    @GetMapping("/refreshFbaAndCountryList")
    public CommonResult refreshFbaAndCountryList() {
        try {
            fbaFreightUtils.forceRefresh();
            freightUtils.forceRefresh();
            return CommonResult.success("success");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("refreshFbaAndCountryList,,error:", e);
            return CommonResult.failed("refreshFbaAndCountryList,error");
        }
    }


}
