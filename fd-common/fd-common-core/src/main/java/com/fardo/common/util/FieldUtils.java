package com.fardo.common.util;

import io.swagger.annotations.ApiModelProperty;

import java.lang.reflect.Field;
import java.util.Objects;

public class FieldUtils {

    public static Field getDeclaredField(Class<?> cls, String name) {
        Field field = null;
        try {
            if (cls == null) {
                return null;
            }
            field = cls.getDeclaredField(name);
        } catch (NoSuchFieldException var4) {
            field = getDeclaredField(cls.getSuperclass(), name);
        } catch (SecurityException var5) {
            var5.printStackTrace();
        }

        return field;
    }

    public static boolean allFieldIsNull(Object o) {
        try {
            for (Field field : o.getClass().getDeclaredFields()) {
                //把私有属性公有化
                field.setAccessible(true);
                Object object = field.get(o);
                if (!Objects.isNull(object)) {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 获取ApiModelProperty值
     * @throws SecurityException
     */
    public static String getFieldApiModelPropertyValue(Object object){
        // 获取所有的字段
        Field[] fields = object.getClass().getDeclaredFields();
        StringBuilder data = new StringBuilder();
        for (Field field : fields) {
            // 判断字段注解是否存在
            boolean annotationPresent = field.isAnnotationPresent(ApiModelProperty.class);
            if (annotationPresent) {
                ApiModelProperty annotation = field.getAnnotation(ApiModelProperty.class);
                // 获取注解值
                String value = annotation.value();
                // 设置字段可访问， 否则无法访问private修饰的变量值
                field.setAccessible(true);
                try {
                    // 获取指定对象的当前字段的值
                    Object fieldVal = field.get(object);
                    if(fieldVal != null) {
                        data.append(value).append("：").append(fieldVal).append("，");
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        if(data.length()>0){
            data.deleteCharAt(data.length()-1);
        }
        return data.toString();
    }

}
