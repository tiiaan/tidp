package com.tiiaan.tidp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tiiaan.tidp.dto.LoginFormDTO;
import com.tiiaan.tidp.dto.Result;
import com.tiiaan.tidp.dto.UserDTO;
import com.tiiaan.tidp.entity.User;
import com.tiiaan.tidp.mapper.UserMapper;
import com.tiiaan.tidp.service.IUserService;
import com.tiiaan.tidp.utils.RedisConstants;
import com.tiiaan.tidp.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result sendCode(String phone, HttpSession session) {
        //1.校验手机号
        if (RegexUtils.isPhoneInvalid(phone)) {
            return Result.fail("invalid phone");
        }
        //2.生成验证码
        String code = RandomUtil.randomNumbers(6);
        //3.保存验证码到session
        //session.setAttribute("code", code);
        //保存验证码到redis
        stringRedisTemplate.opsForValue().set(
                RedisConstants.LOGIN_CODE_KEY + phone, code,
                RedisConstants.LOGIN_CODE_TTL, TimeUnit.MINUTES);
        //4.发送验证码
        log.info("发送验证码 [{}]", code);
        return Result.ok();
    }


    @Override
    public Result login(LoginFormDTO loginFormDTO, HttpSession session) {
        //1.校验手机号
        String phone = loginFormDTO.getPhone();
        if (RegexUtils.isPhoneInvalid(phone)) {
            return Result.fail("invalid phone");
        }
        //2.校验验证码
        String code = loginFormDTO.getCode();
        //String cachedCode = (String) session.getAttribute("code");
        String cachedCode = stringRedisTemplate.opsForValue().get(RedisConstants.LOGIN_CODE_KEY + phone);
        if (cachedCode == null || !cachedCode.equals(code)) {
            return Result.fail("invalid code");
        }
        //3.验证通过根据手机号查用户
        User user = query().eq("phone", phone).one();
        //4.如果查不到就创建新用户
        if (user == null) {
            user = createUserWithPhone(phone);
        }
        //5.不管是创建新用户还是查出来的用户, 都要保存到session
        //session.setAttribute("user", BeanUtil.copyProperties(user, UserDTO.class));

        //5.改成保存用户到redis
        //生成token，因为此时没有JSESSIONID帮我们自动管理ID了，需要手动凭证
        String token = UUID.randomUUID().toString(true);
        //转成UserDTO，隐藏敏感信息
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        //以Map形式存到redis中
        Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
        String tokenKey = RedisConstants.LOGIN_USER_KEY + token;
        //保存到redis并设置过期时间
        stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);
        stringRedisTemplate.expire(tokenKey, RedisConstants.LOGIN_USER_TTL, TimeUnit.MINUTES);
        //返回token给客户端
        return Result.ok(token);
    }


    private User createUserWithPhone(String phone) {
        // 1.创建用户
        User user = new User();
        user.setPhone(phone);
        user.setNickName("user_" + RandomUtil.randomString(10));
        // 2.保存用户
        save(user);
        return user;
    }

}
