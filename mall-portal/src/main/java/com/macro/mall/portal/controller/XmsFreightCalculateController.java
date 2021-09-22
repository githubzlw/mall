package com.macro.mall.portal.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.util.BeanCopyUtil;
import com.macro.mall.common.util.BigDecimalUtil;
import com.macro.mall.entity.*;
import com.macro.mall.portal.domain.*;
import com.macro.mall.portal.util.*;
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

    @Autowired
    private ExchangeRateUtils exchangeRateUtils;
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
            freightResult.setTotalWeight(freightResult.getTotalWeight());
            return this.freightUtils.commonCalculate(freightResult);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("commonCalculate,freightParam:[{}],error:", freightParam, e);
            return CommonResult.failed("commonCalculate error");
        }

    }

    @ApiOperation(value = "FBA海运运费计算")
    @RequestMapping(value = "/fbaSeaCalculate", method = RequestMethod.POST)
    public CommonResult fbaSeaCalculate(FbaFreightParam fbaFreightParam) {

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
            log.error("fbaSeaCalculate,fbaFreightParam:[{}],error:", fbaFreightParam, e);
            return CommonResult.failed("fbaSeaCalculate error");
        }
    }

    @ApiOperation(value = "FBA空运运费计算")
    @RequestMapping(value = "/fbaAirCalculate", method = RequestMethod.POST)
    public CommonResult fbaAirCalculate(FbaFreightParam fbaFreightParam) {

        Assert.notNull(fbaFreightParam, "fbaFreightParam null");
        Assert.isTrue(null != fbaFreightParam.getCountryId() && fbaFreightParam.getCountryId() > 0, "countryId null");
        Assert.isTrue(null != fbaFreightParam.getWeight() && fbaFreightParam.getWeight() > 0, "weight null");
        Assert.isTrue(null != fbaFreightParam.getVolume() && fbaFreightParam.getVolume() > 0, "volume null");
        try {

            XmsFbaFreightUnit freightUnit = new XmsFbaFreightUnit();
            BeanUtil.copyProperties(fbaFreightParam, freightUnit);
            Map<Integer, List<XmsFbaFreightUnit>> fbaFreightUnitMap = this.fbaFreightUtils.getFbaFreightUnitMap();
            // 判断是否存在此国家
            if (fbaFreightUnitMap.containsKey(fbaFreightParam.getCountryId())) {

                List<XmsFbaFreightUnit> list = fbaFreightUnitMap.get(fbaFreightParam.getCountryId());
                List<XmsFbaFreightUnit> collect = list.stream().filter(e -> 3 == e.getModeOfTransport()).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(collect)) {
                    double tempWeight = 0;
                    switch (fbaFreightParam.getCountryId()) {
                        case 36:// USA
                            // 计费重量=max（实际重量，体积（立方米）*167）
                            tempWeight = Math.max(fbaFreightParam.getWeight(), fbaFreightParam.getVolume() * 167);
                            break;
                        case 6:// CANADA
                            //
//                            tempWeight = Math.max(fbaFreightParam.getWeight(), 0);
                            // 计费重量=max(实际重量，体积（立方厘米）/6000)
                            tempWeight = Math.max(fbaFreightParam.getWeight(), fbaFreightParam.getVolume() * 100 * 100 * 100 / 6000);
                            break;
                        case 35:// UK
//                            tempWeight = Math.max(fbaFreightParam.getWeight(), 1);
                            // 计费重量=max(实际重量，体积（立方厘米）/6000)
                            tempWeight = Math.max(fbaFreightParam.getWeight(), fbaFreightParam.getVolume() * 100 * 100 * 100 / 6000);
                            break;
                        case 13:// GERMANY
                            // 计费重量=max(实际重量，体积（立方厘米）/6000)
                            tempWeight = Math.max(fbaFreightParam.getWeight(), fbaFreightParam.getVolume() * 100 * 100 * 100 / 6000);
                            break;
                        case 20:// ITALY
                            // 计费重量=max(实际重量，体积（立方厘米）/6000)
                            tempWeight = Math.max(fbaFreightParam.getWeight(), fbaFreightParam.getVolume() * 100 * 100 * 100 / 6000);
                            break;
                        default:
                            break;
                    }

                    XmsFbaFreightUnit xmsFbaFreightUnit = collect.get(0);
                    if (fbaFreightParam.getCountryId() == 36) {
                        final String beginCode = fbaFreightParam.getZipCode().substring(0, 1);
                        collect = collect.stream().filter(e -> e.getTypeOfMode() == 1 && e.getZipCode().contains("," + beginCode + ",")).collect(Collectors.toList());
                        if (CollectionUtil.isNotEmpty(collect)) {
                            xmsFbaFreightUnit = collect.get(0);
                        }
                    }
                    xmsFbaFreightUnit.setTotalPrice(BigDecimalUtil.truncateDouble((xmsFbaFreightUnit.getWeightPrice() * tempWeight) / this.exchangeRateUtils.getUsdToCnyRate(), 2));
                    xmsFbaFreightUnit.setWeight(fbaFreightParam.getWeight());
                    xmsFbaFreightUnit.setVolume(fbaFreightParam.getVolume());
                    return CommonResult.success(xmsFbaFreightUnit);
                } else {
                    return CommonResult.failed("No Match for This ModeOfTransport");
                }
            }
            return CommonResult.failed("No Match for This CountryId");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("fbaAirCalculate,fbaFreightParam:[{}],error:", fbaFreightParam, e);
            return CommonResult.failed("fbaAirCalculate error");
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

            EstimatedCost busySellStandard = new EstimatedCost();
            EstimatedCost busySellPremium = new EstimatedCost();

            if (36 == estimatedCostParam.getCountryId()) {
                // 价格95折
                busySellStandard.setEstimatedPrice(estimatedCostResult.getOriginalProductPrice());
                busySellPremium.setEstimatedPrice(estimatedCostResult.getOriginalProductPrice());


                // importXStandard cost 照抄
                busySellStandard.setCost(estimatedCostResult.getOriginalShippingFee());

                // importXPremium cost 集运价格-EUB
                double eubFreight = this.freightUtils.getEubFreight(estimatedCostParam.getWeight());// EUB
                double centralizedFreight = this.freightUtils.getCentralizedTransportFreight(estimatedCostParam.getWeight());// 集运价格
                double rsFreight = centralizedFreight > eubFreight ? BigDecimalUtil.truncateDouble(centralizedFreight - eubFreight, 2) : 0;
                if (StrUtil.isNotEmpty(estimatedCostParam.getOriginalShippingFee())) {
                    // 直接用集运价格
                    busySellPremium.setCost(BigDecimalUtil.truncateDoubleToString(centralizedFreight, 2));
                } else {
                    busySellPremium.setCost(BigDecimalUtil.truncateDoubleToString(rsFreight, 2));
                }
            } else {
                // 其他国家的运费计算：标准运费按eub算，premium不支持，海运的只有有海外仓的那几个国家有
                FreightResult freightResult = new FreightResult();
                if (StrUtil.isNotBlank(estimatedCostParam.getOriginalProductPrice())) {
                    freightResult.setProductCost(Double.parseDouble(estimatedCostParam.getOriginalProductPrice()));
                } else {
                    freightResult.setProductCost(0);
                }

                freightResult.setTotalWeight(estimatedCostParam.getWeight());
                if (StrUtil.isNotBlank(estimatedCostParam.getOriginalShippingFee()) && !(estimatedCostParam.getOriginalShippingFee().contains("free") || estimatedCostParam.getOriginalShippingFee().contains("FREE"))) {
                    freightResult.setB2cFlag(0);
                } else {
                    freightResult.setB2cFlag(1);
                }

                freightResult.setCountryId(estimatedCostParam.getCountryId());
                TrafficFreightUnitResult unitResult = this.freightUtils.commonCalculateResult(freightResult);
                if (CollectionUtil.isNotEmpty(unitResult.getUnitList())) {
                    TrafficFreightUnitShort unitShort = unitResult.getUnitList().stream().filter(e -> StrUtil.isNotBlank(e.getModeOfTransport()) && e.getModeOfTransport().toUpperCase().contains("EPACKET")).findFirst().orElse(null);
                    if (null != unitShort) {
                        busySellStandard.setCost(BigDecimalUtil.truncateDoubleToString(unitShort.getTotalFreight(), 2));
                    } else {
                        //busySellStandard.setCost("-1");
                        busySellStandard.setCost(estimatedCostResult.getOriginalShippingFee());
                    }
                } else {
                    //busySellStandard.setCost("-1");
                    busySellStandard.setCost(estimatedCostResult.getOriginalShippingFee());
                }
            }
            estimatedCostResult.setBusySellStandard(busySellStandard);
            estimatedCostResult.setBusySellPremium(busySellPremium);

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
            List<XmsListOfCountries> countriesList = freightUtils.getCountriesFilterList();
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


    @ApiOperation(value = "shopify的运费计算")
    @RequestMapping(value = "/shopifyFreightCalculate", method = RequestMethod.POST)
    public CommonResult shopifyFreightCalculate(ShopifyFreightParam freightParam) {

        Assert.notNull(freightParam, "fbaFreightParam null");
        Assert.isTrue(null != freightParam.getFreeShippingWeight() && freightParam.getFreeShippingWeight() >= 0, "freeShippingWeight null");
        Assert.isTrue(null != freightParam.getFreeShippingVolume() && freightParam.getFreeShippingVolume() >= 0, "freeShippingVolume null");
        if (freightParam.getFreeShippingWeight() > 0) {
            Assert.isTrue(null != freightParam.getCountryId() && freightParam.getCountryId() >= 0, "countryId null");
            Assert.isTrue(null != freightParam.getFreeShippingProductCost() && freightParam.getFreeShippingProductCost() >= 0, "freeShippingProductCost null");
        }
        Assert.isTrue(null != freightParam.getNonFreeShippingWeight() && freightParam.getNonFreeShippingWeight() >= 0, "nonFreeShippingWeight null");
        Assert.isTrue(null != freightParam.getNonFreeShippingVolume() && freightParam.getNonFreeShippingVolume() >= 0, "nonFreeShippingVolume null");
        Assert.isTrue(null != freightParam.getTransportType() && freightParam.getTransportType() >= 0 && freightParam.getTransportType() < 2, "transportType null");
        Assert.isTrue(null != freightParam.getSingleMaxWeight() && freightParam.getSingleMaxWeight() > 0, "singleMaxWeight null");
        Assert.isTrue(null != freightParam.getSingleMaxVolume() && freightParam.getSingleMaxVolume() > 0, "singleMaxVolume null");
        try {
            /**
             * Shopify订单运输方式计算逻辑和运输方式
             * 1、ship from china
             * 当商品免邮时，运输方式包括
             * Busysell standard express 免邮
             * Busysell premium express 计算逻辑，单个包括重量不能超过4.5KG，体积不能超过0.028立方米，有单个商品不满足此标准，不提供busysel premium express选项，满足条件的情况下，总重量/4.5，运费=((整数部分*61.95+35.2)+(小数部分*4.5*（赛诚表内对应单位运费）+赛诚表内对应操作费))/汇率 - 易U宝运费
             * 其他正常空运快递运输方式，计算逻辑为实际运费 - 易U宝运费
             *
             * 当商品非免邮时，运输方式包括
             * Busysell standard express 按易U宝逻辑计算运费
             * Busysell premium express 同免邮，区别不减去易U宝运费
             * 其他正常空运快递运输方式，计算逻辑为实际运费
             *
             * 2、ship from usa
             * 万邑通的尾程计算逻辑，计算公式同预估费用中的尾程计算公式
             */
            ShopifyFreightResult shopifyFreightResult = new ShopifyFreightResult();
            BeanUtil.copyProperties(freightParam, shopifyFreightResult);
            shopifyFreightResult.setStandardFreight(0D);
            List<TrafficFreightUnitShort> premiumFreightList = new ArrayList<>();

            double standardFreight = 0;
            double premiumFreight = 0;
            FreightResult freightResult = new FreightResult();
            freightResult.setCountryId(freightParam.getCountryId());

            if (0 == freightParam.getTransportType()) {
                // CHINA
                Map<String, TrafficFreightUnitShort> unitShortMap = new HashMap<>();
                if (freightParam.getFreeShippingWeight() > 0) {
                    // 当商品免邮时
                    // Busysell standard express 免邮,价格是0
                    standardFreight += 0;
                    double tempEub = this.freightUtils.getEubFreight(freightParam.getFreeShippingWeight());// EUB
                    double shareNum = freightParam.getFreeShippingWeight() / 4.5;
                    double integerPart = Math.floor(shareNum);
                    double decimalPart = shareNum - integerPart;

                    if (freightParam.getSingleMaxWeight() <= 4.5 && freightParam.getSingleMaxVolume() <= 0.028) {
                        //总重量/4.5，运费=((整数部分*61.95+35.2)+(小数部分*4.5*（赛诚表内对应单位运费）+赛诚表内对应操作费))/汇率 - 易U宝运费
                        premiumFreight += ((integerPart * 61.95 + 35.2) + (this.freightUtils.getCentralizedTransportFreight(decimalPart * 4.5))) / this.freightUtils.getCurrRate() - tempEub;
                    } else {
                        premiumFreight += ((integerPart * 61.95 + 35.2) + (this.freightUtils.getCentralizedTransportFreight(decimalPart * 4.5))) / this.freightUtils.getCurrRate() - tempEub;
                    }
                    if (premiumFreight < 0) {
                        premiumFreight = 0;
                    }
                    freightResult.setTotalWeight(freightParam.getFreeShippingWeight());
                    freightResult.setProductCost(freightParam.getFreeShippingProductCost());
                    // 其他正常空运快递运输方式，计算逻辑为实际运费 - 易U宝运费
                    TrafficFreightUnitResult unitResult = this.freightUtils.commonCalculateResult(freightResult);
                    unitResult.getUnitList().forEach(e -> {
                        e.setTotalFreight(e.getTotalFreight() - tempEub > 0 ? e.getTotalFreight() - tempEub : 0);
                        e.setCostAndFreightOfOurCompany(0);
                        unitShortMap.put(e.getModeOfTransport(), e);
                    });
                }

                if (freightParam.getNonFreeShippingWeight() > 0) {
                    double shareNum = freightParam.getNonFreeShippingWeight() / 4.5;
                    double integerPart = Math.floor(shareNum);
                    double decimalPart = shareNum - integerPart;
                    //当商品非免邮时 按易U宝逻辑计算运费
                    standardFreight += this.freightUtils.getEubFreight(freightParam.getNonFreeShippingWeight());// EUB
                    premiumFreight += ((integerPart * 61.95 + 35.2) + (this.freightUtils.getCentralizedTransportFreight(decimalPart * 4.5))) / this.freightUtils.getCurrRate();
                    // 同免邮，区别不减去易U宝运费
                    TrafficFreightUnitResult unitResult = this.freightUtils.commonCalculateResult(freightResult);
                    unitResult.getUnitList().forEach(e -> {
                        if (unitShortMap.containsKey(e.getModeOfTransport())) {
                            TrafficFreightUnitShort unitShort = unitShortMap.get(e.getModeOfTransport());
                            unitShort.setTotalFreight(unitShort.getTotalFreight() + e.getTotalFreight());
                        }
                    });
                }

                unitShortMap.forEach((k, v) -> v.setTotalFreight(BigDecimalUtil.truncateDouble(v.getTotalFreight(), 2)));
                shopifyFreightResult.setStandardFreight(BigDecimalUtil.truncateDouble(standardFreight, 2));
                shopifyFreightResult.setPremiumFreight(BigDecimalUtil.truncateDouble(premiumFreight, 2));
                List<TrafficFreightUnitShort> airFreightList = new ArrayList<>(unitShortMap.values());
                airFreightList.sort(Comparator.comparing(TrafficFreightUnitShort::getTotalFreight));
                shopifyFreightResult.setAirFreightList(airFreightList);
            } else {
                // USA
                premiumFreight = this.freightUtils.getCentralizedTransportFreight(freightParam.getNonFreeShippingWeight());
                shopifyFreightResult.setStandardFreight(BigDecimalUtil.truncateDouble(standardFreight, 2));
                shopifyFreightResult.setPremiumFreight(BigDecimalUtil.truncateDouble(premiumFreight, 2));
                shopifyFreightResult.setAirFreightList(new ArrayList<>());
            }
            return CommonResult.success(shopifyFreightResult);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("shopifyFreightCalculate,freightParam:[{}],error:", freightParam, e);
            return CommonResult.failed("shopifyFreightCalculate error");
        }
    }


}
