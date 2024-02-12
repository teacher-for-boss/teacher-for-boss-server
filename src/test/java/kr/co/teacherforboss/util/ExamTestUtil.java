package kr.co.teacherforboss.util;

import kr.co.teacherforboss.domain.Exam;
import kr.co.teacherforboss.domain.ExamCategory;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.MemberAnswer;
import kr.co.teacherforboss.domain.MemberExam;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.QuestionChoice;
import kr.co.teacherforboss.domain.enums.ExamType;

public class ExamTestUtil {

    public ExamCategory generateExamCategory() {
        return ExamCategory.builder()
                .categoryName("카테고리")
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

    public Question generateQuestion(Exam exam, String questionName, String answer) {
        return Question.builder()
                .exam(exam)
                .questionName(questionName)
                .answer(answer)
                .points(10)
                .commentary("해설")
                .build();
    }
}
