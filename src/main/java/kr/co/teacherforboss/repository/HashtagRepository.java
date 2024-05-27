package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.Hashtag;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    boolean existsByNameAndStatus(String name, Status status);
    Hashtag findByNameAndStatus(String name, Status status);
}
