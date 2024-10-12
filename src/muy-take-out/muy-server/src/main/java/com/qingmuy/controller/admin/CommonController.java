package com.qingmuy.controller.admin;

import com.qingmuy.constant.MessageConstant;
import com.qingmuy.result.Result;
import com.qingmuy.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    @Resource
    AliOssUtil aliOssUtil;

    /**
     * 上传图片文件
     * @param file 图片文件
     * @return 该图片在oss服务器的路径
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {

        // 原始文件名
        String originalFilename = file.getOriginalFilename();
        // 截取原始文件名的后缀
        String extension = null;
        if (originalFilename != null) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        // 构造新文件名称
        String objectName = UUID.randomUUID() + extension;

        // 文件的请求路径
        try {
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.info("文件上传失败：{1}", e);
        }

        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
