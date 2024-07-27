package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.Exchange;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, Long> {
    Optional<Exchange> findByIdAndStatus(Long exchangeId, Status status);
}
