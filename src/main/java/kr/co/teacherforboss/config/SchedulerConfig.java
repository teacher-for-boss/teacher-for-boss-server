package kr.co.teacherforboss.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
@ConditionalOnProperty(prefix = "spring.scheduling", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SchedulerConfig {

    @Bean(name = "taskScheduler")
    public TaskScheduler taskScheduler() {
        System.out.println("Custom TaskScheduler bean is being created.");
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);  // 스케줄링 작업에서 사용할 스레드 수
        scheduler.setThreadNamePrefix("SchedulerExecutor-");
        return scheduler;
    }
}

