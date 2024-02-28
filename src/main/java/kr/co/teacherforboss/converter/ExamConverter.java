package kr.co.teacherforboss.converter;


import java.time.LocalDateTime;
import java.util.List;
import kr.co.teacherforboss.domain.Exam;
import kr.co.teacherforboss.domain.ExamCategory;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.MemberAnswer;
import kr.co.teacherforboss.domain.MemberExam;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.QuestionChoice;
import kr.co.teacherforboss.web.dto.ExamResponseDTO;

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

    public static ExamResponseDTO.GetExamIncorrectAnswersResultDTO toGetExamAnsNotesDTO(List<Question> questions) {
        return ExamResponseDTO.GetExamIncorrectAnswersResultDTO.builder()
                .examIncorrectQuestionList(questions.stream().map(q ->
                        new ExamResponseDTO.GetExamIncorrectAnswersResultDTO.ExamIncorrectQuestion(
                                q.getQuestionSequence(), q.getQuestionName()))
                        .toList()).build();
    }

    public static ExamResponseDTO.GetQuestionsDTO toGetQuestionsDTO(List<Question> questions) {
        List<ExamResponseDTO.GetQuestionsDTO.QuestionInfo> questionInfos = questions.stream()
                .map(ExamConverter::toQuestionInfo)
                .toList();

        return ExamResponseDTO.GetQuestionsDTO.builder()
                .questionList(questionInfos)
                .build();
    }

    private static ExamResponseDTO.GetQuestionsDTO.QuestionInfo toQuestionInfo(Question question) {
        List<ExamResponseDTO.GetQuestionsDTO.QuestionInfo.QuestionChoiceInfo> choiceInfos = question.getQuestionOptionList().stream()
                .map(choice -> new ExamResponseDTO.GetQuestionsDTO.QuestionInfo.QuestionChoiceInfo(choice.getId(), choice.getChoice()))
                .toList();

        return new ExamResponseDTO.GetQuestionsDTO.QuestionInfo(question.getId(), question.getQuestionSequence(), question.getQuestionName(), choiceInfos);
    }

    public static ExamResponseDTO.GetExamRankInfoDTO toGetExamRankInfoDTO(List<ExamResponseDTO.GetExamRankInfoDTO.ExamRankInfo> examRankInfos) {
        return ExamResponseDTO.GetExamRankInfoDTO.builder()
                .examRankList(examRankInfos)
                .build();
    }

    public static ExamResponseDTO.GetExamRankInfoDTO.ExamRankInfo toGetExamRankInfo(MemberExam memberExam, Long rank, boolean isMine) {
        return ExamResponseDTO.GetExamRankInfoDTO.ExamRankInfo.builder()
                .rank(rank)
                .name(memberExam.getMember().getName())
                .profileImg(memberExam.getMember().getProfileImg())
                .score(memberExam.getScore())
                .isMine(isMine)
                .build();
    }
}
