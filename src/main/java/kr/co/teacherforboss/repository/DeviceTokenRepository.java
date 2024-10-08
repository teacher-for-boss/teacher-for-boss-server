package kr.co.teacherforboss.repository;

import java.util.List;
import java.util.Optional;
import kr.co.teacherforboss.domain.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    Optional<DeviceToken> findByMemberIdAndFcmToken(Long memberId, String fcmToken);
    List<DeviceToken> findAllByMemberId(Long memberId);
    List<DeviceToken> findAllByMemberIdIn(List<Long> memberIds);
}
