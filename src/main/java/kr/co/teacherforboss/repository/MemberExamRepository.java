package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.MemberExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberExamRepository extends JpaRepository<MemberExam, Long> {

}
