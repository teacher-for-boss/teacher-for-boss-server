package kr.co.teacherforboss.service.examService;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.GeneralException;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.enums.ExamQuarter;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.MemberExamRepository;
import kr.co.teacherforboss.util.AuthTestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ExamQueryServiceImplTest {

    @Mock
    private MemberExamRepository memberExamRepository;
    @InjectMocks
    private AuthTestUtil authTestUtil;

    /*
    // TODO: 평균 조회
     */
    @DisplayName("평균 조회 (성공)")
    @Test
    void getAverage() {
        // given
        Member member = authTestUtil.generateMemberDummy("email@gmail.com");

        doReturn(Optional.of(Integer.class)).when(memberExamRepository).getAverageByMemberId(member.getId(), 1, 12);
        doReturn(Optional.of(Integer.class)).when(memberExamRepository).getAverageByMemberIdNot(member.getId(), 1, 12);

        // when
        Optional<Integer> memberScore = memberExamRepository.getAverageByMemberId(member.getId(), 1, 12);
        Optional<Integer> otherScore = memberExamRepository.getAverageByMemberIdNot(member.getId(), 1, 12);

        // then
        assertThat(memberScore).isNotNull();
        assertThat(otherScore).isNotNull();
    }
}