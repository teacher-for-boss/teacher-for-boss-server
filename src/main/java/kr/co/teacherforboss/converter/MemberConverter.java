package kr.co.teacherforboss.converter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.MemberSurvey;
import kr.co.teacherforboss.domain.TeacherInfo;
import kr.co.teacherforboss.domain.enums.Level;
import kr.co.teacherforboss.domain.enums.Survey;
import kr.co.teacherforboss.web.dto.HomeResponseDTO;
import kr.co.teacherforboss.web.dto.MemberRequestDTO;
import kr.co.teacherforboss.web.dto.MemberResponseDTO;

public class MemberConverter {
    public static MemberResponseDTO.GetMemberProfileDTO toGetMemberProfileDTO(Member member, TeacherInfo teacherInfo, Integer answerCount) {
        return MemberResponseDTO.GetMemberProfileDTO.builder()
                .name(member.getName())
                .profileImg(member.getProfileImg())
                .role(member.getRole().toString())
                .teacherInfo(toTeacherInfo(teacherInfo, answerCount))
                .build();
    }

    public static MemberResponseDTO.GetMemberProfileDTO.TeacherInfo toTeacherInfo(TeacherInfo teacherInfo, int answerCount) {
        if (teacherInfo == null) return null;
        int leftAnswerCount = 0;
        if (!teacherInfo.getLevel().equals(Level.LEVEL5)) leftAnswerCount = teacherInfo.getLevel().getLast() - answerCount;
        return MemberResponseDTO.GetMemberProfileDTO.TeacherInfo.builder()
                .level(teacherInfo.getLevel().getLevel())
                .leftAnswerCount(leftAnswerCount)
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

    public static MemberResponseDTO.EditMemberProfileDTO toEditMemberProfileDTO(Member member) {
        return MemberResponseDTO.EditMemberProfileDTO.builder()
                .nickname(member.getNickname())
                .profileImg(member.getProfileImg())
                .build();
    }

    public static HomeResponseDTO.GetHotTeachersDTO toGetHotTeachersDTO(List<Long> memberIds, Map<Long, Member> memberMap, Map<Long, TeacherInfo> teacherInfoMap) {
        return HomeResponseDTO.GetHotTeachersDTO.builder()
                .hotTeacherList(memberIds.stream().map(memberId -> {
                    Member member = memberMap.get(memberId);
                    TeacherInfo teacherInfo = teacherInfoMap.get(memberId);
                    return new HomeResponseDTO.GetHotTeachersDTO.HotTeacherInfo(member.getId(), member.getNickname(), member.getProfileImg(),
                            teacherInfo.getField(), teacherInfo.getCareer(), Arrays.stream(teacherInfo.getKeywords().split(";")).toList());
                }).toList())
                .build();
    }

    public static MemberResponseDTO.GetMemberAccountInfoDTO toGetMemberAccountInfoDTO(Member member) {
        return MemberResponseDTO.GetMemberAccountInfoDTO.builder()
                .loginType(member.getLoginType().name())
                .email(member.getEmail())
                .phone(member.getPhone())
                .build();
    }
}
