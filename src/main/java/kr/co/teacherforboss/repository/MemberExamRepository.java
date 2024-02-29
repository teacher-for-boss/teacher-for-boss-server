package kr.co.teacherforboss.repository;

import java.util.Optional;
import java.util.List;
import kr.co.teacherforboss.domain.MemberExam;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberExamRepository extends JpaRepository<MemberExam, Long> {
    boolean existsByMemberIdAndExamId(Long memberId, Long examId);

    Optional<MemberExam> findByMemberIdAndExamIdAndStatus(Long memberId, Long examId, Status status);
    List<MemberExam> findTop3ByExamIdAndStatus(Long examId, Status status, Sort sort);
    MemberExam findTop1ByExamIdAndStatus(Long examId, Status status, Sort sort);

    @Query(value = "select me.ranking "
            + "from ("
            + "    select *, rank() over (order by score desc) as ranking"
            + "    from member_exam"
            + "     ) as me "
            + "where me.id = :id", nativeQuery = true)
    Long findRankById(@Param("id") Long id);


}
