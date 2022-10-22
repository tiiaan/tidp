package com.tiiaan.tidp.cache;

import com.tiiaan.tidp.entity.Shop;
import com.tiiaan.tidp.service.IShopService;
import com.tiiaan.tidp.service.impl.ShopServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class CacheClientTest {

    @Resource
    private CacheClient cacheClient;

    @Resource
    private IShopService shopService;

    @Test
    void setWithLogicalExpire() {
        //CacheClient cacheClient = new CacheClient();
        //ShopServiceImpl shopService = new ShopServiceImpl();
        Long id = 1L;
        Shop shop = shopService.getById(id);
        cacheClient.setWithLogicalExpire("cache:shop:1", shop, 30L, TimeUnit.SECONDS);
    }

}