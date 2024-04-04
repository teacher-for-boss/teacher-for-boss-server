package kr.co.teacherforboss.repository;

import java.util.Optional;
import java.util.List;

import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.MemberExam;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberExamRepository extends JpaRepository<MemberExam, Long> {
    @Query(value = "select me.* "
            + "from member_exam me "
            + "inner join exam e on me.exam_id = e.id "
            + "inner join (select e1.exam_category_id, e1.tag_id, max(me1.updated_at) as max_updated_at "
            + "            from member_exam me1 "
            + "            inner join exam e1 on me1.exam_id = e1.id "
            + "            where me1.member_id = :memberId and me1.status = 'ACTIVE' "
            + "            group by e1.exam_category_id, e1.tag_id) as latest "
            + "on e.exam_category_id = latest.exam_category_id "
            + "and e.tag_id = latest.tag_id "
            + "and me.updated_at = latest.max_updated_at " 
            + "where me.member_id = :memberId "
            + "and me.status = 'ACTIVE'", nativeQuery = true)
    List<MemberExam> findRecentByMemberIdGroupByCategoryAndTag(@Param("memberId") Long memberId);
    Optional<MemberExam> findByIdAndMemberAndStatus(Long memberExamId, Member member, Status status);
    boolean existsByMemberIdAndExamId(Long memberId, Long examId);

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
            "and month(me.created_at) between :first and :last " +
            "and me.status = 'ACTIVE'", nativeQuery = true)
    Optional<Integer> getAverageByMemberId(@Param("memberId") Long memberId, @Param("first") int first, @Param("last") int last);

    @Query(value = "select round(avg(me.score)) from member_exam me " +
            "where me.member_id <> :memberId " +
            "and month(me.created_at) between :first and :last " +
            "and me.status = 'ACTIVE'", nativeQuery = true)
    Optional<Integer> getAverageByMemberIdNot(@Param("memberId") Long memberId, @Param("first") int first, @Param("last") int last);
}
