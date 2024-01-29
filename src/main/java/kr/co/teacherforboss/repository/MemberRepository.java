package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.enums.LoginType;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByIdAndStatus(Long memberId, Status status);
    Optional<Member> findByEmailAndStatus(String email, Status status);
    Optional<Member> findByEmail(String email);
    Member findByPhoneAndStatus(String phone, Status status);
    boolean existsByEmailAndStatusAndLoginType(String email, Status status, LoginType loginType);
    Member findByEmailAndStatusAndLoginType(String email, Status status, LoginType loginType);
}
