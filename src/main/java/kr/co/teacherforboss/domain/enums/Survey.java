package kr.co.teacherforboss.domain.enums;

import kr.co.teacherforboss.domain.enums.survey.MemberSurvey1;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Survey {

    NONE(0, 0, ""),

    NO1_O1(1, 1,"사장님"),
    NO1_O2(1, 2, "예비 사장님"),
    NO1_O3(1, 3, "외식업 직원"),
    NO1_O4(1, 4, "프랜차이즈 관계자"),
    NO1_O5(1, 5, "외식업에 관심있는 일반인"),

    NO2_O1(2, 1, "요리 방법"),
    NO2_O2(2, 2, "사장님들의 노하우"),
    NO2_O3(2, 3, "배달 어플 활용법"),
    NO2_O4(2, 4, "소자본/1인 창업"),
    NO2_O5(2, 5, "배달 전문점"),
    NO2_O6(2, 6, "셀프 인테리어"),
    NO2_O7(2, 7, "SNS 마케팅"),

    NO3_O1(3, 1, "매우 잘 알고 있다"),
    NO3_O2(3, 2, "알고 있다"),
    NO3_O3(3, 3, "모르겠다"),
    NO3_O4(3, 4, "하나도 모르겠다");

    private final int question;
    private final int option;
    private final String label;

    public static Survey of(int question, int option) {
        for (Survey survey : Survey.values()) {
            if (survey.getQuestion() == question && survey.getOption() == option)
                return survey;
        }
        return NONE;
    }
}
