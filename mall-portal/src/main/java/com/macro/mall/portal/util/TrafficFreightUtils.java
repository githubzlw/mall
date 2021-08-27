package com.macro.mall.portal.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.util.BeanCopyUtil;
import com.macro.mall.common.util.BigDecimalUtil;
import com.macro.mall.entity.*;
import com.macro.mall.mapper.*;
import com.macro.mall.portal.domain.CentralizedTransportFreight;
import com.macro.mall.portal.domain.FreightResult;
import com.macro.mall.portal.domain.TrafficFreightUnitResult;
import com.macro.mall.portal.domain.TrafficFreightUnitShort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.util
 * @date:2021-04-14
 */
@Service
@Slf4j
public class TrafficFreightUtils {

    @Autowired
    private ExchangeRateUtils exchangeRateUtils;


    @Autowired
    private XmsCifFreightUnitMapper cifFreightUnitMapper;

    @Autowired
    private XmsListOfCountriesMapper listOfCountriesMapper;

    @Autowired
    private XmsListOfFbaCountriesMapper listOfFbaCountriesMapper;

    @Autowired
    private XmsTrafficFreightFbaMapper trafficFreightFbaMapper;
    @Autowired
    private XmsTrafficFreightPortMapper trafficFreightPortMapper;
    @Autowired
    private XmsTrafficFreightUnitMapper trafficFreightUnitMapper;

    /**
     * 国家列表
     */
    private List<XmsListOfCountries> countriesList = new ArrayList<>();
    /**
     * 国家列表
     */
    private List<XmsListOfCountries> countriesFilterList = new ArrayList<>();
    /**
     * FBA国家城市列表
     */
    private List<XmsListOfFbaCountries> listOfFbaCountries = new ArrayList<>();
    /**
     * 普通运费的List
     */
    private List<XmsTrafficFreightUnit> freightUnitList = new ArrayList<>();

    /**
     * 普通运费的MapList
     */
    private Map<Integer, List<XmsTrafficFreightUnit>> trafficFreightMap = new HashMap<>();
    /**
     * FBA运费列表List
     */
    private List<XmsTrafficFreightFba> freightFbaList = new ArrayList<>();
    /**
     * FBA运费列表ListMapList
     */
    private Map<Integer, List<XmsTrafficFreightFba>> fbaFreightMap = new HashMap<>();
    /**
     * 港口运费列表
     */
    private List<XmsTrafficFreightPort> freightPortList = new ArrayList<>();
    /**
     * CIF-FBA的港口和运费列表
     */
    private List<XmsCifFreightUnit> cifFreightUnitList = new ArrayList<>();

    /**
     * 默认免邮运费固定为美国运费
     */
    private XmsTrafficFreightUnit defaultTrafficFreightUnit = new XmsTrafficFreightUnit();

    /**
     * 集运运费列表
     */
    private List<CentralizedTransportFreight> centralizedFreightList = new ArrayList<>();

    //------------------------------方法区---------------------------


    /**
     * 普通运费的计算
     *
     * @param freightResult
     */
    public CommonResult commonCalculate(FreightResult freightResult) {

        return CommonResult.success(commonCalculateResult(freightResult));
    }

    public TrafficFreightUnitResult commonCalculateResult(FreightResult freightResult){
        // 未完成版本
        boolean isSupplyEconomicMethod = this.checkIsSupplyEconomicMethod(freightResult.getCountryId(), 1);

        double canToFreeFlag = 0d;// 是否可免邮
        if (freightResult.getB2cFlag() == 1) {
            //b2c 超过99 免邮
            if (freightResult.getProductCost() > 99d && isSupplyEconomicMethod) {
                canToFreeFlag = 0.001;
                if (this.checkIsSupplyEconomicMethod(freightResult.getCountryId(), 2)) {
                    canToFreeFlag = 0.003;
                }
            } else if (isSupplyEconomicMethod) {
                canToFreeFlag = 0.002;
            }
        }

        // 1.获取结果
        TrafficFreightUnitResult calculateResult = this.getTrafficFreightUnitResult(freightResult, canToFreeFlag);
        // 2.进行数据过滤
        this.checkAndFilterUnitResult(calculateResult, false, null, false);
        return calculateResult;
    }

    /**
     * 普通运费分2步进行  1获取运费列表和附加费结果
     *
     * @param freightResult
     * @return
     */
    private TrafficFreightUnitResult getTrafficFreightUnitResult(FreightResult freightResult, double canToFreeFlag) {

        TrafficFreightUnitResult calculateResult = new TrafficFreightUnitResult();

        List<XmsTrafficFreightUnit> unitList = new ArrayList<>();// 普通运费列表
        List<XmsTrafficFreightUnit> seaList = new ArrayList<>();// 海运列表

        // 初始化数据,港口等数据
        initCountryAndFreightList();
        // 获取普通运费
        if (this.trafficFreightMap.containsKey(freightResult.getCountryId())) {
            unitList = BeanCopyUtil.deepListCopy(this.trafficFreightMap.get(freightResult.getCountryId()));
        }

        double freeWeight = 0;
        // 获取正常重量的免邮价格
        double freePostagePrice = 0;
        if (freightResult.getProductCost() > 0) {
            freePostagePrice = this.getFreePostagePrice(freightResult.getTotalWeight(), 1);
        } else {
            freePostagePrice = this.getFreePostagePrice(freeWeight);
        }

        boolean canFreeShipping = false;//超过99 免邮free shipping
        boolean supplyFreeCountry = false;//不超过99 但是免邮
        boolean otherShippingCost = false;// 超过99 免邮但是是其他国家，需要额外 10美金

        if (canToFreeFlag == 0.001d) {
            canFreeShipping = true;
        } else if (canToFreeFlag == 0.002d) {
            supplyFreeCountry = true;
        } else if (canToFreeFlag == 0.003d) {
            canFreeShipping = true;
            otherShippingCost = true;
        }

        double RSCNFI = 0d;// 实际航运成本非免费运费项目 real shipping cost non-free freightcost ietms

        boolean isJCEXOrCNE = false; //是否存在JCEXOrCNE
        boolean isCne = false; //是否存在cne
        boolean isJcex = false; //是否存在jcex
        boolean isEub = false;

        double surcharge = 0;//附加费用

        for (XmsTrafficFreightUnit unit : unitList) {
            unit.setId(0);
            String shippingMethod = unit.getModeOfTransport();

            isJCEXOrCNE = this.checkIsJCEXOrCNE(isJCEXOrCNE, shippingMethod);
            boolean tempIsJcex = this.checkTransportMode(shippingMethod, 6);

            if (!isCne) {
                isCne = this.checkTransportMode(shippingMethod, 5);
            }
            if (!isJcex) {
                isJcex = tempIsJcex;
            }

            /*对EUB,EMS,ChinaPost这种运输方式 按实际重量算运费，对其他运输方式按体积重量算*/
            // 提取计算总运费的公共接口
            double totalFreight = unit.calculateFreight(freightResult.getTotalWeight());
            //判断是否是EUB
            boolean isEubChild = this.checkTransportMode(shippingMethod, 3);
            double eubRate = 1;

            // Description : 是否是ship within china
            boolean isShipWithChina = this.checkTransportMode(shippingMethod, 10);
            if (!isEub && isEubChild) {
                isEub = isEubChild;
            }

            boolean isDDP = this.checkTransportMode(unit.getModeOfTransport(), 11);

            //如果是jcex 或者 eub 利润率为 1
            boolean isNoAdd = isEubChild || tempIsJcex;

            double totalWeightTemp = this.getProfitableFreight(freePostagePrice, totalFreight, isNoAdd, eubRate, isShipWithChina, freightResult);
            double getBigHeavyFreight = this.getBigHeavyFreight(freePostagePrice, unit.getCostAndFreightOfOurCompany(), eubRate);
            // 开始赋值
            unit.setDiscountedTotalPrice(getBigHeavyFreight);
            unit.setCostAndFreightOfOurCompany(totalFreight / this.exchangeRateUtils.getUsdToCnyRate());
            unit.setTotalFreight(totalWeightTemp);
            if (isDDP) {
                totalWeightTemp = Math.max(totalWeightTemp, FreightConstant.MINBULKAIRFREIGHTCOST);
                unit.setTotalFreight(totalWeightTemp);
            }

            //是免邮国家，且满足99 jcex 免邮
            double jcexFreightCost = unit.getTotalFreight();
            if (canFreeShipping && tempIsJcex) {
                unit.setTotalFreight(0D);
                if (otherShippingCost) {
                    unit.setTotalFreight(10d);
                }
            } else if (supplyFreeCountry && tempIsJcex && jcexFreightCost > 0) {
                jcexFreightCost = Math.max(9d, jcexFreightCost * 0.5d);
                unit.setTotalFreight(jcexFreightCost);
            }

            if (FreightConstant.SHIP_CHINA.equalsIgnoreCase(shippingMethod)) {
                unit.setId(1);
            }
            // 如果是bulk Air Freight 则放到后面
            boolean b = this.checkTransportMode(shippingMethod, 11);
            if (b) {
                unit.setId(2);
            }
            // 循环结束
        }

        // Description : 9天以下 交期的运费 >=  EUB 运费 *30%
        double ECFreightCost = 0d;// /eub || china post freight cost运费
        double JCEXShippingCost = 0d;
        this.judgmentOfVariousFreightCharges(unitList, isJCEXOrCNE, ECFreightCost, RSCNFI, JCEXShippingCost, 0.3d);
        this.judgmentJcexAndCneOrEubFreightCharges(unitList, isJcex && (isCne || isEub));

        this.checkTotalFreightByProductCost(freightResult.getProductCost(), unitList);
        // Description :    按照运费排序
        unitList.sort(Comparator.comparing(XmsTrafficFreightUnit::getTotalFreight));

        // ---------------------------------------------------------
        List<TrafficFreightUnitShort> unitShortList = new ArrayList<>();
        List<TrafficFreightUnitShort> seaShortList = new ArrayList<>();

        this.copyBean(unitList, unitShortList);
        this.copyBean(seaList, seaShortList);

        calculateResult.setUnitList(unitShortList);
        calculateResult.setSeaList(seaShortList);
        calculateResult.setSurcharge(surcharge);
        // ---------------------------------------------------------

        return calculateResult;
    }

    private void copyBean(List<XmsTrafficFreightUnit> unitList, List<TrafficFreightUnitShort> unitShortList) {
        if (CollectionUtil.isNotEmpty(unitList)) {
            unitList.forEach(e -> {
                TrafficFreightUnitShort unitShort = new TrafficFreightUnitShort();
                BeanUtil.copyProperties(e, unitShort);
                unitShort.setCostAndFreightOfOurCompany(BigDecimalUtil.truncateDouble(unitShort.getCostAndFreightOfOurCompany(), 2));
                unitShort.setTotalFreight(BigDecimalUtil.truncateDouble(unitShort.getTotalFreight(), 2));
                unitShort.setDiscountedTotalPrice(BigDecimalUtil.truncateDouble(unitShort.getDiscountedTotalPrice(), 2));
                unitShortList.add(unitShort);
            });
            unitList.clear();
        }
    }

    /**
     * 普通运费分2步进行  2各种数据过滤
     *
     * @param calculateResult
     * @param isFreeFlag
     * @param shippingMethod
     * @param choseJcex
     */
    private void checkAndFilterUnitResult(TrafficFreightUnitResult calculateResult, boolean isFreeFlag, String shippingMethod, boolean choseJcex) {
        // 判断和过滤购物车数据(模拟)
        List<TrafficFreightUnitShort> unitList = calculateResult.getUnitList();// 普通运费列表
        List<TrafficFreightUnitShort> seaList = calculateResult.getSeaList();// 海运列表
        double surcharge = calculateResult.getSurcharge();//附加费用
        if (isFreeFlag) {
            double economicShippingCost = 9d;
            unitList.forEach(e -> e.setTotalFreight(Math.max(e.getTotalFreight(), economicShippingCost)));
        }

        List<TrafficFreightUnitShort> unitNewList = new ArrayList<>();

        // 过滤Ship Within China
        /*TrafficFreightUnitShort unit1 = unitList.stream().filter(e -> FreightConstant.SHIP_CHINA.equalsIgnoreCase(e.getModeOfTransport())).findFirst().orElse(null);
        if (null != unit1) {
            unitList.clear();
            unitList.add(unit1);
        }*/

        //过滤 包含保险费的与运输方式,不参与运输方式选择
        unitNewList.addAll(unitList);

        // String shippingMethod = null;// 默认客户没有选择
        // boolean choseJcex = false;
        if (null == shippingMethod || (null != shippingMethod && !FreightConstant.DDP_NAME.equalsIgnoreCase(shippingMethod))) {
            List<TrafficFreightUnitShort> collect = unitNewList.stream().filter(e -> !FreightConstant.DDP_NAME.equalsIgnoreCase(e.getModeOfTransport())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(collect)) {
                unitNewList.clear();
                unitNewList.addAll(collect);
            }
        }


        //用户选择的运输方式
        String defaultShippingMethod = "EUB";
        String chooseShippingMethod = null;// 程序中给出的默认方式
        TrafficFreightUnitShort unitCommon = new TrafficFreightUnitShort();
        double shippingCost = 0d;
        if (shippingMethod == null && unitNewList.size() > 0) {
            unitNewList.sort(Comparator.comparing(TrafficFreightUnitShort::getTotalFreight));//按照 shipcost 从小到大排序
            boolean exsitJcex = false;
            if (choseJcex) {
                for (TrafficFreightUnitShort unitTemp : unitNewList) {
                    String shippingmethod = unitTemp.getModeOfTransport();
                    boolean isJcexTemp = this.checkTransportMode(shippingmethod, 6);
                    if (isJcexTemp) {
                        unitCommon = unitTemp;
                        shippingCost = unitCommon.getTotalFreight();
                        defaultShippingMethod = unitCommon.getModeOfTransport();
                        exsitJcex = true;
                        break;
                    }
                }
            }
            if (!choseJcex || (!exsitJcex)) {
                unitCommon = unitNewList.get(0);
                shippingCost = unitCommon.getTotalFreight();
                defaultShippingMethod = unitCommon.getModeOfTransport();
            }
            // 默认客户存放的数据
            chooseShippingMethod = defaultShippingMethod;
        } else if (shippingMethod != null) {
            defaultShippingMethod = shippingMethod;
        }
        //根据运输方式获取对应的交期信息和对应的运费
        List<TrafficFreightUnitShort> choseUnitList = new ArrayList<>();
        if (shippingMethod != null) {
            for (int i = 0; i < unitNewList.size(); i++) {
                TrafficFreightUnitShort pricecost = unitNewList.get(i);
                if (pricecost.getModeOfTransport().equalsIgnoreCase(shippingMethod)) {
                    choseUnitList.add(pricecost);
                }
            }
        }
        if (choseUnitList.size() <= 0 && unitNewList.size() > 0) {
            if (CollectionUtil.isNotEmpty(seaList)) {
                for (int i = 0; i < seaList.size(); i++) {
                    TrafficFreightUnitShort pricecost = seaList.get(i);
                    if (pricecost.getModeOfTransport().equalsIgnoreCase(shippingMethod)) {
                        choseUnitList.add(pricecost);
                    }
                }
            }
            if (choseUnitList.size() <= 0) {
                unitNewList.sort(Comparator.comparing(TrafficFreightUnitShort::getTotalFreight));//按照 shipcost 从小到大排序

                unitCommon = unitNewList.get(0);
                shippingCost = unitCommon.getTotalFreight();
                defaultShippingMethod = unitCommon.getModeOfTransport();
                chooseShippingMethod = defaultShippingMethod;
            } else {
                shippingCost = choseUnitList.get(0).getTotalFreight();
            }
        } else {
            if (choseUnitList.size() > 0) {
                shippingCost = choseUnitList.get(0).getTotalFreight();
            }
        }

        if (seaList.size() > 0) {
            for (TrafficFreightUnitShort tr : seaList) {
                unitList.add(tr);
            }
        }
        //来个排序,china within china 放到最后
        unitList.sort(Comparator.comparing(TrafficFreightUnitShort::getId).thenComparing(TrafficFreightUnitShort::getTotalFreight));
        int hasInsuranceFee = 0;
        boolean isJcexTemp = this.checkTransportMode(defaultShippingMethod, 6);
        if (surcharge > 0 && isJcexTemp) {
            hasInsuranceFee = 1;
        }
        /*System.err.println("surcharge:" + surcharge);
        System.err.println("hasInsuranceFee:" + hasInsuranceFee);
        System.err.println("defaultShippingMethod:" + defaultShippingMethod);

        System.err.println("shippingCost:" + DoubleUtil.divide(shippingCost, 1d, 2));
        System.err.println("chooseShippingMethod:" + chooseShippingMethod);*/
        /*map.put("insuranceFee", surcharge);
        map.put("hasInsuranceFee", hasInsuranceFee);
        map.put("defultShippingMethod", defaultShippingMethod);
        map.put("freightResult", calculateResult);
        map.put("shippingCost", DoubleUtil.divide(shippingCost, 1d, 2));*/
    }


    /**
     * 获取集运运费
     *
     * @param weight
     * @return
     */
    public double getCentralizedTransportFreight(double weight) {
        initCentralizedTransportFreight();

        CentralizedTransportFreight anElse = this.centralizedFreightList.stream().filter(e -> e.getMinWeight() <= weight && weight <= e.getMaxWeight()).findFirst().orElse(null);
        if (null == anElse) {
            return 0;
        }
        return BigDecimalUtil.truncateDouble((anElse.getHandlingFee() + anElse.getFreightPerKilogram() * weight) / this.exchangeRateUtils.getUsdToCnyRate(), 2);
    }


    //------------------------------方法区---------------------------

    /**
     * 初始化集中运费列表
     */
    private List<CentralizedTransportFreight> initCentralizedTransportFreight() {

        synchronized (TrafficFreightUtils.class) {

            if (CollectionUtil.isEmpty(this.centralizedFreightList)) {
                this.centralizedFreightList = new ArrayList<>();

                CentralizedTransportFreight centralizedFreight1 = new CentralizedTransportFreight();
                centralizedFreight1.setMinWeight(0.05);
                centralizedFreight1.setMaxWeight(0.11);
                centralizedFreight1.setHandlingFee(22.96);
                centralizedFreight1.setFreightPerKilogram(61.17);
                this.centralizedFreightList.add(centralizedFreight1);

                CentralizedTransportFreight centralizedFreight2 = new CentralizedTransportFreight();
                centralizedFreight2.setMinWeight(0.111);
                centralizedFreight2.setMaxWeight(0.225);
                centralizedFreight2.setHandlingFee(25.37);
                centralizedFreight2.setFreightPerKilogram(61.17);
                this.centralizedFreightList.add(centralizedFreight2);


                CentralizedTransportFreight centralizedFreight3 = new CentralizedTransportFreight();
                centralizedFreight3.setMinWeight(0.226);
                centralizedFreight3.setMaxWeight(0.34);
                centralizedFreight3.setHandlingFee(29.55);
                centralizedFreight3.setFreightPerKilogram(61.17);
                this.centralizedFreightList.add(centralizedFreight3);


                CentralizedTransportFreight centralizedFreight4 = new CentralizedTransportFreight();
                centralizedFreight4.setMinWeight(0.341);
                centralizedFreight4.setMaxWeight(0.45);
                centralizedFreight4.setHandlingFee(36.9);
                centralizedFreight4.setFreightPerKilogram(61.17);
                this.centralizedFreightList.add(centralizedFreight4);

                CentralizedTransportFreight centralizedFreight5 = new CentralizedTransportFreight();
                centralizedFreight5.setMinWeight(0.451);
                centralizedFreight5.setMaxWeight(0.9);
                centralizedFreight5.setHandlingFee(56.47);
                centralizedFreight5.setFreightPerKilogram(61.17);
                this.centralizedFreightList.add(centralizedFreight5);

                CentralizedTransportFreight centralizedFreight6 = new CentralizedTransportFreight();
                centralizedFreight6.setMinWeight(0.901);
                centralizedFreight6.setMaxWeight(2.26);
                centralizedFreight6.setHandlingFee(56.47);
                centralizedFreight6.setFreightPerKilogram(68.20);
                this.centralizedFreightList.add(centralizedFreight6);

                CentralizedTransportFreight centralizedFreight7 = new CentralizedTransportFreight();
                centralizedFreight7.setMinWeight(2.261);
                centralizedFreight7.setMaxWeight(4.5);
                centralizedFreight7.setHandlingFee(56.47);
                centralizedFreight7.setFreightPerKilogram(77.58);
                this.centralizedFreightList.add(centralizedFreight7);
            }
        }

        return this.centralizedFreightList;

    }

    /**
     * 根据商品总价格设置运费
     *
     * @param productCost
     * @param unitList
     */
    private void checkTotalFreightByProductCost(double productCost, List<XmsTrafficFreightUnit> unitList) {
        if (productCost > 0) {
            unitList.forEach(e -> {
                boolean b = false;
                if (e.getTotalFreight() <= 0d) {
                    if (productCost > 8) {
                        b = true;
                    }
                } else {
                    b = DoubleUtil.divide(productCost, e.getTotalFreight(), 2) > 8;
                }
                String shippingMethod = e.getModeOfTransport();
                boolean b1 = ((shippingMethod.contains("UPS") && !(shippingMethod.contains("CNE"))) || (shippingMethod.contains("DHL") && !(shippingMethod.contains("CNE"))) || shippingMethod.contains("EMS") || shippingMethod.contains("FEDEX")) && b;
                if (b1) {
                    e.setTotalFreight(Math.max(DoubleUtil.mul(productCost, 0.125d), e.getTotalFreight()));
                }
            });
        }
    }


    /**
     * Jcex并且是 Cne或者Eub的运费计算
     *
     * @param unitList
     * @param isJcexAndCneOrEub
     */
    private void judgmentJcexAndCneOrEubFreightCharges(List<XmsTrafficFreightUnit> unitList, boolean isJcexAndCneOrEub) {
        if (isJcexAndCneOrEub) {
            double cneShippingCost = 0d;
            double eubShippingCost = 0d;
            for (XmsTrafficFreightUnit e : unitList) {
                boolean cne = this.checkTransportMode(e.getModeOfTransport(), 5);
                if (cne) {
                    cneShippingCost = e.getTotalFreight();
                }
                boolean eub = this.checkTransportMode(e.getModeOfTransport(), 1);
                if (eub) {
                    eubShippingCost = e.getTotalFreight();
                }
            }
            final double cneShippingCostF = cneShippingCost;
            final double eubShippingCostF = eubShippingCost;
            unitList.forEach(e -> {
                boolean fedex = this.checkTransportMode(e.getModeOfTransport(), 4);
                if (fedex) {
                    e.setTotalFreight(Math.max(e.getTotalFreight(), DoubleUtil.add(Math.max(cneShippingCostF, eubShippingCostF), 3d)));
                }
            });
        }
    }


    /**
     * 判断各种运费的逻辑
     *
     * @param unitList
     * @param isJCEXOrCNE
     * @param ECFreightCostF
     * @param RSCNFIF
     * @param JCEXShippingCost
     * @param addRate
     */
    private void judgmentOfVariousFreightCharges(List<XmsTrafficFreightUnit> unitList, boolean isJCEXOrCNE, double ECFreightCostF, double RSCNFIF, double JCEXShippingCost, double addRate) {
        if (isJCEXOrCNE) {
            for (XmsTrafficFreightUnit unit : unitList) {
                if (unit.getDeliveryTime().contains("-")) {
                    //9天以下 交期的运费 >=  EUB 运费 *30%
                    int thisShippingTime = Integer.parseInt(unit.getDeliveryTime().split("-")[0]);
                    if (thisShippingTime < 9) {
                        unit.setTotalFreight(Math.max(unit.getTotalFreight(), new BigDecimal(ECFreightCostF * addRate).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
                    }
                }
                //Description : MAX(计算运费,非免邮商品EUB真实运费*0.3)
                unit.setTotalFreight(Math.max(unit.getTotalFreight(), new BigDecimal(RSCNFIF * addRate).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
                //  Description : 在运费计算中， 强制 DHL和 FedEx运费至少比 JCEX或者CNE 贵10美元  (如果没有JCEX或者 CNE ，这个规则就不适用， 如果 同时存在则使用 Max (JCEX+10,DHL(FedEx))）
                //Description : 先获取jcex（ImportX Standard） 的运费
                if (FreightConstant.IMPORTX_STANDARD.equalsIgnoreCase(unit.getModeOfTransport())) {
                    JCEXShippingCost = unit.getTotalFreight();
                } else if (JCEXShippingCost <= 0 && unit.getModeOfTransport().toLowerCase().contains(FreightConstant.CNE_NAME.toLowerCase())) {
                    JCEXShippingCost = unit.getTotalFreight();
                }
                //End：
            }
            //  Description : 在运费计算中， 强制 DHL和 FedEx运费至少比 JCEX或者CNE 贵10美元  (如果没有JCEX或者 CNE ，这个规则就不适用， 如果 同时存在则使用 Max (JCEX+10,DHL(FedEx))）
            final double JCEXShippingCostF = DoubleUtil.add(JCEXShippingCost, 10d);
            unitList.forEach(e -> {
                if (FreightConstant.DHL_NAME.equalsIgnoreCase(e.getModeOfTransport()) || e.getModeOfTransport().toLowerCase().contains(FreightConstant.FEDEX_NAME.toLowerCase())) {
                    e.setTotalFreight(Math.max(e.getTotalFreight(), JCEXShippingCostF));
                }
            });
        }
    }


    /**
     * 正常重量的免邮价格
     *
     * @param totalWeight
     * @return
     */
    private double getFreePostagePrice(double totalWeight) {
        // Description : 获取正常重量的免邮价格
        // return 5 + 0.042f * totalWeight;
        return 25 + 85f * totalWeight;
    }

    /**
     * 有价格的重量的免邮价格
     *
     * @param totalWeight
     * @return
     */
    private double getFreePostagePrice(double totalWeight, int num) {
        // Description : 获取正常重量的免邮价格
        return 25 + 85f * totalWeight * num;
    }

    /**
     * 获取EUB的运费
     *
     * @param totalWeight
     * @return
     */
    public double getEubFreight(double totalWeight) {
        return getFreePostagePrice(totalWeight) / exchangeRateUtils.getUsdToCnyRate();
    }


    private double getBigHeavyFreight(double freeTotalPrice, double totalFreight, double rate) {
        // Description : 如果购物车重量超过21kg ,则要计算不超过21kg的运费
        // Description : 超过21kg以后不超过21kg的运费
        if (totalFreight > 0) {
            //Description : (整体运费减去免邮运费)*利润率 / 汇率
            double shippingCostRs = (totalFreight - freeTotalPrice * rate) * FreightConstant.PROFITMARGIN / this.exchangeRateUtils.getUsdToCnyRate();
            if (shippingCostRs <= 0) {
                shippingCostRs = 0;
            }
            return shippingCostRs;
        }
        return 0;
    }


    /**
     * 获取带利润的运费
     *
     * @param freeTotalPrice
     * @param totalFreight
     * @param isJcex
     * @param rate
     * @param isShipWithChina
     * @return
     */
    private double getProfitableFreight(double freeTotalPrice, double totalFreight, boolean isJcex, double rate, boolean isShipWithChina, FreightResult freightResult) {
        double profitMargin = FreightConstant.PROFITMARGIN;
        if (isShipWithChina || isJcex) {
            profitMargin = 1;
        }
        // Description : 利润, 减去免邮
        if(freightResult.getB2cFlag() > 0){
            totalFreight = totalFreight * profitMargin - freeTotalPrice * rate;
        } else{
            totalFreight = totalFreight * profitMargin;
        }
        if (totalFreight <= 0) {
            totalFreight = 0;
        }
        //直接使用免邮价格
        //return freeTotalPrice / exchangeRateUtils.getUsdToCnyRate();
        return totalFreight / exchangeRateUtils.getUsdToCnyRate();
    }

    private boolean checkIsJCEXOrCNE(boolean isJCEXOrCNE, String shippingMethod) {
        // Description : 校验是否存在  JCEXOrCNE
        if (!isJCEXOrCNE) {
            isJCEXOrCNE = this.checkTransportMode(shippingMethod, 4);
            if (!isJCEXOrCNE) {
                isJCEXOrCNE = this.checkTransportMode(shippingMethod, 5);
            }
        }
        return isJCEXOrCNE;
    }


    /**
     * 判断运费类型
     *
     * @param shippingMethod
     * @param flag
     * @return
     */
    public boolean checkTransportMode(String shippingMethod, int flag) {
        switch (flag) {
            case 0:
                return shippingMethod.toLowerCase().contains(FreightConstant.EPACKET_NAME.toLowerCase())
                        || shippingMethod.toLowerCase().contains(FreightConstant.CHINA_POST.toLowerCase())
                        || shippingMethod.toLowerCase().contains(FreightConstant.EMS_NAME);
            case 1:
                return shippingMethod.toLowerCase().contains(FreightConstant.EPACKET_NAME.toLowerCase());
            case 3:
                return shippingMethod.toLowerCase().contains(FreightConstant.EPACKET_NAME.toLowerCase())
                        || shippingMethod.toLowerCase().contains(FreightConstant.CHINA_POST.toLowerCase()) && !(shippingMethod.toLowerCase().contains(FreightConstant.CHINA_POST_AIR_MAIL.toLowerCase()));
            case 4:
                return shippingMethod.toLowerCase().contains(FreightConstant.IMPORTX_STANDARD.toLowerCase());
            case 5:
                return shippingMethod.toLowerCase().contains(FreightConstant.CNE_NAME.toLowerCase());
            case 6:
                return FreightConstant.JCEX_NAME.equalsIgnoreCase(shippingMethod);
            case 7:
                return FreightConstant.DHL_NAME.equalsIgnoreCase(shippingMethod);
            case 8:
                return FreightConstant.CIF_NAME.equalsIgnoreCase(shippingMethod);
            case 9:
                return shippingMethod.toLowerCase().contains(FreightConstant.CHINA_POST.toLowerCase()) && !(shippingMethod.toLowerCase().contains(FreightConstant.CHINA_POST_AIR_MAIL.toLowerCase()));
            case 10:
                return FreightConstant.SHIP_CHINA.equalsIgnoreCase(shippingMethod);
            case 11:
                return shippingMethod.toLowerCase().contains(FreightConstant.DDP_NAME.toLowerCase());
            case 12:
                return shippingMethod.toLowerCase().contains(FreightConstant.ECONOMIC.toLowerCase()) || FreightConstant.ECONOMIC.toLowerCase().contains(shippingMethod.toLowerCase());
            default:
                return false;
        }
    }


    /**
     * 初始化一个默认的运输方式和运费
     */
    private void initDefaultTrafficFreightUnit() {
        if (null == defaultTrafficFreightUnit || defaultTrafficFreightUnit.getFirstHeavy() <= 0) {
            defaultTrafficFreightUnit = new XmsTrafficFreightUnit();
            //  Description : 免邮运费固定为美国运费
            defaultTrafficFreightUnit.setFirstHeavy(1d);
            defaultTrafficFreightUnit.setFirstHeavyPrice(BigDecimal.valueOf(0.085d));
            defaultTrafficFreightUnit.setContinuedHeavyPrice(BigDecimal.valueOf(0.085d));
            defaultTrafficFreightUnit.setBigHeavyPrice(BigDecimal.valueOf(35d));
            defaultTrafficFreightUnit.setSplit(0);
            defaultTrafficFreightUnit.setDefaultWeightOfSpecial(1d);
            defaultTrafficFreightUnit.setContinuedHeavyPriceOfSpecial(BigDecimal.valueOf(22d));
            defaultTrafficFreightUnit.setBigHeavyPriceOfSpecial(BigDecimal.valueOf(60d));
        }

    }

    /**
     * @param countryId 国家
     * @return boolean
     * @despricetion: 判断国家是否支持economic methods
     */
    public boolean checkIsSupplyEconomicMethod(int countryId, int flag) {
        boolean isSupply = false;
        int[] countryArray = null;
        switch (flag) {
            case 1:
                countryArray = new int[]{94, 16, 21, 93, 34, 24, 4, 27, 32, 47, 31, 78, 18, 11, 12, 13, 57, 3, 25, 26, 5, 9, 19, 55, 56, 87, 111, 15, 50, 53, 54, 10, 33, 51, 2, 35, 36, 30, 79, 49, 23, 6, 20, 14, 22, 28};
                break;
            case 2:
                countryArray = new int[]{49, 23, 6, 20, 14, 22, 28};
                break;
            case 3:
                countryArray = new int[]{2, 36, 35};
                break;
            default:
                break;
        }
        if (null != countryArray) {
            isSupply = Arrays.stream(countryArray).filter(e -> e == countryId).count() > 0;
        }
        return isSupply;
    }


    public void forceRefresh() {
        synchronized (TrafficFreightUtils.class) {
            if (null != this.countriesList) {
                this.countriesList.clear();
            }
            if (null != this.countriesFilterList) {
                this.countriesFilterList.clear();
            }
            if (null != this.listOfFbaCountries) {
                this.listOfFbaCountries.clear();
            }
            if (null != this.freightUnitList) {
                this.freightUnitList.clear();
            }
            if (null != this.trafficFreightMap) {
                this.trafficFreightMap.clear();
            }
            if (null != this.freightFbaList) {
                this.freightFbaList.clear();
            }
            if (null != this.fbaFreightMap) {
                this.fbaFreightMap.clear();
            }
            if (null != this.freightPortList) {
                this.freightPortList.clear();
            }
            if (null != this.cifFreightUnitList) {
                this.cifFreightUnitList.clear();
            }
            initCountryAndFreightList();
        }

    }

    /**
     * 初始化国家和运费列表
     */
    public void initCountryAndFreightList() {

        initListOfFbaCountries();
        initFreightUnitList();
        initFreightFbaList();
        initFreightPortList();
        initCifFreightUnitList();

        initCountriesList();

    }


    private void initCifFreightUnitList() {
        synchronized (TrafficFreightUtils.class) {
            // 初始化CIF-FBA的港口列表
            if (CollectionUtil.isEmpty(this.cifFreightUnitList)) {
                QueryWrapper<XmsCifFreightUnit> queryWrapper = new QueryWrapper<>();
                this.cifFreightUnitList = this.cifFreightUnitMapper.selectList(queryWrapper);
            }
            if (CollectionUtil.isNotEmpty(this.cifFreightUnitList)) {
                this.cifFreightUnitList.forEach(e -> e.setRmbRate(this.exchangeRateUtils.getUsdToCnyRate()));
            }
        }
    }

    private void initCountriesList() {
        synchronized (TrafficFreightUtils.class) {
            // 初始化国家列表
            if (CollectionUtil.isEmpty(this.countriesList)) {
                this.countriesFilterList.clear();
                QueryWrapper<XmsListOfCountries> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(XmsListOfCountries::getDel, 0);
                this.countriesFilterList = this.listOfCountriesMapper.selectList(queryWrapper);
                if (CollectionUtil.isNotEmpty(this.countriesFilterList)) {
                    this.initFreightUnitList();
                    this.countriesList = this.countriesFilterList.stream().filter(e -> this.trafficFreightMap.containsKey(e.getId())).collect(Collectors.toList());
                    // 排序
                    this.countriesList.sort(Comparator.comparing(XmsListOfCountries::getEnglishNameOfCountry));
                }
            }
        }
    }

    private void initListOfFbaCountries() {
        synchronized (TrafficFreightUtils.class) {
            //FBA的国家城市列表
            if (CollectionUtil.isEmpty(this.listOfFbaCountries)) {
                QueryWrapper<XmsListOfFbaCountries> queryWrapper = new QueryWrapper<>();
                this.listOfFbaCountries = this.listOfFbaCountriesMapper.selectList(queryWrapper);
            }
        }
    }

    private void initFreightUnitList() {
        synchronized (TrafficFreightUtils.class) {
            // 初始化运费列表
            if (CollectionUtil.isEmpty(this.freightUnitList)) {
                LambdaQueryWrapper<XmsTrafficFreightUnit> lambdaQuery = Wrappers.lambdaQuery();
                lambdaQuery.eq(XmsTrafficFreightUnit::getDel, 0)
                        .or().eq(XmsTrafficFreightUnit::getDel, 2)
                        .or().eq(XmsTrafficFreightUnit::getDel, 3);
                // del = 0 or del = 2  or del = 3
                this.freightUnitList = this.trafficFreightUnitMapper.selectList(lambdaQuery);
            }
            // 初始化运费Map
            if (null == this.trafficFreightMap || this.trafficFreightMap.size() == 0) {
                if (CollectionUtil.isNotEmpty(this.freightUnitList)) {
                    this.trafficFreightMap = this.freightUnitList.stream().collect(Collectors.groupingBy(XmsTrafficFreightUnit::getCountryId));
                }
            }
        }
    }

    private void initFreightFbaList() {
        synchronized (TrafficFreightUtils.class) {
            // FBA运费列表
            if (CollectionUtil.isEmpty(this.freightFbaList)) {
                QueryWrapper<XmsTrafficFreightFba> queryWrapper = new QueryWrapper<>();
                this.freightFbaList = this.trafficFreightFbaMapper.selectList(queryWrapper);
            }
            if (CollectionUtil.isNotEmpty(this.freightFbaList)) {
                this.fbaFreightMap = this.freightFbaList.stream().collect(Collectors.groupingBy(XmsTrafficFreightFba::getCountryId));
            }
        }
    }

    private void initFreightPortList() {
        synchronized (TrafficFreightUtils.class) {
            // 港口运费列表
            if (CollectionUtil.isEmpty(this.freightPortList)) {
                QueryWrapper<XmsTrafficFreightPort> queryWrapper = new QueryWrapper<>();
                this.freightPortList = this.trafficFreightPortMapper.selectList(queryWrapper);
            }
        }
    }


    public List<XmsCifFreightUnit> getCifFreightUnitList() {
        initCifFreightUnitList();
        if (CollectionUtil.isNotEmpty(this.cifFreightUnitList)) {
            return BeanCopyUtil.deepListCopy(this.cifFreightUnitList);
        }
        return null;
    }


    public List<XmsListOfCountries> getCountriesList() {
        initCountriesList();
        if (CollectionUtil.isNotEmpty(this.countriesList)) {
            return BeanCopyUtil.deepListCopy(this.countriesList);
        }
        return null;
    }

    public List<XmsListOfCountries> getCountriesFilterList() {
        initCountriesList();
        if (CollectionUtil.isNotEmpty(this.countriesFilterList)) {
            return BeanCopyUtil.deepListCopy(this.countriesFilterList);
        }
        return null;
    }

    public List<XmsListOfFbaCountries> getListOfFbaCountries() {
        initListOfFbaCountries();
        if (CollectionUtil.isNotEmpty(this.listOfFbaCountries)) {
            return BeanCopyUtil.deepListCopy(this.listOfFbaCountries);
        }
        return null;
    }


    public List<XmsTrafficFreightUnit> getFreightUnitList() {
        initFreightUnitList();
        if (CollectionUtil.isNotEmpty(this.freightUnitList)) {
            return BeanCopyUtil.deepListCopy(this.freightUnitList);
        }
        return null;
    }

    public Map<Integer, List<XmsTrafficFreightUnit>> getTrafficFreightMap() {
        initFreightUnitList();
        Map<Integer, List<XmsTrafficFreightUnit>> tempMap = new HashMap<>();
        if (null != this.trafficFreightMap && this.trafficFreightMap.size() > 0) {
            this.trafficFreightMap.forEach((k, v) -> {
                tempMap.put(k, BeanCopyUtil.deepListCopy(v));
            });
        }
        return tempMap;
    }


    public Map<Integer, List<XmsTrafficFreightFba>> getFabFreightMap() {
        initFreightFbaList();
        Map<Integer, List<XmsTrafficFreightFba>> tempMap = new HashMap<>();
        if (null != this.fbaFreightMap && this.fbaFreightMap.size() > 0) {
            this.fbaFreightMap.forEach((k, v) -> {
                tempMap.put(k, BeanCopyUtil.deepListCopy(v));
            });
        }
        return tempMap;
    }


    public List<XmsTrafficFreightFba> getFreightFbaList() {
        initFreightFbaList();
        if (CollectionUtil.isNotEmpty(this.freightFbaList)) {
            return BeanCopyUtil.deepListCopy(this.freightFbaList);
        }
        return null;
    }

    public List<XmsTrafficFreightPort> getFreightPortList() {
        initFreightPortList();
        if (CollectionUtil.isNotEmpty(this.freightPortList)) {
            return BeanCopyUtil.deepListCopy(this.freightPortList);
        }
        return null;
    }


    public double getCurrRate() {
        return this.exchangeRateUtils.getUsdToCnyRate();
    }

}
