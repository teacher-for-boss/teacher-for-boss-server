package kr.co.teacherforboss.repository;

import java.util.Optional;
import kr.co.teacherforboss.domain.AgreementTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgreementTermRepository extends JpaRepository<AgreementTerm, Long> {
    Optional<AgreementTerm> findByMemberId(Long memberId);
}
