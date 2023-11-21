package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/* *
 * @packing com.sky.controller.admin
 * @author mtc
 * @date 16:25 11 21 16:25
 *
 */
@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api(tags = "通用接口")
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;


    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        // 接受的file
        log.info("文件上传,{}", file);
        //上传到alioss中去
        //生产id

        // 对原始文件重新命名
        try {
            // 原始文件名
            String originalFilename = file.getOriginalFilename();
            // 截取后缀
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 构建新的文件名称
            String ObjectName = UUID.randomUUID().toString() + extension;
            // 文件的请求路径
            String filepath = aliOssUtil.upload(file.getBytes(), ObjectName);
            log.info("上传的文件名称为,{}", filepath);
            return Result.success(filepath);
        } catch (IOException e) {
            log.error("文件上传失败，{}", e);
//            throw new RuntimeException(e);
        }
        
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }

}
