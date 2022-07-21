package com.tiiaan.tidp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tiiaan.tidp.entity.Follow;
import com.tiiaan.tidp.mapper.FollowMapper;
import com.tiiaan.tidp.service.IFollowService;
import org.springframework.stereotype.Service;


@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {

}
