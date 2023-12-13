package com.sky.annotation;

/* *
 * @packing com.sky.annotation
 * @author mtc
 * @date 9:38 11 19 9:38
 * 自定义注解，用于标识某个方法需要进行功能字段自动填充处理
 */

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    // 指定数据库操作类型：update,insert
    OperationType value();
}
