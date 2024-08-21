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

    @Query(value = """
            SELECT * FROM teacher_select_info
            WHERE status = 'ACTIVE'
            ORDER BY select_count DESC, created_at DESC
            LIMIT 5;
    """, nativeQuery = true)
    List<TeacherSelectInfo> findHotTeachers(); //TODO: 일주일 안에서 계산
}
