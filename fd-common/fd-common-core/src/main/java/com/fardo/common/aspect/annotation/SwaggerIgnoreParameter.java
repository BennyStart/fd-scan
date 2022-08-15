package com.fardo.common.aspect.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SwaggerIgnoreParameter {
}
