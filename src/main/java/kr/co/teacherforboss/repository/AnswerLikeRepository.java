package kr.co.teacherforboss.repository;

import java.util.List;
import kr.co.teacherforboss.domain.AnswerLike;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerLikeRepository extends JpaRepository<AnswerLike, Long> {
    List<AnswerLike> findAllByAnswerIdInAndStatus(List<Long> answerIdCollect, Status status);
    Optional<AnswerLike> findByAnswerIdAndMemberIdAndStatus(Long answerId, Long memberId, Status status);
}