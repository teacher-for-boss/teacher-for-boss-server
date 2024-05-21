package kr.co.teacherforboss.converter;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import kr.co.teacherforboss.config.ExamConfig;
import kr.co.teacherforboss.domain.Exam;
import kr.co.teacherforboss.domain.ExamCategory;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.MemberChoice;
import kr.co.teacherforboss.domain.MemberExam;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.QuestionChoice;
import kr.co.teacherforboss.domain.ExamTag;
import kr.co.teacherforboss.web.dto.ExamResponseDTO;

public class ExamConverter {

    public static ExamResponseDTO.TakeExamDTO toTakeExamDTO(MemberExam memberExam) {
        return ExamResponseDTO.TakeExamDTO.builder()
                .memberExamId(memberExam.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static MemberChoice toMemberChoice(MemberExam memberExam, Question question, QuestionChoice questionChoice) {
        return MemberChoice.builder()
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
                        new ExamResponseDTO.GetExamCategoriesDTO.ExamCategoryInfo(examCategory.getId(), examCategory.getName())).toList())
                .build();
    }

    public static ExamResponseDTO.GetExamTagsDTO toGetExamTagsDTO(List<ExamTag> examTags) {
        return ExamResponseDTO.GetExamTagsDTO.builder()
                .examTagsList(examTags.stream().map(examTag ->
                        new ExamResponseDTO.GetExamTagsDTO.ExamTagInfo(examTag.getId(), examTag.getName()))
                        .toList()).build();
    }

    public static ExamResponseDTO.GetExamsDTO toGetExamsDTO(List<Exam> examList, List<MemberExam> memberExamList) {
        Map<Long, Integer> examScoresMap = memberExamList.stream()
                .collect(Collectors.toMap(memberExam -> memberExam.getExam().getId(), MemberExam::getScore));

        List<ExamResponseDTO.GetExamsDTO.ExamInfo> examInfos = examList.stream()
                .map(exam -> {
                    boolean isTakenExam = examScoresMap.containsKey(exam.getId());
                    Integer score = isTakenExam ? examScoresMap.get(exam.getId()) : null;
                    boolean isPassed = isTakenExam && score >= ExamConfig.PASS_THRESHOLD;
                    return toGetExamInfo(exam, isTakenExam, isPassed, score);
                }).collect(Collectors.toList());

        return ExamResponseDTO.GetExamsDTO.builder()
                .examList(examInfos)
                .build();
    }

    public static ExamResponseDTO.GetExamsDTO.ExamInfo toGetExamInfo(Exam exam, boolean isTakenExam, Boolean isPassed, Integer score) {
        return ExamResponseDTO.GetExamsDTO.ExamInfo.builder()
                .id(exam.getId())
                .examTag(exam.getExamTag().getName())
                .title(exam.getName())
                .description(exam.getDescription())
                .isTaken(isTakenExam)
                .isPassed(isPassed)
                .score(score)
                .build();
    }

    public static ExamResponseDTO.GetExamIncorrectChoicesResultDTO toGetExamAnsNotesDTO(List<Question> questions) {
        return ExamResponseDTO.GetExamIncorrectChoicesResultDTO.builder()
                .examIncorrectQuestionList(questions.stream().map(q ->
                        new ExamResponseDTO.GetExamIncorrectChoicesResultDTO.ExamIncorrectQuestion(
                                q.getId(), q.getSequence(), q.getName()))
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

        return new ExamResponseDTO.GetQuestionsDTO.QuestionInfo(question.getId(), question.getSequence(), question.getName(), choiceInfos);
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
