package com.sidchai.music.annotion.avoidRepeatableCommit;

import com.sidchai.music.constants.RedisConstants;
import com.sidchai.music.holder.RequestHolder;
import com.sidchai.music.result.R;
import com.sidchai.music.utils.IpUtils;
import com.sidchai.music.utils.RedisUtil;
import com.sidchai.music.utils.StringOpeUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 重复提交aop
 * @author sidchai
 * @since 2020-06-02
 */
@Aspect
@Component
@Slf4j
public class AvoidRepeatableCommitAspect {

    @Autowired
    private RedisUtil redisUtil;

    @Around("@annotation(com.sidchai.music.annotion.avoidRepeatableCommit.AvoidRepeatableCommit)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        HttpServletRequest request = RequestHolder.getRequest();

        String ip = IpUtils.getIpAddr(request);

        //获取注解
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        //目标类、方法
        String className = method.getDeclaringClass().getName();

        String name = method.getName();

        // 得到类名和方法
        String ipKey = String.format("%s#%s", className, name);

        // 转换成HashCode
        int hashCode = Math.abs(ipKey.hashCode());

        String key = String.format("%s:%s_%d", RedisConstants.AVOID_REPEATABLE_COMMIT, ip, hashCode);

        log.info("ipKey={},hashCode={},key={}", ipKey, hashCode, key);

        AvoidRepeatableCommit avoidRepeatableCommit = method.getAnnotation(AvoidRepeatableCommit.class);

        long timeout = avoidRepeatableCommit.timeout();

        String value = (String) redisUtil.get(key);

        if (StringOpeUtils.isNotBlank(value)) {
            return R.error().message("请勿重复提交表单");
        }

        // 设置过期时间
        redisUtil.setEx(key, StringOpeUtils.getUUID(), timeout, TimeUnit.MILLISECONDS);

        //执行方法
        Object object = point.proceed();
        return object;
    }
}
