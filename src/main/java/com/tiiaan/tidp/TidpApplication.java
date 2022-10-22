package com.tiiaan.tidp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@MapperScan("com.tiiaan.tidp.mapper")
@SpringBootApplication
public class TidpApplication {

    public static void main(String[] args) {
        SpringApplication.run(TidpApplication.class, args);
    }

    //TODO 02/22 查循缓存、优惠券秒杀、分布式锁
    //TODO 02/23 消息队列、达人探店、点赞、排行榜、关注、附近商铺、用户签到、UV统计

}
