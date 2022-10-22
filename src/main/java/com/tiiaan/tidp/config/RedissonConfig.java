package com.tiiaan.tidp.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

/**
 * @author tiiaan Email:tiiaan.w@gmail.com
 * @version 0.0
 * description
 */

@Configuration
public class RedissonConfig {

    @Value(value = "${spring.redis.cluster.nodes}")
    private String clusterNodeAddress;

    @Bean
    public RedissonClient redissonClient() {
        String[] nodes = clusterNodeAddress.split(",");
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = "redis://" + nodes[i];
        }
        Config config = new Config();
        config.useClusterServers().addNodeAddress(nodes);
        return Redisson.create(config);
    }
}
