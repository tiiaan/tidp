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
        //HttpSession session = request.getSession();
        //Object user = session.getAttribute("user");

        //前端每次发axios请求会带上token请求头
        String token = request.getHeader("authorization");
        String tokenKey = RedisConstants.LOGIN_USER_KEY + token;
        if (token == null || token.length() == 0) {
            return false;
        }
        //用token从redis中查出Map结构的用户信息
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(tokenKey);
        if (userMap.isEmpty()) {
            return false;
        }
        //将Map转为UserDTO
        UserDTO user = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);
        //将UserDTO存到ThreadLocal
        UserHolder.saveUser((UserDTO) user);
        //刷新redis token有效期，有效期内没有访问才会销毁，只要访问一次就重置一下
        stringRedisTemplate.expire(tokenKey, RedisConstants.LOGIN_USER_TTL, TimeUnit.MINUTES);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
