package kr.co.teacherforboss.repository;

import java.time.LocalDateTime;
import java.util.List;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.enums.LoginType;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByIdAndStatus(Long memberId, Status status);
    Optional<Member> findByEmailAndStatus(String email, Status status);
    Optional<Member> findByEmail(String email);
    Optional<Member> findByPhoneAndStatus(String phone, Status status);
    boolean existsByEmailAndLoginTypeAndStatus(String email, LoginType loginType, Status status);
    Optional<Member> findByEmailAndLoginTypeAndStatus(String email, LoginType loginType, Status status);
    boolean existsByEmailAndStatus(String email, Status status);
    boolean existsByPhoneAndStatus(String phone, Status status);
    boolean existsByNicknameAndStatus(String nickname, Status status);
    List<Member> findAllByIdInAndStatus(List<Long> idCollect, Status status);

    @Query(value = """
            SELECT *
            FROM member
            INNER JOIN (
                SELECT member_id
                FROM answer
                WHERE selected_at BETWEEN :startDate AND :endDate
                    AND status = 'ACTIVE'
                GROUP BY member_id
                ORDER BY COUNT(*) DESC
                LIMIT 5
            ) hot_teachers ON member.id = hot_teachers.member_id
            WHERE member.status = 'ACTIVE'
            """, nativeQuery = true)
    List<Member> findHotTeachers(LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = """
            SELECT m.*
            FROM member m
            INNER JOIN notification_setting ns ON m.id = ns.member_id
            WHERE ns.service = 'T'
                AND m.status = 'ACTIVE'
            """, nativeQuery = true)
    Page<Member> findAllAgreeServiceNotification(Pageable pageable);
}
