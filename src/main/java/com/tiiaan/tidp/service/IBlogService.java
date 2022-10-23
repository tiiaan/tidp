package com.tiiaan.tidp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tiiaan.tidp.dto.Result;
import com.tiiaan.tidp.entity.Blog;
import org.springframework.web.bind.annotation.PathVariable;


public interface IBlogService extends IService<Blog> {

    Result queryBlogById(Long id);

    Result likeBlog(Long id);

    Result queryBlogLikes(Long id);

    Result queryHotBlog(Integer current);
}
