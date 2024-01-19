package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.PhoneAuth;
import kr.co.teacherforboss.domain.enums.Purpose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhoneAuthRepository extends JpaRepository<PhoneAuth, Long> {
    boolean existsByIdAndPhoneAndPurposeAndIsChecked(Long phoneAuthId, String phone, Purpose purpose, String isChecked);
}
