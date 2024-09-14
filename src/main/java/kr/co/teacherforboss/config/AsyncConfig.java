package kr.co.teacherforboss.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Value("${spring.async.corepoolsize}")
    private int corepoolsize;

    @Value("${spring.async.maxpoolsize}")
    private int maxpoolsize;

    @Value("${spring.async.queuecapacity}")
    private int queuecapacity;

    @Override
    @Bean(name = "mailExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corepoolsize);
        executor.setMaxPoolSize(maxpoolsize);
        executor.setQueueCapacity(queuecapacity);
        executor.setThreadNamePrefix("Async MailExecutor-");
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return AsyncConfigurer.super.getAsyncUncaughtExceptionHandler();
    }
}