package com.fardo.modules.system.aspect;

import com.fardo.common.aspect.annotation.RedisLock;
import com.fardo.common.util.RedisUtil;
import com.fardo.common.util.UUIDGenerator;
import com.fardo.modules.system.constant.CacheKeyConstants;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class LockAspect {

	private static final Logger logger = LoggerFactory.getLogger(LockAspect.class);

    @Autowired
    private RedisUtil redisUtil;

	@Pointcut("@annotation(com.fardo.common.aspect.annotation.RedisLock)")
	public void lockAspect() {

	}

	@Around("lockAspect()")
	public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = proceedingJoinPoint.getTarget().getClass().getDeclaredMethod(signature.getName(), signature.getMethod().getParameterTypes());
		Object result = null;
        RedisLock job = method.getAnnotation(RedisLock.class);
        if(job.isLock()) {
            // 添加redis分布式锁
            String targetName = proceedingJoinPoint.getTarget().getClass().getSimpleName(); //类名
            String methodName = method.getName(); //方法名
            int expireTime  = job.expireTime(); //过期时间
            int sleepTime = job.sleepTime(); //休眠时间
            String key = CacheKeyConstants.REDIS_LOCK_KEY_PREFIX + targetName + methodName;
            String requestId = UUIDGenerator.generate();
            while (true) {
                // 获取锁
                boolean isLockSuccess = redisUtil.lock(key, requestId, expireTime);
                if(isLockSuccess) {
                    try {
                        result = proceedingJoinPoint.proceed();
                    } finally {
                        // 释放锁
                        redisUtil.unLock(key, requestId);
                    }
                    break;
                }
                if(sleepTime == 0) {
                   break;
                }
                Thread.sleep(sleepTime);
            }
        } else {
            result = proceedingJoinPoint.proceed();
        }
        return result;
	}

}
