package com.duu.matchPartner.config; /**
 * @author : duu
 * @data : 2023/10/31
 * @from ：https://github.com/0oHo0
 **/

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @BelongsProject: matchPartner-backend
 * @BelongsPackage: com.duu.matchPartner.config
 * @Author: duu
 * @CreateTime: 2023-10-31  22:54
 * @Description: TODO
 * @Version: 1.0
 */
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(RedisSerializer.string());
        return redisTemplate;
    }
}
