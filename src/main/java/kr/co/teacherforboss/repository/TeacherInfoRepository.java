package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.TeacherInfo;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherInfoRepository extends JpaRepository<TeacherInfo, Long> {
    List<TeacherInfo> findByMemberInAndStatus(List<Member> memberList, Status status);
}
