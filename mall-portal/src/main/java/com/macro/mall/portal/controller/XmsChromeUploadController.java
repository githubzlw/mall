package com.macro.mall.portal.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.binarywang.java.emoji.EmojiConverter;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.entity.XmsChromeUpload;
import com.macro.mall.portal.domain.XmsChromeUploadParam;
import com.macro.mall.portal.service.IXmsChromeUploadService;
import com.macro.mall.portal.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 谷歌扩展插件抓取内容上传Controller
 * Created by jack.luo on 2021/4/15.
 */
@Controller
@Api(tags = "XmsChromeUploadController", description = "谷歌扩展插件抓取内容上传")
@RequestMapping("/chrome")
public class XmsChromeUploadController {

    @Autowired
    private UmsMemberService umsMemberService;

    @Autowired
    private IXmsChromeUploadService xmsChromeUploadService;

    private EmojiConverter emojiConverter = EmojiConverter.getInstance();

    @ApiOperation("上传")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult upload(XmsChromeUploadParam xmsChromeUploadParam) {
        if(StrUtil.isNotEmpty(xmsChromeUploadParam.getUrl()) && xmsChromeUploadParam.getUrl().length() > 300){
            xmsChromeUploadParam.setUrl(xmsChromeUploadParam.getUrl().substring(0, 300));
        }
        if(StrUtil.isNotEmpty(xmsChromeUploadParam.getImages()) && xmsChromeUploadParam.getImages().length() > 2500){
            xmsChromeUploadParam.setUrl(xmsChromeUploadParam.getImages().substring(0, 2500));
        }
        // 处理productDescription和productDetail的emoji表情
        if(StrUtil.isNotEmpty(xmsChromeUploadParam.getProductDetail())){
            String alias = this.emojiConverter.toAlias(xmsChromeUploadParam.getProductDetail());
            xmsChromeUploadParam.setProductDetail(alias);
        }
        if(StrUtil.isNotEmpty(xmsChromeUploadParam.getProductDescription())){
            String alias = this.emojiConverter.toAlias(xmsChromeUploadParam.getProductDescription());
            xmsChromeUploadParam.setProductDescription(alias);
        }
        xmsChromeUploadService.upload(xmsChromeUploadParam);
        return CommonResult.success(null,"上传成功");
    }

    @ApiOperation("显示上传列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Page<XmsChromeUpload>> list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                          @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) {

        Page<XmsChromeUpload> result = xmsChromeUploadService.list(umsMemberService.getCurrentMember().getId(), pageNum, pageSize);
        return CommonResult.success(result);
    }

}
