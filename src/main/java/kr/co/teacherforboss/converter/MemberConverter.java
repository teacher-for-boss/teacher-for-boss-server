package kr.co.teacherforboss.converter;

import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.MemberSurvey;
import kr.co.teacherforboss.domain.enums.Survey;
import kr.co.teacherforboss.web.dto.MemberRequestDTO;
import kr.co.teacherforboss.web.dto.MemberResponseDTO;

public class MemberConverter {
    public static MemberResponseDTO.GetMemberProfileDTO toGetMemberProfileDTO(Member member) {
        return MemberResponseDTO.GetMemberProfileDTO.builder()
                .name(member.getName())
                .profileImg(member.getProfileImg())
                .build();
    }

    public static MemberResponseDTO.SurveyResultDTO toSurveyResultDTO(MemberSurvey memberSurvey) {
        return MemberResponseDTO.SurveyResultDTO.builder()
                .surveyId(memberSurvey.getId())
                .createdAt(memberSurvey.getCreatedAt())
                .build();
    }

    public static MemberSurvey toMemberSurvey(MemberRequestDTO.SurveyDTO request, Member member) {
        return MemberSurvey.builder()
                .member(member)
                .question1(Survey.of(1, request.getQuestion1()))
                .question2(request.getQuestion2().stream().map(q -> Survey.of(2, q)).toList())
                .question3(Survey.of(3, request.getQuestion3()))
                .question4(request.getQuestion4())
                .build();
    }
}
