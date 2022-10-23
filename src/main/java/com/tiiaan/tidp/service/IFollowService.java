package com.tiiaan.tidp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tiiaan.tidp.dto.Result;
import com.tiiaan.tidp.entity.Follow;


public interface IFollowService extends IService<Follow> {

    Result follow(Long followUserId, Boolean isFollow);

    Result isFollow(Long followUserId);

    Result followCommons(Long id);

}
