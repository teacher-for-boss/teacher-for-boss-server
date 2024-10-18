package kr.co.teacherforboss.config;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${cache.name.hot-post}")
    private String hotPostCacheName;

    @Value("${cache.name.hot-question}")
    private String hotQuestionCacheName;

    @Value("${cache.name.hot-teacher}")
    private String hotTeacherCacheName;

    @Value("${cache.key.hot-post}")
    private String hotPostCacheKey;

    @Value("${cache.key.hot-question}")
    private String hotQuestionCacheKey;

    @Value("${cache.key.hot-teacher}")
    private String hotTeacherCacheKey;

    public static String HOT_POST_CACHE_NAME;
    public static String HOT_QUESTION_CACHE_NAME;
    public static String HOT_TEACHER_CACHE_NAME;
    public static String HOT_POST_CACHE_KEY;
    public static String HOT_QUESTION_CACHE_KEY;
    public static String HOT_TEACHER_CACHE_KEY;

    @PostConstruct
    public void init() {
        HOT_POST_CACHE_NAME = hotPostCacheName;
        HOT_QUESTION_CACHE_NAME = hotQuestionCacheName;
        HOT_TEACHER_CACHE_NAME = hotTeacherCacheName;
        HOT_POST_CACHE_KEY = hotPostCacheKey;
        HOT_QUESTION_CACHE_KEY = hotQuestionCacheKey;
        HOT_TEACHER_CACHE_KEY = hotTeacherCacheKey;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean
    public RedisCacheManager contentCacheManager(RedisConnectionFactory cf) {
        RedisCacheConfiguration popularBoardCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())) // Value Serializer 변경
                .entryTtl(Duration.ofMinutes(3L)); // 캐시 수명 30분

        RedisCacheConfiguration popularTeacherCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())) // Value Serializer 변경
                .entryTtl(Duration.ofDays(7L)); // 캐시 수명 7일

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put(HOT_POST_CACHE_NAME, popularBoardCacheConfiguration);
        cacheConfigurations.put(HOT_QUESTION_CACHE_NAME, popularBoardCacheConfiguration);
        cacheConfigurations.put(HOT_TEACHER_CACHE_NAME, popularTeacherCacheConfiguration);

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(cf)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}

