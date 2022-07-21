package com.tiiaan.tidp.interceptor;

import cn.hutool.core.bean.BeanUtil;
import com.tiiaan.tidp.dto.UserDTO;
import com.tiiaan.tidp.utils.RedisConstants;
import com.tiiaan.tidp.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author tiiaan Email:tiiaan.w@gmail.com
 * @version 0.0
 * description
 */

public class RefreshTokenInterceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;

    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1. 获取token
        String token = request.getHeader("authorization");
        //2. 拿着token从Redis中取出用户
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(token);
        //3. 如果查到了用户就保存到ThreadLocal中
        if (!userMap.isEmpty()) {
            UserDTO user = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);
            UserHolder.saveUser(user);
        }
        //4. 刷新token有效期
        stringRedisTemplate.expire(token, RedisConstants.LOGIN_USER_TTL, TimeUnit.MINUTES);
        //5. 全部放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }

}
