package kr.co.teacherforboss.scheduler;

import kr.co.teacherforboss.config.RedisConfig;
import kr.co.teacherforboss.service.homeService.HomeQueryService;
import kr.co.teacherforboss.web.dto.HomeResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class HomeScheduler {

    private final HomeQueryService homeQueryService;
    private final CacheManager cacheManager;

    // 매 정각 및 30분
    @Scheduled(cron = "0 0,30 * * * ?")
    public HomeResponseDTO.GetHotPostsDTO updateHotPosts() {
        log.info("====== Refresh Hot Posts ======");
        cacheManager.getCache(RedisConfig.HOT_POST_CACHE_NAME).clear();
        return homeQueryService.getHotPosts();
    }

    // 매 정각 및 30분
    @Scheduled(cron = "0 0,30 * * * ?")
    public HomeResponseDTO.GetHotQuestionsDTO updateHotQuestions() {
        log.info("====== Refresh Hot Questions ======");
        cacheManager.getCache(RedisConfig.HOT_QUESTION_CACHE_NAME).clear();
        return homeQueryService.getHotQuestions();
    }

    // 매주 월요일 15시
    @Scheduled(cron = "0 0 15 ? * MON")
    public HomeResponseDTO.GetHotTeachersDTO updateHotTeachers() {
        log.info("====== Refresh Hot Teachers ======");
        cacheManager.getCache(RedisConfig.HOT_TEACHER_CACHE_NAME).clear();
        return homeQueryService.getHotTeachers();
    }

}
