package kr.co.teacherforboss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.teacherforboss.domain.QuestionHashtag;

@Repository
public interface QuestionHashtagRepository extends JpaRepository<QuestionHashtag, Long> {
}
