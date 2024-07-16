package kr.co.teacherforboss.repository;

import java.util.Optional;
import kr.co.teacherforboss.domain.TeacherSelectInfo;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherSelectInfoRepository extends JpaRepository<TeacherSelectInfo, Long> {

    Optional<TeacherSelectInfo> findByMemberIdAndStatus(Long memberId, Status status);
}
