package com.fardo.common.aspect.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
/**
 * redis分布式锁
 */
public @interface RedisLock {

    /**
     * 是否加锁
     * @return
     */
	boolean isLock() default true;

    /**
     * 锁过期时间（毫秒）
     */
	int expireTime() default 3600000;

    /**
     * 循环次数，默认一次
     * @return
     */
//	int cycleIndex() default 1;

    /**
     * 线程休眠时间（毫秒）
     * @return
     */
    int sleepTime() default 0;

}

