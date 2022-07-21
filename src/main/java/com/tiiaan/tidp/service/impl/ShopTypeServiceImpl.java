package com.tiiaan.tidp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tiiaan.tidp.entity.ShopType;
import com.tiiaan.tidp.mapper.ShopTypeMapper;
import com.tiiaan.tidp.service.IShopTypeService;
import org.springframework.stereotype.Service;


@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

}
