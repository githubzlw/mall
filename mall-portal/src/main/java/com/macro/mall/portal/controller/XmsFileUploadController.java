package com.macro.mall.portal.controller;

import com.alibaba.fastjson.JSONObject;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.portal.util.CSVUtils;
import com.macro.mall.portal.util.ExcelUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.controller
 * @date:2021-05-24
 */
@Api(tags = "FileUploadController", description = "文件上传相关接口")
@RestController
@RequestMapping("/fileUpload")
@Slf4j
public class XmsFileUploadController {

    @Autowired
    private ExcelUtils excelUtils;

    @Autowired
    private CSVUtils csvUtils;

    @Autowired
    private UmsMemberService umsMemberService;

    @ApiOperation("excel文件上传")
    @PostMapping("/excel")
    public CommonResult excel(@RequestParam("file") MultipartFile multipartFile) {
        Assert.notNull(multipartFile, "file null");

        UmsMember currentMember = this.umsMemberService.getCurrentMember();

        try {
            String name = multipartFile.getOriginalFilename();
            String substr = name.substring(name.lastIndexOf(".") + 1);
            if (substr.equalsIgnoreCase("xlsx") || substr.equalsIgnoreCase("xls")) {
                List<JSONObject> list = this.excelUtils.excelToList(null, multipartFile.getInputStream());
                return CommonResult.success(list);
            } else {
                return CommonResult.failed("File format abnormal");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("upload excel file:[{}],error:", multipartFile, e);
            return CommonResult.failed("upload failure");
        }
    }


    @ApiOperation("csv文件上传")
    @PostMapping("/csv")
    public CommonResult csv(@RequestParam("file") MultipartFile multipartFile) {
        Assert.notNull(multipartFile, "file null");

        UmsMember currentMember = this.umsMemberService.getCurrentMember();

        File file = null;
        try {
            String name = multipartFile.getOriginalFilename();
            String substr = name.substring(name.lastIndexOf(".") + 1);
            if (substr.equalsIgnoreCase("csv")) {
                file = CSVUtils.uploadFile(multipartFile);
                List<List<String>> userRoleLists = CSVUtils.readCSV(file.getPath());
                return CommonResult.success(userRoleLists);
            } else {
                return CommonResult.failed("File format abnormal");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("upload excel file:[{}],error:", multipartFile, e);
            return CommonResult.failed("upload failure");
        } finally {
            if (null != file) {
                file.delete();
            }
        }
    }


}
