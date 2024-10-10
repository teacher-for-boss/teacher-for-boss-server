package kr.co.teacherforboss.repository;

import java.util.List;
import java.util.Optional;
import kr.co.teacherforboss.domain.DeviceToken;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    Optional<DeviceToken> findByMemberIdAndFcmToken(Long memberId, String fcmToken);
    List<DeviceToken> findAllByMemberIdAndStatus(Long memberId, Status status);
    List<DeviceToken> findAllByMemberIdInAndStatus(List<Long> memberIds, Status status);
}
