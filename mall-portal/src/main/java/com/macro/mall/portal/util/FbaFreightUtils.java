package com.macro.mall.portal.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.macro.mall.entity.*;
import com.macro.mall.mapper.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: FBA运费计算
 * @date:2021-03-04
 */
@Component
public class FbaFreightUtils {

    @Autowired
    private ExchangeRateUtils exchangeRateUtils;

    @Autowired
    private XmsCifFreightUnitMapper cifFreightUnitMapper;
    @Autowired
    private XmsFbaFreightUnitMapper fbaFreightUnitMapper;
    @Autowired
    private XmsListOfCountriesMapper listOfCountriesMapper;
    @Autowired
    private XmsListOfFbaCountriesMapper listOfFbaCountriesMapper;
    @Autowired
    private XmsListOfHomeDeliveryCountriesMapper listOfHomeDeliveryCountriesMapper;


    private List<XmsListOfCountries> countriesList = new ArrayList<>();

    private List<XmsFbaFreightUnit> fbaFreightUnitList = new ArrayList<>();
    private Map<Integer, List<XmsFbaFreightUnit>> fbaFreightUnitMap = new HashMap<>();

    private List<XmsListOfFbaCountries> fbaCountriesList = new ArrayList<>();

    private List<XmsCifFreightUnit> cifFreightUnitList = new ArrayList<>();

    private List<XmsListOfHomeDeliveryCountries> homeDeliveryCountries = new ArrayList<>();


    /**
     * 计算各种方式的国家的运费(1 进FBA, 2 进客户门点)
     *
     * @param fbaParam
     * @return
     */
    public List<XmsFbaFreightUnit> getProductShippingCost(XmsFbaFreightUnit fbaParam) {
        checkAndInit();

        double currencyRate = exchangeRateUtils.getUsdToCnyRate();
        // 过滤 国家和ZIP
        fbaParam.setRbmRate(currencyRate);

        List<XmsFbaFreightUnit> list = new ArrayList<>();
        List<XmsFbaFreightUnit> tmList = new ArrayList<>();
        if (fbaParam.getCountryId() > 0 && this.fbaFreightUnitMap.containsKey(fbaParam.getCountryId())) {
            list = this.fbaFreightUnitMap.get(fbaParam.getCountryId());
            // 如果选择了modeOfTransport,则进行过滤
            if (CollectionUtil.isNotEmpty(list) && null != fbaParam.getModeOfTransport() && fbaParam.getModeOfTransport() > 0) {
                list = list.stream().filter(e -> e.getModeOfTransport().equals(fbaParam.getModeOfTransport())).collect(Collectors.toList());
            }
        }


        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(e -> {

                e.setRbmRate(currencyRate);
                // 保留原始数据
                e.setWeight(fbaParam.getWeight());
                e.setVolume(fbaParam.getVolume());
                // 计算运费结果
                if (e.getTypeOfMode() == 1) {
                    setFbaTotalPrice(e, fbaParam, currencyRate);
                } else if (e.getTypeOfMode() == 2) {
                    double totalPrice = e.calculateTotalPrice(Math.max(Math.ceil(fbaParam.getVolume()), fbaParam.getVolume()), currencyRate);
                    e.setTotalPrice(totalPrice);
                }
            });


            // 产品单页仅显示FBA数据
            if (fbaParam.getProductFlag() == 1) {
                // 如果是美国，则进行zip过滤判断
                if (36 == fbaParam.getCountryId()) {
                    if (StrUtil.isNotBlank(fbaParam.getZipCode())) {
                        final String beginCode = fbaParam.getZipCode().substring(0, 1);
                        list = list.stream().filter(e -> e.getTypeOfMode() == 1 && e.getZipCode().contains("," + beginCode + ",")).collect(Collectors.toList());
                    } else {
                        list = list.stream().filter(e -> e.getTypeOfMode() == 1).collect(Collectors.toList());
                    }
                } else {
                    list = list.stream().filter(e -> e.getTypeOfMode() == 1).collect(Collectors.toList());
                }
                // 做价格排序
                if (CollectionUtil.isNotEmpty(list)) {
                    list.sort(Comparator.comparing(XmsFbaFreightUnit::getTotalPrice));
                }
            } else {
                // 仅美国进行zip过滤判断
                if (36 == fbaParam.getCountryId()) {
                    final String beginCode;
                    if (StrUtil.isNotEmpty(fbaParam.getZipCode())) {
                        beginCode = fbaParam.getZipCode().substring(0, 1);
                    } else {
                        beginCode = "0";
                    }
                    list = list.stream().filter(e -> e.getZipCode().contains("," + beginCode + ",")).collect(Collectors.toList());
                }
            }
        }

        // 进行类别分组
        if (CollectionUtil.isNotEmpty(list)) {
            Map<Integer, List<XmsFbaFreightUnit>> listMap = list.stream().collect(Collectors.groupingBy(XmsFbaFreightUnit::getModeOfTransport));

            listMap.forEach((k, v) -> {
                v.sort(Comparator.comparing(XmsFbaFreightUnit::getTotalPrice));
                // 复制数据
                XmsFbaFreightUnit clFba = new XmsFbaFreightUnit();
                BeanUtils.copyProperties(v.get(0), clFba);
                tmList.add(clFba);
            });
            listMap.clear();
            list.clear();
        }
        if (CollectionUtil.isNotEmpty(tmList)) {
            if (StrUtil.isNotBlank(fbaParam.getZipCode())) {
                tmList.forEach(e -> e.setZipCode(fbaParam.getZipCode()));
            }
            return tmList;
        }
        return list;
    }


    /**
     * 根据国家，计算运费
     *
     * @param fbaFreightUnit
     * @return
     */
    public void getTransportTotalPrice(XmsFbaFreightUnit fbaFreightUnit, double currencyRate) {
        checkAndInit();
        fbaFreightUnit.setRbmRate(this.exchangeRateUtils.getUsdToCnyRate());
        if (this.fbaFreightUnitMap.containsKey(fbaFreightUnit.getCountryId())) {
            filterTransportAndCalculate(fbaFreightUnit, currencyRate);
        }
    }


    /**
     * 购物车页面获取总价格逻辑
     *
     * @param odlFba
     */
    public void filterAndGetTotalPrice(XmsFbaFreightUnit odlFba, double currencyRate) {
        checkAndInit();
        if (this.fbaFreightUnitMap.containsKey(odlFba.getCountryId())) {
            if (odlFba.getId() > 0) {
                XmsFbaFreightUnit anElse = this.fbaFreightUnitMap.get(odlFba.getCountryId()).stream().filter(e -> odlFba.getId() == e.getId()).findFirst().orElse(null);
                if (null != anElse) {
                    setFbaTotalPrice(anElse, odlFba, currencyRate);
                }
            } else if (StrUtil.isNotBlank(odlFba.getZipCode())) {
                this.fbaFreightUnitMap.get(odlFba.getCountryId()).stream().filter(e -> odlFba.getZipCode().equalsIgnoreCase(e.getZipCode())).findFirst().ifPresent(anElse -> setFbaTotalPrice(anElse, odlFba, currencyRate));
            }
        }
    }


    /**
     * 根据信息计算价格
     *
     * @param fbaFreightUnit
     */
    private void filterTransportAndCalculate(XmsFbaFreightUnit fbaFreightUnit, double currencyRate) {
        if (StrUtil.isNotBlank(fbaFreightUnit.getZipCode())) {

            // 匹配 fba类型
            List<XmsFbaFreightUnit> list = this.fbaFreightUnitMap.get(fbaFreightUnit.getCountryId()).stream().filter(e -> e.getModeOfTransport() == fbaFreightUnit.getModeOfTransport()).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(list)) {
                // 判断 fba类型: 1 进FBA, 2 进客户门点
                if (fbaFreightUnit.getModeOfTransport() == 1 || fbaFreightUnit.getModeOfTransport() == 2) {
                    calculateFbaPrice(list, fbaFreightUnit, currencyRate);
                }
            }
        }
    }


    /**
     * fba类型: 1 进FBA, 2 进客户门点
     * 运输方式: 1 FBA方式, 2 传统海运
     *
     * @param list
     */
    private void calculateFbaPrice(List<XmsFbaFreightUnit> list, XmsFbaFreightUnit fbaParam, double currencyRate) {
        String beginCode = fbaParam.getZipCode().substring(0, 1);
        XmsFbaFreightUnit fbaFreightUnit = list.stream().filter(e -> e.getTypeOfMode() == 1 && e.getZipCode().contains("," + beginCode + ",")).findFirst().orElse(null);

        double fbaPrice = 0;
        double trdPrice = 0;

        if (null != fbaFreightUnit) {
            setFbaTotalPrice(fbaFreightUnit, fbaParam, currencyRate);
        }
        fbaFreightUnit = list.stream().filter(e -> e.getTypeOfMode() == 2 && e.getZipCode().contains("," + beginCode + ",")).findFirst().orElse(null);
        if (null != fbaFreightUnit) {
            // 按照体积来
            trdPrice = fbaFreightUnit.calculateTotalPrice(fbaParam.getVolume(), currencyRate);
        }

        if (null != fbaFreightUnit) {
            copyBeanDada(fbaFreightUnit, fbaParam);
            if (trdPrice < fbaPrice) {
                fbaParam.setTotalPrice(BigDecimalUtil.truncateDouble(trdPrice, 2));
            } else {
                fbaParam.setTotalPrice(BigDecimalUtil.truncateDouble(fbaPrice, 2));
            }
        }
    }


    /**
     * fba类型: 3 到港口
     * 运输方式: 3传统 CIF
     *
     * @param list
     */
    private void calculatePortPrice(List<XmsFbaFreightUnit> list, XmsFbaFreightUnit fbaParam, double currencyRate) {
        String beginCode = fbaParam.getZipCode().substring(0, 1);
        XmsFbaFreightUnit fbaFreightUnit = list.stream().filter(e -> e.getModeOfTransport() == 3 && e.getZipCode().contains("," + beginCode + ",")).findFirst().orElse(null);
        if (null != fbaFreightUnit) {
            setFbaTotalPrice(fbaFreightUnit, fbaParam, currencyRate);
        }
    }


    /**
     * 设置总价
     *
     * @param setFba
     * @param fbaParam
     */
    private void setFbaTotalPrice(XmsFbaFreightUnit setFba, XmsFbaFreightUnit fbaParam, double currencyRate) {
        copyBeanDada(setFba, fbaParam);
        getFreightByVolumeWeight(setFba, fbaParam, currencyRate);
    }

    private void copyBeanDada(XmsFbaFreightUnit setFba, XmsFbaFreightUnit fbaParam) {

        fbaParam.setCountryId(setFba.getCountryId());
        fbaParam.setModeOfTransport(setFba.getModeOfTransport());
        fbaParam.setTypeOfMode(setFba.getTypeOfMode());
        fbaParam.setWeightPrice(setFba.getWeightPrice());
        fbaParam.setVolumePrice(setFba.getVolumePrice());

        fbaParam.setDeliveryTime(setFba.getDeliveryTime());

        fbaParam.setDocumentFee(setFba.getDocumentFee());
        fbaParam.setHandlingFee(setFba.getHandlingFee());
        fbaParam.setClearanceFee(setFba.getClearanceFee());
        fbaParam.setAmsFree(setFba.getAmsFree());
        fbaParam.setIsfFree(setFba.getIsfFree());
        fbaParam.setCustomsCharge(setFba.getCustomsCharge());
        fbaParam.setLocalTruckingFee(setFba.getLocalTruckingFee());
        fbaParam.setSeaFreight(setFba.getSeaFreight());
        fbaParam.setEndInTowing(setFba.getEndInTowing());
        fbaParam.setStorageCharges(setFba.getStorageCharges());

    }

    /**
     * 根据体积和重量获取运费
     *
     * @param fbaFreightUnit
     * @return
     */
    private void getFreightByVolumeWeight(XmsFbaFreightUnit fbaFreightUnit, XmsFbaFreightUnit odlFba, double currencyRate) {
        int vlNum = (int) Math.floor(1d / odlFba.getVolume());
        double vlMaxWeight = 200;
        /**
         * 当体积<1时
         * 如果 roundup(1/体积，0)*重量>=200KG 运费=12*实际重量/6.5
         * 如果 roundup(1/体积，0)*重量<200KG 运费=(200KG*12/6.5)/round(1/体积，0)
         * 当体积>1时
         * 如果实际重量>= 取整(体积,0)*200KG 运费=12*实际重量/6.5
         * 如果实际重量<   取整(体积,0)*200KG  运费=体积*200kg*12/6.5
         */
        double tempPrice = 0;
        if (vlNum > 1) {
            if (odlFba.getWeight() * vlNum < vlMaxWeight) {
                //实际体积转换的重量
                tempPrice = vlMaxWeight * fbaFreightUnit.getWeightPrice() / vlNum / this.exchangeRateUtils.getUsdToCnyRate();
            } else {
                tempPrice = odlFba.getWeight() * fbaFreightUnit.getWeightPrice() / this.exchangeRateUtils.getUsdToCnyRate();
            }
        } else {
            double totalVolWeight = odlFba.getVolume() * vlMaxWeight;
            tempPrice = Math.max(totalVolWeight, odlFba.getWeight()) * fbaFreightUnit.getWeightPrice() / this.exchangeRateUtils.getUsdToCnyRate();
        }

        fbaFreightUnit.setTotalPrice(DoubleUtil.mul(tempPrice, currencyRate, 2));
        odlFba.setTotalPrice(fbaFreightUnit.getTotalPrice());

    }


    public Map<Integer, List<XmsFbaFreightUnit>> getFbaFreightUnitMap() {
        initFbaFreightUnit();
        return this.fbaFreightUnitMap;
    }


    public List<XmsListOfHomeDeliveryCountries> getHomeDeliveryCountries() {
        initHomeDeliveryCountries();
        return this.homeDeliveryCountries;
    }


    private void checkAndInit() {

        initCountriesList();
        initListOfFbaCountries();

        initFbaFreightUnit();
        initCifFreightUnit();

        initHomeDeliveryCountries();
    }


    /**
     * 强制刷新
     */
    public void forceRefresh() {

        synchronized (FbaFreightUtils.class) {
            if (null != this.countriesList) {
                this.countriesList.clear();
            }
            if (null != this.fbaCountriesList) {
                this.fbaCountriesList.clear();
            }


            if (null != this.fbaFreightUnitList) {
                this.fbaFreightUnitList.clear();
            }
            if (null != this.fbaFreightUnitMap) {
                this.fbaFreightUnitMap.clear();
            }

            if (null != this.cifFreightUnitList) {
                this.cifFreightUnitList.clear();
            }

            if (null != this.homeDeliveryCountries) {
                this.homeDeliveryCountries.clear();
            }


            checkAndInit();
        }

    }


    private void initCountriesList() {
        synchronized (FbaFreightUtils.class) {
            if (CollectionUtil.isEmpty(this.countriesList)) {
                QueryWrapper<XmsListOfCountries> queryWrapper = new QueryWrapper<>();
                this.countriesList = this.listOfCountriesMapper.selectList(queryWrapper);
            }
        }

    }

    private void initFbaFreightUnit() {
        synchronized (FbaFreightUtils.class) {
            if (CollectionUtil.isEmpty(this.fbaFreightUnitList)) {
                QueryWrapper<XmsFbaFreightUnit> queryWrapper = new QueryWrapper<>();
                this.fbaFreightUnitList = this.fbaFreightUnitMapper.selectList(queryWrapper);
            }
            if (null == this.fbaFreightUnitMap || this.fbaFreightUnitMap.size() == 0) {
                this.fbaFreightUnitMap = this.fbaFreightUnitList.stream().collect(Collectors.groupingBy(XmsFbaFreightUnit::getCountryId));
            }
        }

    }

    private void initListOfFbaCountries() {
        synchronized (FbaFreightUtils.class) {
            if (CollectionUtil.isEmpty(this.fbaCountriesList)) {
                QueryWrapper<XmsListOfFbaCountries> queryWrapper = new QueryWrapper<>();
                this.fbaCountriesList = this.listOfFbaCountriesMapper.selectList(queryWrapper);
            }
        }
    }

    private void initCifFreightUnit() {
        synchronized (FbaFreightUtils.class) {
            if (CollectionUtil.isEmpty(this.cifFreightUnitList)) {
                QueryWrapper<XmsCifFreightUnit> queryWrapper = new QueryWrapper<>();
                this.cifFreightUnitList = this.cifFreightUnitMapper.selectList(queryWrapper);
            }
        }
    }


    private void initHomeDeliveryCountries() {
        synchronized (FbaFreightUtils.class) {
            if (CollectionUtil.isEmpty(this.homeDeliveryCountries)) {
                QueryWrapper<XmsListOfHomeDeliveryCountries> queryWrapper = new QueryWrapper<>();
                this.homeDeliveryCountries = this.listOfHomeDeliveryCountriesMapper.selectList(queryWrapper);
            }
        }
    }


}