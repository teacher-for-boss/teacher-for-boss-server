package kr.co.teacherforboss.repository;

import java.time.LocalDateTime;
import java.util.List;
import kr.co.teacherforboss.domain.Notification;
import kr.co.teacherforboss.domain.enums.NotificationType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query(value = """
            SELECT *
            FROM notification
            WHERE type = :type
                AND created_at BETWEEN :startDate AND :endDate
            """, nativeQuery = true)
    List<Notification> findAllByTypeAndBetweenDate(NotificationType type, LocalDateTime startDate, LocalDateTime endDate);

    Slice<Notification> findByMemberIdOrderByIdDesc(Long memberId, Pageable pageable);
    Slice<Notification> findByMemberIdAndIdLessThanOrderByIdDesc(Long memberId, Long lastNotificationId, Pageable pageable);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
            UPDATE notification
            SET is_read = 'T'
            WHERE id IN :notificationIds
            """, nativeQuery = true)
    void readAllByIds(List<Long> notificationIds);
}
