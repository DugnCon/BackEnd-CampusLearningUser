package com.javaweb.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * Factory mặc định cho post (dùng @Primary)
     * **/
    @Bean
    @Primary
    public RedisConnectionFactory defaultRedisConnectionFactory() {
        return new LettuceConnectionFactory("localhost", 6379);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    /**
     * Cho comment
     * **/
    @Value("${redis.comment.host}")
    private String commentHost;

    @Value("${redis.comment.port}")
    private int commentPort;

    @Value("${redis.comment.password:}")
    private String commentPassword;

    @Bean(name = "commentRedisConnectionFactory")
    public LettuceConnectionFactory commentRedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(commentHost, commentPort);
        if (commentPassword != null && !commentPassword.isEmpty()) {
            config.setPassword(commentPassword);
        }
        return new LettuceConnectionFactory(config);
    }

    @Bean(name = "commentRedisTemplate")
    public RedisTemplate<String, String> commentRedisTemplate(
            @Qualifier("commentRedisConnectionFactory") LettuceConnectionFactory factory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    /**
     * Cho conversation
     * **/
    @Value("${redis.conversation.host}")
    private String conversationHost;

    @Value("${redis.conversation.port}")
    private int conversationPort;

    @Value("${redis.conversation.password:}")
    private String conversationPassword;

    @Bean(name = "conversationRedisConnectionFactory")
    public LettuceConnectionFactory conversationRedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(conversationHost, conversationPort);
        if (conversationPassword != null && !conversationPassword.isEmpty()) {
            config.setPassword(conversationPassword);
        }
        return new LettuceConnectionFactory(config);
    }

    @Bean(name = "conversationRedisTemplate")
    public RedisTemplate<String, Object> conversationRedisTemplate(
            @Qualifier("conversationRedisConnectionFactory") LettuceConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    /**
     * Cho message
     * */
    @Value("${redis.message.host}")
    private String messageHost;

    @Value("${redis.message.port}")
    private int messagePort;

    @Value("${redis.conversation.password:}")
    private String messagePassword;

    @Bean(name = "messageRedisConnectionFactory")
    public LettuceConnectionFactory messageRedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(conversationHost, conversationPort);
        if (conversationPassword != null && !conversationPassword.isEmpty()) {
            config.setPassword(conversationPassword);
        }
        return new LettuceConnectionFactory(config);
    }

    @Bean(name = "messageRedisTemplate")
    public RedisTemplate<String, Object> messageRedisTemplate(
            @Qualifier("messageRedisConnectionFactory") LettuceConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}
