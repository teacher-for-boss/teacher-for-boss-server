package kr.co.teacherforboss.util;

import kr.co.teacherforboss.domain.Exam;
import kr.co.teacherforboss.domain.ExamCategory;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.MemberExam;
import kr.co.teacherforboss.domain.Problem;
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

    public Problem generateProblem(Exam exam, String problemName, Long answer) {
        return Problem.builder()
                .exam(exam)
                .name(problemName)
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
