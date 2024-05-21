package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.BusinessAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessAuthRepository extends JpaRepository<BusinessAuth, Long> {
    boolean existsByBusinessNumber(String businessNumber);
}
