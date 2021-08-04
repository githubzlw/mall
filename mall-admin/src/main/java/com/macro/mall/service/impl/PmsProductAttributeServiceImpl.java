package com.macro.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.util.StringUtil;
import com.macro.mall.dao.PmsProductAttributeDao;
import com.macro.mall.dao.PmsProductAttributeValueDao;
import com.macro.mall.dto.PmsProductAttributeParam;
import com.macro.mall.dto.ProductAttrInfo;
import com.macro.mall.mapper.PmsProductAttributeCategoryMapper;
import com.macro.mall.mapper.PmsProductAttributeMapper;
import com.macro.mall.mapper.PmsProductAttributeValueMapper;
import com.macro.mall.mapper.PmsSkuStockMapper;
import com.macro.mall.model.*;
import com.macro.mall.service.PmsProductAttributeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品属性管理Service实现类
 * Created by macro on 2018/4/26.
 */
@Service
public class PmsProductAttributeServiceImpl implements PmsProductAttributeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PmsProductAttributeServiceImpl.class);
    @Autowired
    private PmsProductAttributeMapper productAttributeMapper;
    @Autowired
    private PmsProductAttributeCategoryMapper productAttributeCategoryMapper;
    @Autowired
    private PmsProductAttributeDao productAttributeDao;
    @Autowired
    private PmsProductAttributeValueDao productAttributeValueDao;

    @Autowired
    private PmsProductAttributeValueMapper productAttributeValueMapper;
    @Autowired
    private PmsSkuStockMapper skuStockMapper;

    @Override
    public List<PmsProductAttribute> getList(Long cid, Integer type, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        PmsProductAttributeExample example = new PmsProductAttributeExample();
        example.setOrderByClause("sort desc");
        example.createCriteria().andProductAttributeCategoryIdEqualTo(cid).andTypeEqualTo(type);
        return productAttributeMapper.selectByExample(example);
    }

    @Override
    public int create(PmsProductAttributeParam pmsProductAttributeParam) {
        PmsProductAttribute pmsProductAttribute = new PmsProductAttribute();
        BeanUtils.copyProperties(pmsProductAttributeParam, pmsProductAttribute);
        int count = productAttributeMapper.insertSelective(pmsProductAttribute);
        //新增商品属性以后需要更新商品属性分类数量
        PmsProductAttributeCategory pmsProductAttributeCategory = productAttributeCategoryMapper.selectByPrimaryKey(pmsProductAttribute.getProductAttributeCategoryId());
        if(pmsProductAttribute.getType()==0){
            pmsProductAttributeCategory.setAttributeCount(pmsProductAttributeCategory.getAttributeCount()+1);
        }else if(pmsProductAttribute.getType()==1){
            pmsProductAttributeCategory.setParamCount(pmsProductAttributeCategory.getParamCount()+1);
        }
        productAttributeCategoryMapper.updateByPrimaryKey(pmsProductAttributeCategory);
//        return count;
        int maxId = pmsProductAttribute.getId().intValue();
        return maxId;
    }

    @Override
    public int createType(String cType,Long productId,Long attributeCategoryId){

        int maxId = 0;
        if(StrUtil.isNotEmpty(cType)) {
            if (cType.indexOf(";") > 0) {
                String[] cTypeArry = cType.split(";");
                for (int i = 0; i < cTypeArry.length; i++) {

                    PmsProductAttributeParam productAttributeParam = new PmsProductAttributeParam();
                    productAttributeParam.setProductAttributeCategoryId(attributeCategoryId);
                    if (StringUtil.isNotEmpty(cTypeArry[i].split(":")[0])) {
                        productAttributeParam.setName(cTypeArry[i].split(":")[0]);
                    }
                    if (StringUtil.isNotEmpty(cTypeArry[i].split(":")[1])) {
                        productAttributeParam.setInputList(cTypeArry[i].split(":")[1]);
                    }
                    productAttributeParam.setHandAddStatus(1);
                    productAttributeParam.setType(0);
                    PmsProductAttribute pmsProductAttribute = new PmsProductAttribute();
                    BeanUtils.copyProperties(productAttributeParam, pmsProductAttribute);
                    productAttributeMapper.insertSelective(pmsProductAttribute);
                    maxId = pmsProductAttribute.getId().intValue();

                    //新增商品属性以后需要更新商品属性分类数量
                    PmsProductAttributeCategory pmsProductAttributeCategory = productAttributeCategoryMapper.selectByPrimaryKey(pmsProductAttribute.getProductAttributeCategoryId());
                    if(pmsProductAttribute.getType()==0){
                        pmsProductAttributeCategory.setAttributeCount(pmsProductAttributeCategory.getAttributeCount()+1);
                    }else if(pmsProductAttribute.getType()==1){
                        pmsProductAttributeCategory.setParamCount(pmsProductAttributeCategory.getParamCount()+1);
                    }
                    productAttributeCategoryMapper.updateByPrimaryKey(pmsProductAttributeCategory);

                    // 添加商品属性值信息
                    List<PmsProductAttributeValue> productAttributeValueList = new ArrayList<PmsProductAttributeValue>();
                    PmsProductAttributeValue pmsProductAttributeValue = new PmsProductAttributeValue();
                    pmsProductAttributeValue.setProductAttributeId(Long.valueOf(maxId));
                    pmsProductAttributeValue.setValue(cTypeArry[i].split(":")[1]);
                    productAttributeValueList.add(pmsProductAttributeValue);
                    //添加商品参数,添加自定义商品规格
                    relateAndInsertList(productAttributeValueDao, productAttributeValueList, productId);
                }
            } else {

                PmsProductAttributeParam productAttributeParam = new PmsProductAttributeParam();
                productAttributeParam.setProductAttributeCategoryId(attributeCategoryId);
                if (StringUtil.isNotEmpty(cType.split(":")[0])) {
                    productAttributeParam.setName(cType.split(":")[0]);
                }
                if (StringUtil.isNotEmpty(cType.split(":")[1])) {
                    productAttributeParam.setInputList(cType.split(":")[1]);
                }

                productAttributeParam.setHandAddStatus(1);
                productAttributeParam.setType(0);
                PmsProductAttribute pmsProductAttribute = new PmsProductAttribute();
                BeanUtils.copyProperties(productAttributeParam, pmsProductAttribute);
                productAttributeMapper.insertSelective(pmsProductAttribute);
                maxId = pmsProductAttribute.getId().intValue();
                //新增商品属性以后需要更新商品属性分类数量
                PmsProductAttributeCategory pmsProductAttributeCategory = productAttributeCategoryMapper.selectByPrimaryKey(pmsProductAttribute.getProductAttributeCategoryId());
                if(pmsProductAttribute.getType()==0){
                    pmsProductAttributeCategory.setAttributeCount(pmsProductAttributeCategory.getAttributeCount()+1);
                }else if(pmsProductAttribute.getType()==1){
                    pmsProductAttributeCategory.setParamCount(pmsProductAttributeCategory.getParamCount()+1);
                }
                productAttributeCategoryMapper.updateByPrimaryKey(pmsProductAttributeCategory);
                // 添加商品属性值信息
                List<PmsProductAttributeValue> productAttributeValueList = new ArrayList<PmsProductAttributeValue>();
                PmsProductAttributeValue pmsProductAttributeValue = new PmsProductAttributeValue();
                pmsProductAttributeValue.setProductAttributeId(Long.valueOf(maxId));
                pmsProductAttributeValue.setValue(cType.split(":")[1]);
                productAttributeValueList.add(pmsProductAttributeValue);
                //添加商品参数,添加自定义商品规格
                relateAndInsertList(productAttributeValueDao, productAttributeValueList, productId);
            }
        }

        return maxId;

    }

    @Override
    public int delType(Long productId,Long attributeCategoryId) {

        PmsProductAttributeExample example = new PmsProductAttributeExample();
        example.createCriteria().andProductAttributeCategoryIdEqualTo(attributeCategoryId);
        productAttributeMapper.deleteByExample(example);

        PmsProductAttributeValueExample valueExample = new PmsProductAttributeValueExample();
        valueExample.createCriteria().andProductIdEqualTo(productId);
        int count = productAttributeValueMapper.deleteByExample(valueExample);

        PmsProductAttributeCategory pmsProductAttributeCategory = productAttributeCategoryMapper.selectByPrimaryKey(attributeCategoryId);
        pmsProductAttributeCategory.setParamCount(0);
        pmsProductAttributeCategory.setAttributeCount(0);
        productAttributeCategoryMapper.updateByPrimaryKey(pmsProductAttributeCategory);

        //关联sku数据删除
        PmsSkuStockExample skuExample = new PmsSkuStockExample();
        skuExample.createCriteria().andProductIdEqualTo(productId);
        skuStockMapper.deleteByExample(skuExample);

        return count;
    }

    /**
     * 建立和插入关系表操作
     *
     * @param dao       可以操作的dao
     * @param dataList  要插入的数据
     * @param productId 建立关系的id
     */
    private void relateAndInsertList(Object dao, List dataList, Long productId) {
        try {
            if (CollectionUtils.isEmpty(dataList)) return;
            for (Object item : dataList) {
                Method setId = item.getClass().getMethod("setId", Long.class);
                setId.invoke(item, (Long) null);
                Method setProductId = item.getClass().getMethod("setProductId", Long.class);
                setProductId.invoke(item, productId);
            }
            Method insertList = dao.getClass().getMethod("insertList", List.class);
            insertList.invoke(dao, dataList);
        } catch (Exception e) {
            LOGGER.warn("创建产品出错:{}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public int update(Long id, PmsProductAttributeParam productAttributeParam) {
        PmsProductAttribute pmsProductAttribute = new PmsProductAttribute();
        pmsProductAttribute.setId(id);
        BeanUtils.copyProperties(productAttributeParam, pmsProductAttribute);
        return productAttributeMapper.updateByPrimaryKeySelective(pmsProductAttribute);
    }

    @Override
    public PmsProductAttribute getItem(Long id) {
        return productAttributeMapper.selectByPrimaryKey(id);
    }

    @Override
    public int delete(List<Long> ids) {
        //获取分类
        PmsProductAttribute pmsProductAttribute = productAttributeMapper.selectByPrimaryKey(ids.get(0));
        Integer type = pmsProductAttribute.getType();
        PmsProductAttributeCategory pmsProductAttributeCategory = productAttributeCategoryMapper.selectByPrimaryKey(pmsProductAttribute.getProductAttributeCategoryId());
        PmsProductAttributeExample example = new PmsProductAttributeExample();
        example.createCriteria().andIdIn(ids);
        int count = productAttributeMapper.deleteByExample(example);
        //删除完成后修改数量
        if(type==0){
            if(pmsProductAttributeCategory.getAttributeCount()>=count){
                pmsProductAttributeCategory.setAttributeCount(pmsProductAttributeCategory.getAttributeCount()-count);
            }else{
                pmsProductAttributeCategory.setAttributeCount(0);
            }
        }else if(type==1){
            if(pmsProductAttributeCategory.getParamCount()>=count){
                pmsProductAttributeCategory.setParamCount(pmsProductAttributeCategory.getParamCount()-count);
            }else{
                pmsProductAttributeCategory.setParamCount(0);
            }
        }
        productAttributeCategoryMapper.updateByPrimaryKey(pmsProductAttributeCategory);
        return count;
    }

    @Override
    public List<ProductAttrInfo> getProductAttrInfo(Long productCategoryId) {
        return productAttributeDao.getProductAttrInfo(productCategoryId);
    }
}
