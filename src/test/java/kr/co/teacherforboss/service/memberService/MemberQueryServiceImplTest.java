package kr.co.teacherforboss.service.memberService;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.GeneralException;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.TeacherInfo;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.AnswerRepository;
import kr.co.teacherforboss.repository.MemberRepository;
import kr.co.teacherforboss.repository.TeacherInfoRepository;
import kr.co.teacherforboss.service.authService.AuthCommandServiceImpl;
import kr.co.teacherforboss.util.AuthTestUtil;
import kr.co.teacherforboss.util.MemberTestUtil;
import kr.co.teacherforboss.web.dto.MemberResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberQueryServiceImplTest {

    @InjectMocks @Spy
    private MemberQueryServiceImpl memberQueryService;
    @Mock
    private AuthCommandServiceImpl authCommandService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private TeacherInfoRepository teacherInfoRepository;
    @Mock
    private AnswerRepository answerRepository;
    @InjectMocks
    private AuthTestUtil authTestUtil;
    @InjectMocks
    private MemberTestUtil memberTestUtil;

    /*
    // TODO: 멤버 조회 테스트
     */
    @Test
    @DisplayName("프로필 조회 (성공) - ACTIVE 상태의 사용자 이름과 프로필 이미지 조회")
    void getMemberProfile() {
        // given
        Member member = authTestUtil.generateMemberDummy("email@gmail.com");
        TeacherInfo teacherInfo = memberTestUtil.generateTeacherInfoDummy(member);
        when(authCommandService.getMember())
                .thenReturn(member);
        when(memberRepository.findByIdAndStatus(any(), any()))
                .thenReturn(Optional.of(member));
        when(teacherInfoRepository.findByMemberIdAndStatus(any(), any()))
                .thenReturn(Optional.ofNullable(teacherInfo));
        when(answerRepository.countAllByMemberIdAndSelectedAtIsNotNullAndStatus(any(), any()))
                .thenReturn(100);

        // when
        MemberResponseDTO.GetMemberProfileDTO result = memberQueryService.getMemberProfile();

        // then
        assertThat(result.getNickname()).isEqualTo(member.getNickname());
        verify(memberRepository, times(1)).findByIdAndStatus(any(), any());
    }

    @Test
    @DisplayName("프로필 조회 (실패) - 존재하지 않는 사용자 이름과 프로필 이미지 조회")
    void failGetMemberProfile() {
        // given
        Member member = authTestUtil.generateMemberDummy("email@gmail.com"); // 조회하려는 사용자
        when(authCommandService.getMember())
                .thenReturn(member);
        when(memberRepository.findByIdAndStatus(member.getId(), Status.ACTIVE))
                .thenReturn(Optional.empty()); // 존재하지 않는 사용자

        // when
        GeneralException e = assertThrows(GeneralException.class
                , () -> memberQueryService.getMemberProfile());

        // then
        assertThat(e.getCode()).isEqualTo(ErrorStatus.MEMBER_NOT_FOUND);
    }
}