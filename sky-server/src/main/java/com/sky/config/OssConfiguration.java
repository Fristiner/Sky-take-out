package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/* *
 * @packing com.sky.config
 * @author mtc
 * @date 16:47 11 21 16:47
 * 配置类，用于创建阿里ossuntils对象
 */
@Configuration
@Slf4j
public class OssConfiguration {

    // 对象直接注入
    @Bean
    @ConditionalOnMissingBean // 当没有这个在创建
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties) {
        log.info("开始创建阿里云文件上传工具类对象，{}", aliOssProperties);

        return new AliOssUtil(aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName());

    }

}
