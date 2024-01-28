package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.mapping.AgreementTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgreementTermRepository extends JpaRepository<AgreementTerm, Long> {
}
