package kr.co.teacherforboss.repository;

import java.util.Optional;
import java.util.List;

import kr.co.teacherforboss.domain.MemberExam;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberExamRepository extends JpaRepository<MemberExam, Long> {
    boolean existsByMemberIdAndExamId(Long memberId, Long examId);
    Optional<MemberExam> findByMemberIdAndExamIdAndStatus(Long memberId, Long examId, Status status);

    @Query(value = "select *"
            + "from member_exam me "
            + "where member_id = :memberId"
            + "    and exam_id = :examId "
            + "    and status = 'ACTIVE'"
            + "order by created_at desc "
            + "limit 1", nativeQuery = true)
    Optional<MemberExam> findRecentByMemberIdAndExamId(@Param("memberId") Long memberId, @Param("examId") Long examId);

    @Query(value = "select *"
            + "from ("
            + "    select *"
            + "    from member_exam"
            + "    where exam_id = :examId and status = 'ACTIVE'"
            + "    group by member_id"
            + "    having max(created_at)"
            + "     ) as me1_0 "
            + "order by me1_0.score desc, me1_0.time desc "
            + "limit 3", nativeQuery = true)
    List<MemberExam> findTop3ByExamId(@Param("examId") Long examId);

    @Query(value = "select *"
            + "from ("
            + "    select *"
            + "    from member_exam"
            + "    where exam_id = :examId and status = 'ACTIVE'"
            + "    group by member_id"
            + "    having max(created_at)"
            + "     ) as me1_0 "
            + "order by me1_0.score, me1_0.time "
            + "limit 1", nativeQuery = true)
    MemberExam findBottom1ByExamId(@Param("examId") Long examId);

    @Query(value = "select me1_0.ranking "
            + "from ("
            + "    select *, rank() over (order by score desc) as ranking"
            + "    from member_exam"
            + "    where status = 'ACTIVE'"
            + "     ) as me1_0 "
            + "where me1_0.id = :id", nativeQuery = true)
    Long findRankById(@Param("id") Long id);

    @Query(value = "select round(avg(me.score)) from member_exam me " +
            "where me.member_id = :memberId " +
            "and me.status = 'ACTIVE'", nativeQuery = true)
    Optional<Integer> findAllByMemberId(@Param("memberId") Long memberId);

    @Query(value = "select round(avg(me.score)) from member_exam me " +
            "where me.member_id <> :memberId " +
            "and me.status = 'ACTIVE'", nativeQuery = true)
    Optional<Integer> findByMemberIdNot(@Param("memberId") Long memberId);
}
