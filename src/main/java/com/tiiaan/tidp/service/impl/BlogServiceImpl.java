package com.tiiaan.tidp.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tiiaan.tidp.dto.Result;
import com.tiiaan.tidp.entity.Blog;
import com.tiiaan.tidp.entity.User;
import com.tiiaan.tidp.mapper.BlogMapper;
import com.tiiaan.tidp.service.IBlogService;
import com.tiiaan.tidp.service.IUserService;
import com.tiiaan.tidp.utils.RedisConstants;
import com.tiiaan.tidp.utils.SystemConstants;
import com.tiiaan.tidp.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {


    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private IUserService userService;


    @Override
    public Result queryBlogById(Long id) {
        Blog blog = getById(id);
        if (blog == null) {
            return Result.fail("blog不存在");
        }
        this.fillBlogInfos(blog);
        return Result.ok(blog);
    }


    private void fillBlogInfos(Blog blog) {
        this.fillBlogUser(blog);
        this.fillIsBlogLiked(blog);
    }


    private void fillBlogUser(Blog blog) {
        Long userId = blog.getUserId();
        User user = userService.getById(userId);
        blog.setName(user.getNickName());
        blog.setIcon(user.getIcon());
    }


    private void fillIsBlogLiked(Blog blog) {
        //1. 获取登录用户
        Long userId = UserHolder.getUser().getId();
        //2. 判断当前用户是否已经点赞
        String key = RedisConstants.BLOG_LIKED_KEY + blog.getId();
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(key, userId.toString());
        blog.setIsLike(Boolean.TRUE.equals(isMember));
    }


    @Override
    public Result likeBlog(Long id) {
        //1. 获取登录用户
        Long userId = UserHolder.getUser().getId();
        //2. 判断当前用户是否已经点赞
        String key = RedisConstants.BLOG_LIKED_KEY + id;
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(key, userId.toString());
        //3. 如果未点赞
        if (Boolean.FALSE.equals(isMember)) {
            //数据库点赞数+1
            boolean isSuccess = update().setSql("liked = liked + 1").eq("id", id).update();
            //保存用户到redis set
            if (isSuccess) {
                stringRedisTemplate.opsForSet().add(key, userId.toString());
            }
        } else { //4. 如果已点赞
            //数据库点赞数-1
            boolean isSuccess = update().setSql("liked = liked - 1").eq("id", id).update();
            //从redis set移除用户
            stringRedisTemplate.opsForSet().remove(key, userId.toString());
        }
        return Result.ok();
    }


    @Override
    public Result queryBlogLikes(Long id) {
        //String key = RedisConstants.BLOG_LIKED_KEY + id;
        //stringRedisTemplate.opsForZSet().
        return null;
    }


    @Override
    public Result queryHotBlog(Integer current) {
        // 根据用户查询
        Page<Blog> page = this.query()
                .orderByDesc("liked")
                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 获取当前页数据
        List<Blog> records = page.getRecords();
        // 查询用户
        records.forEach(this::fillBlogInfos);
        return Result.ok(records);
    }

}
