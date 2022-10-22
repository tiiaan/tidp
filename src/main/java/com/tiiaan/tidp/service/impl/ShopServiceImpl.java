package com.tiiaan.tidp.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tiiaan.tidp.dto.Result;
import com.tiiaan.tidp.entity.Shop;
import com.tiiaan.tidp.mapper.ShopMapper;
import com.tiiaan.tidp.service.IShopService;
import com.tiiaan.tidp.utils.RedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryById(Long id) {
        //1. 从Redis查询商铺缓存
        String shopKey = RedisConstants.CACHE_SHOP_KEY + id;
        String shopJson = stringRedisTemplate.opsForValue().get(shopKey);
        //2. 命中就直接返回
        if (shopJson != null && shopJson.length() != 0) {
            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
            return Result.ok(shop);
        }
        //3. 未命中就查询数据库
        Shop shop = getById(id);
        //4. 数据库不存在返回404
        if (shop == null) {
            return Result.fail("店铺不存在");
        }
        //5. 数据库中存在就写入Redis，并设置超时时间兜底
        stringRedisTemplate.opsForValue().set(shopKey, JSONUtil.toJsonStr(shop), RedisConstants.CACHE_SHOP_TTL, TimeUnit.MINUTES);
        //6. 返回
        return Result.ok(shop);
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
