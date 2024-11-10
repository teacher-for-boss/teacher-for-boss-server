package kr.co.teacherforboss.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Slf4j
@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Bean(name = "taskScheduler")
    public TaskScheduler taskScheduler() {
        log.info("Custom TaskScheduler bean is being created.");
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);  // 스케줄링 작업에서 사용할 스레드 수
        scheduler.setThreadNamePrefix("SchedulerExecutor-");
        return scheduler;
    }
}

