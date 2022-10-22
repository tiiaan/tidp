package com.tiiaan.tidp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tiiaan.tidp.dto.Result;
import com.tiiaan.tidp.entity.Shop;

public interface IShopService extends IService<Shop> {

    Result queryById(Long id);
}
