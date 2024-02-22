package kr.co.teacherforboss.converter;


import java.util.List;
import kr.co.teacherforboss.domain.Exam;
import kr.co.teacherforboss.domain.ExamCategory;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.MemberAnswer;
import kr.co.teacherforboss.domain.MemberExam;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.QuestionChoice;
import kr.co.teacherforboss.web.dto.ExamResponseDTO;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public class ExamConverter {

    public static ExamResponseDTO.TakeExamsDTO toTakeExamsDTO(MemberExam memberExam) {
        return ExamResponseDTO.TakeExamsDTO.builder()
                .memberExamId(memberExam.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static MemberAnswer toMemberAnswer(Question question, QuestionChoice questionChoice) {
        return MemberAnswer.builder()
                .question(question)
                .questionChoice(questionChoice)
                .build();
    }

    public static MemberExam toMemberExam(Member member, Exam exam) {
        return MemberExam.builder()
                .member(member)
                .exam(exam)
                .build();
    }

    public static ExamResponseDTO.GetExamResultDTO toGetExamResultDTO(int score, int questionNum,
                                                                      int correctAnsNum, int incorrectAnsNum) {
        return ExamResponseDTO.GetExamResultDTO.builder()
                .score(score)
                .questionsNum(questionNum)
                .correctAnsNum(correctAnsNum)
                .incorrectAnsNum(incorrectAnsNum)
                .build();
    }
          
    public static ExamResponseDTO.GetExamCategoriesDTO toGetExamCategoriesDTO(List<ExamCategory> examCategories) {
        return ExamResponseDTO.GetExamCategoriesDTO.builder()
                .examCategoryList(examCategories.stream().map(examCategory ->
                        new ExamResponseDTO.GetExamCategoriesDTO.ExamCategoryInfo(examCategory.getId(), examCategory.getCategoryName())).toList())
                .build();
    }

    public static ExamResponseDTO.GetExamAnsNotesResultDTO toGetExamAnsNotesDTO(List<Question> questions) {
        return ExamResponseDTO.GetExamAnsNotesResultDTO.builder()
                .examWrongQuestionList(questions.stream().map(q ->
                        new ExamResponseDTO.GetExamAnsNotesResultDTO.ExamWrongQuestion(
                                q.getQuestionSequence(), q.getQuestionName()))
                        .toList()).build();
    }
}
