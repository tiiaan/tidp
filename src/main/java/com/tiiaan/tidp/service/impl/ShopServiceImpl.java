package com.tiiaan.tidp.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tiiaan.tidp.dto.Result;
import com.tiiaan.tidp.entity.Shop;
import com.tiiaan.tidp.mapper.ShopMapper;
import com.tiiaan.tidp.service.IShopService;
import com.tiiaan.tidp.utils.RedisConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public Result queryById(Long id) {
        Shop shop = queryByIdWithLock(id);
        if (shop == null) {
            return Result.fail("店铺不存在");
        }
        return Result.ok(shop);
    }



    private Shop queryByIdNonLock(Long id) {
        //1. 从Redis查询商铺缓存
        String shopKey = RedisConstants.CACHE_SHOP_KEY + id;
        String shopJson = stringRedisTemplate.opsForValue().get(shopKey);
        //2. 命中非空值直接返回
        if (shopJson != null && shopJson.length() != 0) {
            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
            return shop;
        }
        //3. 命中空值返回错误信息
        if (shopJson != null) {
            return null;
        }
        //4. 什么都未命中就查询数据库
        Shop shop = getById(id);
        //try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
        //5. 如果数据库中也不存在就缓存空值，避免缓存穿透，并返回错误信息
        if (shop == null) {
            stringRedisTemplate.opsForValue().set(shopKey, "", RedisConstants.CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }
        //6. 数据库中存在就写入Redis，并设置超时时间兜底
        stringRedisTemplate.opsForValue().set(shopKey, JSONUtil.toJsonStr(shop), RedisConstants.CACHE_SHOP_TTL, TimeUnit.MINUTES);
        //7. 返回
        return shop;
    }



    private Shop queryByIdWithLock(Long id) {
        //1. 从Redis查询商铺缓存
        String shopKey = RedisConstants.CACHE_SHOP_KEY + id;
        String shopJson = stringRedisTemplate.opsForValue().get(shopKey);
        //2. 命中非空值直接返回
        if (shopJson != null && shopJson.length() != 0) {
            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
            return shop;
        }
        //3. 命中空值返回错误信息
        if (shopJson != null) {
            return null;
        }
        //4. 什么都未命中就尝试获取锁
        String lockKey = RedisConstants.LOCK_SHOP_KEY + id;
        Shop shop;
        try {
            //5. 没获取到锁，休眠一段时间重试
            if (!tryLock(lockKey)) {
                try { TimeUnit.MILLISECONDS.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
                return queryByIdWithLock(id);
            }
            //6. 成功获取锁，查数据库
            shop = getById(id);
            try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
            //7. 如果数据库中也不存在就缓存空值，避免缓存穿透
            if (shop == null) {
                stringRedisTemplate.opsForValue().set(shopKey, "", RedisConstants.CACHE_NULL_TTL, TimeUnit.MINUTES);
                return null;
            }
            //8. 数据库中存在就写入Redis，并设置超时时间兜底
            stringRedisTemplate.opsForValue().set(shopKey, JSONUtil.toJsonStr(shop), RedisConstants.CACHE_SHOP_TTL, TimeUnit.MINUTES);
        } finally {
            tryUnLock(lockKey);
        }
        //7. 返回
        return shop;
    }



    private boolean tryLock(String lock) {
        Boolean locked = stringRedisTemplate.opsForValue().setIfAbsent(lock, "1", RedisConstants.LOCK_SHOP_TTL, TimeUnit.MINUTES);
        return BooleanUtil.isTrue(locked);
    }


    private void tryUnLock(String lock) {
        stringRedisTemplate.delete(lock);
    }




    @Override
    @Transactional
    public Result update(Shop shop) {
        Long id = shop.getId();
        if (id == null) {
            return Result.fail("商铺id不能为空");
        }
        //1. 先更新数数据库
        updateById(shop);
        //2. 再删除缓存
        String shopKey = RedisConstants.CACHE_SHOP_KEY + id;
        stringRedisTemplate.delete(shopKey);
        return Result.ok();
    }

}
