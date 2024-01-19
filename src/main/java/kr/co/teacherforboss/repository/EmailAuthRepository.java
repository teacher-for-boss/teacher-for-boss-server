package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.EmailAuth;
import kr.co.teacherforboss.domain.enums.Purpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailAuthRepository extends JpaRepository<EmailAuth, Long> {
    boolean existsByIdAndEmailAndPurposeAndIsChecked(Long emailAuthId, String email, Purpose purpose, String isChecked);
}
