package kr.co.teacherforboss.repository;

import java.util.Optional;
import java.util.List;

import jakarta.persistence.QueryHint;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.MemberExam;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberExamRepository extends JpaRepository<MemberExam, Long> {
    @Query(value = "select me.* "
            + "from member_exam me, "
            + "    (select exam_id, MAX(created_at) as created_at "
            + "    from member_exam "
            + "    where member_id = :memberId "
            + "    group by exam_id"
            + "     ) as latest "
            + "where me.member_id = :memberId "
            + "and me.exam_id = latest.exam_id  "
            + "and me.created_at = latest.created_at "
            + "and me.status = 'ACTIVE'", nativeQuery = true)
    List<MemberExam> findAllRecentByMemberId(@Param("memberId") Long memberId);

    Optional<MemberExam> findByIdAndMemberAndStatus(Long memberExamId, Member member, Status status);

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

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    @Query(value = "select round(avg(me.score)) from member_exam me " +
            "where me.member_id = :memberId " +
            "and month(me.created_at) >= :first and month(me.created_at) <= :last " +
            "and me.status = 'ACTIVE'", nativeQuery = true)
    Optional<Integer> getAverageByMemberId(@Param("memberId") Long memberId, @Param("first") int first, @Param("last") int last);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    @Query(value = "select round(avg(me.score)) from member_exam me " +
            "left outer join (select member_id from member_exam where member_id = :memberId) m on me.member_id = m.member_id " +
            "where m.member_id is null " +
            "and month(me.created_at) >= :first and month(me.created_at) <= :last " +
            "and me.status = 'ACTIVE'", nativeQuery = true)
    Optional<Integer> getAverageByMemberIdNot(@Param("memberId") Long memberId, @Param("first") int first, @Param("last") int last);
}
