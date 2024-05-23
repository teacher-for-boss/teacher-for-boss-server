package kr.co.teacherforboss.service.memberService;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.MemberHandler;
import kr.co.teacherforboss.converter.MemberConverter;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.MemberSurvey;
import kr.co.teacherforboss.repository.MemberSurveyRepository;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.web.dto.MemberRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberCommandServiceImpl implements MemberCommandService{

    private final MemberSurveyRepository memberSurveyRepository;
    private final AuthCommandService authCommandService;

    @Override
    @Transactional
    public MemberSurvey saveSurvey(MemberRequestDTO.SurveyDTO request) {
        Member member = authCommandService.getMember();

        if (memberSurveyRepository.existsByMember(member))
            throw new MemberHandler(ErrorStatus.SURVEY_DUPLICATED);

        MemberSurvey memberSurvey = MemberConverter.toMemberSurvey(request, member);
        return memberSurveyRepository.save(memberSurvey);
    }
}
