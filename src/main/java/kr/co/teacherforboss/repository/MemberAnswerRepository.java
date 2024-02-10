package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.MemberAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberAnswerRepository extends JpaRepository<MemberAnswer, Long> {

}
