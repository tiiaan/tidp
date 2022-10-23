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

    //TODO 02/23 附近商铺、用户签到、UV统计
}
