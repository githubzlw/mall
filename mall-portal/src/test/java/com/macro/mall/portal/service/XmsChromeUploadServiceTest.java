package com.macro.mall.portal.service;

import com.macro.mall.portal.domain.XmsChromeUploadParam;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class XmsChromeUploadServiceTest {

    @Autowired
    private XmsChromeUploadService service;

    @Test
    void upload() {
        XmsChromeUploadParam param = new XmsChromeUploadParam();
        param.setUsername("luohao");
        param.setUrl("https://www.aliexpress.com/item/4000410940954.html?spm=a2g01.12617084.fdpcl001.2.27cekJEPkJEP54&gps-id=5547572&scm=1007.19201.130907.0&scm_id=1007.19201.130907.0&scm-url=1007.19201.130907.0&pvid=d27667eb-2391-40e8-ab5d-f8ee71663cc5");
        param.setTitle("Let'S Make 1set Silicone Baby Feeding Set Waterproof Spoon Non-Slip Feedings Silicone Bowl Tableware Baby Products Baby Plate");
        param.setMoq("3");
        param.setOff("10%");
        param.setPrice("11.66");
        param.setSku("112121");
        param.setImages("image1.gif");
        service.upload(param);
    }
}