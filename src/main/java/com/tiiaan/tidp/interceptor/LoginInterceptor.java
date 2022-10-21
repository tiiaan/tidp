package com.tiiaan.tidp.interceptor;

import cn.hutool.core.bean.BeanUtil;
import com.tiiaan.tidp.dto.UserDTO;
import com.tiiaan.tidp.entity.User;
import com.tiiaan.tidp.utils.RedisConstants;
import com.tiiaan.tidp.utils.UserHolder;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author tiiaan Email:tiiaan.w@gmail.com
 * @version 0.0
 * description
 */

@Component
public class LoginInterceptor implements HandlerInterceptor {

    //@Resource
    private StringRedisTemplate stringRedisTemplate;
    //不能直接注入！


    public LoginInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.从ThreadLocal获取用户
        UserDTO user = UserHolder.getUser();
        //2.如果有就放行，如果没有就拦截
        if (user == null) {
            response.setStatus(401);
            return false;
        }
        return true;
    }


}
