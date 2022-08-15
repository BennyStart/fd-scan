package com.fardo.common.aspect.annotation;

import com.fardo.common.enums.OperTypeEnum;

import java.lang.annotation.*;

/**
 * 请求
 * 
 * @Author wangbt
 * @Date 20-12-16
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestAop {

    /**
     * 接口名称
     * @return
     */
    String value() default "";

    /**
     * 业务参数类型
     * @return
     */
    Class clazz() default String.class;

    /**
     * 操作类型
     * @return
     */
    OperTypeEnum operType() default OperTypeEnum.NULL;

    /**
     * 不记录日志的入参字段，多个逗号隔开，目前不支持入参实体里嵌套对象的字段
     * @return
     */
    String removeParamField() default "";

}
