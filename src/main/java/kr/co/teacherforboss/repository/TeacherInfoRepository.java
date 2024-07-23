package kr.co.teacherforboss.repository;

import java.util.List;
import java.util.Optional;
import kr.co.teacherforboss.domain.TeacherInfo;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherInfoRepository extends JpaRepository<TeacherInfo, Long> {
    List<TeacherInfo> findAllByMemberIdInAndStatus(List<Long> memberIdCollect, Status status);
    Optional<TeacherInfo> findByMemberIdAndStatus(Long memberId, Status status);
}
