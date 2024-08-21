package kr.co.teacherforboss.repository;

import java.util.Optional;
import kr.co.teacherforboss.domain.QuestionTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionTicketRepository extends JpaRepository<QuestionTicket, Long> {

    Optional<QuestionTicket> findTop1ByMemberIdOrderByCreatedAtDesc(Long memberId);
}
