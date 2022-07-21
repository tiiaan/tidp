package com.tiiaan.tidp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tiiaan.tidp.entity.Blog;
import com.tiiaan.tidp.mapper.BlogMapper;
import com.tiiaan.tidp.service.IBlogService;
import org.springframework.stereotype.Service;


@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {

}
