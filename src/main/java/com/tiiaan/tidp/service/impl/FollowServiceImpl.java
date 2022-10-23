package com.tiiaan.tidp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tiiaan.tidp.dto.Result;
import com.tiiaan.tidp.dto.UserDTO;
import com.tiiaan.tidp.entity.Follow;
import com.tiiaan.tidp.mapper.FollowMapper;
import com.tiiaan.tidp.service.IFollowService;
import com.tiiaan.tidp.service.IUserService;
import com.tiiaan.tidp.utils.RedisConstants;
import com.tiiaan.tidp.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {


    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private IUserService userService;


    @Override
    public Result follow(Long followUserId, Boolean isFollow) {
        //1. 获取当前登录用户
        Long userId = UserHolder.getUser().getId();
        String key = RedisConstants.FOLLOW_KEY + userId;
        //2. 根据有没有关注判断到底是关注还是取关
        //3. 如果是关注，插入一条follow记录
        if (isFollow) {
            Follow follow = new Follow();
            follow.setUserId(userId);
            follow.setFollowUserId(followUserId);
            boolean isSaved = save(follow);
            if (isSaved) {
                stringRedisTemplate.opsForSet().add(key, followUserId.toString());
            }
        }
        //4. 如果是取关，删除fellow记录
        else {
            boolean isRemoved = remove(new QueryWrapper<Follow>().eq("user_id", userId).eq("follow_user_id", followUserId));
            if (isRemoved) {
                stringRedisTemplate.opsForSet().remove(key, followUserId.toString());
            }
        }
        return Result.ok();
    }



    @Override
    public Result isFollow(Long followUserId) {
        Long userId = UserHolder.getUser().getId();
        Integer count = query().eq("user_id", userId).eq("follow_user_id", followUserId).count();
        return Result.ok(count > 0);
    }



    @Override
    public Result followCommons(Long id) {
        //1. 获取当前登录用户
        Long userId = UserHolder.getUser().getId();
        //2. Redis 求交集
        Set<String> intersect = stringRedisTemplate.opsForSet().intersect(RedisConstants.FOLLOW_KEY + userId, RedisConstants.FOLLOW_KEY + id);
        //3. 如果没有交集直接返回空列表
        if (intersect == null || intersect.isEmpty()) {
            return Result.ok(Collections.emptyList());
        }
        //4. 将 Redis set 保存的 id 查询数据库获得 UserDTO
        List<Long> ids = intersect.stream().map(Long::valueOf).collect(Collectors.toList());
        List<UserDTO> commons = userService.listByIds(ids).stream().map(user -> BeanUtil.copyProperties(user, UserDTO.class)).collect(Collectors.toList());
        return Result.ok(commons);
    }


}
