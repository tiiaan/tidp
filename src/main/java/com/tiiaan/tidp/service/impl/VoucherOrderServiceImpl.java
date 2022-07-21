package com.tiiaan.tidp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tiiaan.tidp.entity.VoucherOrder;
import com.tiiaan.tidp.mapper.VoucherOrderMapper;
import com.tiiaan.tidp.service.IVoucherOrderService;
import org.springframework.stereotype.Service;


@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

}
