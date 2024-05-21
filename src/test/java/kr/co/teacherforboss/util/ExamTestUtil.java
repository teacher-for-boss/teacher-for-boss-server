package kr.co.teacherforboss.util;

import kr.co.teacherforboss.domain.Exam;
import kr.co.teacherforboss.domain.ExamCategory;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.MemberExam;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.enums.ExamType;

public class ExamTestUtil {

    public ExamCategory generateExamCategory() {
        return ExamCategory.builder()
                .name("카테고리")
                .build();
    }
    public Exam generateExam(ExamType examType) {
        ExamCategory examCategory = generateExamCategory();
        return Exam.builder()
                .examType(examType)
                .name("시험 1")
                .examCategory(examCategory)
                .build();
    }

    public Question generateQuestion(Exam exam, String questionName, Long answer) {
        return Question.builder()
                .exam(exam)
                .name(questionName)
                .answer(answer)
                .points(10)
                .commentary("해설")
                .build();
    }

    public MemberExam generateMemberExam(Exam exam, Member member){
        return MemberExam.builder()
                .member(member)
                .exam(exam)
                .score(70)
                .time(100000L)
                .build();
    }
}
