package com.tiiaan.tidp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tiiaan.tidp.entity.UserInfo;
import com.tiiaan.tidp.mapper.UserInfoMapper;
import com.tiiaan.tidp.service.IUserInfoService;
import org.springframework.stereotype.Service;


@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

}
