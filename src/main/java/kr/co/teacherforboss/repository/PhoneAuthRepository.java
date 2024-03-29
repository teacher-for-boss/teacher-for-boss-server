package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.PhoneAuth;
import kr.co.teacherforboss.domain.enums.Purpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneAuthRepository extends JpaRepository<PhoneAuth, Long> {
    boolean existsByIdAndPhoneAndPurposeAndIsChecked(Long phoneAuthId, String phone, Purpose purpose, String isChecked);

    boolean existsByIdAndPurposeAndIsChecked(Long phoneAuthId, Purpose purpose, String isChecked);
}
