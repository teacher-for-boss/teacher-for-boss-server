package kr.co.teacherforboss.util;

import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.TeacherInfo;
import kr.co.teacherforboss.domain.enums.Level;

import java.time.LocalDate;

public class MemberTestUtil {

    public TeacherInfo generateTeacherInfoDummy(Member member) {
            return TeacherInfo.builder()
                    .member(member)
                    .accountHolder("예금주명")
                    .level(Level.LEVEL2)
                    .career(99)
                    .keywords("키워드1")
                    .introduction("자기소개")
                    .field("디지털")
                    .representative("대표자명")
                    .accountNumber("계좌번호")
                    .bank("하나은행")
                    .openDate(LocalDate.parse("2023-04-22"))
                    .businessNumber("비즈니스번호")
                    .build();
    }
}
