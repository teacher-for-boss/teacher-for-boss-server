package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.TeacherInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherInfoRepository extends JpaRepository<TeacherInfo, Long> {

}
