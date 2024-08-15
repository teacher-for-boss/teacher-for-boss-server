package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.Exchange;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, Long> {
    Optional<Exchange> findByIdAndStatus(Long exchangeId, Status status);
    Slice<Exchange> findSliceByMemberIdAndStatusOrderByCreatedAtDesc(Long memberId, Status status, PageRequest pageRequest);
    @Query(value = """
        SELECT * FROM exchange
        WHERE created_at < (SELECT created_at FROM exchange WHERE id = :lastExchangeId)
            AND member_id = :memberId
            AND status = 'ACTIVE'
        ORDER BY created_at DESC
    """, nativeQuery = true)
    Slice<Exchange> findSliceByIdLessthanAndMemberIdOrderByCreatedAtDesc(Long lastExchangeId, Long memberId, PageRequest pageRequest);
}
