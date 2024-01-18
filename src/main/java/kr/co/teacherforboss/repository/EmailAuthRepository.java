package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.EmailAuth;
import kr.co.teacherforboss.domain.enums.Purpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailAuthRepository extends JpaRepository<EmailAuth, Long> {
    boolean existsByEmailAndPurposeAndIsChecked(String email, Purpose purpose, String isChecked);
}
