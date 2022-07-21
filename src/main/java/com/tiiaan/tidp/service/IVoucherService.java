package com.tiiaan.tidp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tiiaan.tidp.dto.Result;
import com.tiiaan.tidp.entity.Voucher;


public interface IVoucherService extends IService<Voucher> {

    Result queryVoucherOfShop(Long shopId);

    void addSeckillVoucher(Voucher voucher);
}
