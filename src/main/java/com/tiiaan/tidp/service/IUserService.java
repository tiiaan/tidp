package com.tiiaan.tidp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tiiaan.tidp.dto.LoginFormDTO;
import com.tiiaan.tidp.dto.Result;
import com.tiiaan.tidp.entity.User;

import javax.servlet.http.HttpSession;


public interface IUserService extends IService<User> {

    Result sendCode(String phone, HttpSession session);

    Result login(LoginFormDTO loginFormDTO, HttpSession session);
}
