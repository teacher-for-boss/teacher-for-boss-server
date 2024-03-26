package kr.co.teacherforboss.converter;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kr.co.teacherforboss.domain.Exam;
import kr.co.teacherforboss.domain.ExamCategory;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.MemberAnswer;
import kr.co.teacherforboss.domain.MemberExam;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.QuestionChoice;
import kr.co.teacherforboss.domain.Tag;
import kr.co.teacherforboss.web.dto.ExamResponseDTO;
import kr.co.teacherforboss.web.dto.ExamResponseDTO.TagInfo;

public class ExamConverter {

    public static ExamResponseDTO.TakeExamDTO toTakeExamDTO(MemberExam memberExam) {
        return ExamResponseDTO.TakeExamDTO.builder()
                .memberExamId(memberExam.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static MemberAnswer toMemberAnswer(MemberExam memberExam, Question question, QuestionChoice questionChoice) {
        return MemberAnswer.builder()
                .memberExam(memberExam)
                .question(question)
                .questionChoice(questionChoice)
                .build();
    }

    public static MemberExam toMemberExam(Member member, Exam exam, int score, long leftTime) {
        return MemberExam.builder()
                .member(member)
                .exam(exam)
                .score(score)
                .time(MemberExam.TIME_LIMIT - leftTime)
                .build();
    }

    public static ExamResponseDTO.GetExamResultDTO toGetExamResultDTO(long memberExamId, int score, int questionNum,
                                                                      int correctAnsNum, int incorrectAnsNum) {
        return ExamResponseDTO.GetExamResultDTO.builder()
                .memberExamId(memberExamId)
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

    public static ExamResponseDTO.GetTagsDTO toGetTagsDTO(List<Tag> tags) {
        Map<String, List<TagInfo>> categoryTagMap = tags.stream()
                .collect(Collectors.groupingBy(
                        tag -> tag.getExamCategory().getCategoryName(),
                        Collectors.mapping(
                                tag -> new TagInfo(tag.getId(), tag.getTagName()),
                                Collectors.toList())
                ));

        List<ExamResponseDTO.GetTagsDTO.CategoryTags> categoryTagsList = categoryTagMap.entrySet().stream()
                .map(entry -> new ExamResponseDTO.GetTagsDTO.CategoryTags(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return new ExamResponseDTO.GetTagsDTO(categoryTagsList);
    }

    public static ExamResponseDTO.GetExamsDTO toGetExamsDTO(List<ExamResponseDTO.GetExamsDTO.ExamInfo> examInfos) {
        return ExamResponseDTO.GetExamsDTO.builder()
                .examList(examInfos)
                .build();
    }

    public static ExamResponseDTO.GetExamsDTO.ExamInfo toGetExamInfo(Exam exam, boolean isTakenExam, Boolean isPassed, Integer score) {
        return ExamResponseDTO.GetExamsDTO.ExamInfo.builder()
                .name(exam.getName())
                .description(exam.getDescription())
                .tagName(exam.getTag().getTagName())
                .examCategoryName(exam.getExamCategory().getCategoryName())
                .isTakenExam(isTakenExam)
                .isPassed(isPassed)
                .score(score)
                .build();
    }

    public static ExamResponseDTO.GetExamIncorrectAnswersResultDTO toGetExamAnsNotesDTO(List<Question> questions) {
        return ExamResponseDTO.GetExamIncorrectAnswersResultDTO.builder()
                .examIncorrectQuestionList(questions.stream().map(q ->
                        new ExamResponseDTO.GetExamIncorrectAnswersResultDTO.ExamIncorrectQuestion(
                                q.getId(), q.getQuestionSequence(), q.getQuestionName()))
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

    public static ExamResponseDTO.GetSolutionsDTO toGetSolutionsDTO(List<Question> questions) {
        return ExamResponseDTO.GetSolutionsDTO.builder()
                .solutionList(questions.stream().map(question ->
                        new ExamResponseDTO.GetSolutionsDTO.QuestionSolution(question.getId(), question.getCommentary()))
                        .toList()).build();
    }
  
    public static ExamResponseDTO.GetExamRankInfoDTO toGetExamRankInfoDTO(List<ExamResponseDTO.GetExamRankInfoDTO.ExamRankInfo> examRankInfos) {
        return ExamResponseDTO.GetExamRankInfoDTO.builder()
                .examRankList(examRankInfos)
                .build();
    }

    public static ExamResponseDTO.GetExamRankInfoDTO.ExamRankInfo toGetExamRankInfo(MemberExam memberExam, Long rank, boolean isMine) {
        return ExamResponseDTO.GetExamRankInfoDTO.ExamRankInfo.builder()
                .rank(rank)
                .memberId(memberExam.getMember().getId())
                .name(memberExam.getMember().getName())
                .profileImg(memberExam.getMember().getProfileImg())
                .score(memberExam.getScore())
                .isMine(isMine)
                .build();
    }

    public static ExamResponseDTO.GetAverageDTO toGetAverageDTO(int averageScore, int userScore) {
        return ExamResponseDTO.GetAverageDTO.builder()
                .averageScore(averageScore)
                .userScore(userScore)
                .build();
    }
  
    public static ExamResponseDTO.GetTakenExamCountDTO toGetTakenExamCountDTO(List<Exam> exams) {
        return ExamResponseDTO.GetTakenExamCountDTO.builder()
                .takenExamsCount(exams.size())
                .build();
    }
}
