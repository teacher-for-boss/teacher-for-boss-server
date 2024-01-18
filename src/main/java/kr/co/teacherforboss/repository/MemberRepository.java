package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmailAndStatus(String email, Status status);
}
