package kr.co.teacherforboss.repository;

import java.time.LocalDateTime;
import java.util.List;
import kr.co.teacherforboss.domain.Notification;
import kr.co.teacherforboss.domain.enums.NotificationType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query(value = """
            SELECT *
            FROM notification
            WHERE type = :type
                AND created_at BETWEEN :startDate AND :endDate
            """, nativeQuery = true)
    List<Notification> findAllByTypeAndBetweenDate(NotificationType type, LocalDateTime startDate, LocalDateTime endDate);

    Slice<Notification> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);
    Slice<Notification> findByMemberIdAndIdLessThanOrderByCreatedAtDesc(Long memberId, Long lastNotificationId, Pageable pageable);
}
