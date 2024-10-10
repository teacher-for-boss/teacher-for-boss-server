package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.TeacherSelectInfo;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherSelectInfoRepository extends JpaRepository<TeacherSelectInfo, Long> {

    Optional<TeacherSelectInfo> findByMemberIdAndStatus(Long memberId, Status status);
}
