package kr.co.teacherforboss.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Slice;

import kr.co.teacherforboss.config.S3Config;
import kr.co.teacherforboss.domain.Answer;
import kr.co.teacherforboss.domain.AnswerLike;
import kr.co.teacherforboss.domain.Category;
import kr.co.teacherforboss.domain.Comment;
import kr.co.teacherforboss.domain.CommentLike;
import kr.co.teacherforboss.domain.Hashtag;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.PostBookmark;
import kr.co.teacherforboss.domain.PostHashtag;
import kr.co.teacherforboss.domain.PostLike;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.QuestionBookmark;
import kr.co.teacherforboss.domain.QuestionHashtag;
import kr.co.teacherforboss.domain.QuestionLike;
import kr.co.teacherforboss.domain.TeacherInfo;
import kr.co.teacherforboss.domain.enums.BooleanType;
import kr.co.teacherforboss.domain.enums.ImageOrigin;
import kr.co.teacherforboss.web.dto.BoardRequestDTO;
import kr.co.teacherforboss.web.dto.BoardResponseDTO;

public class BoardConverter {

    public static BoardResponseDTO.SavePostDTO toSavePostDTO(Post post) {
        return BoardResponseDTO.SavePostDTO.builder()
                .postId(post.getId())
                .createdAt(post.getCreatedAt())
                .build();
    }

    public static BoardResponseDTO.GetPostDTO toGetPostDTO(Post post, boolean liked, boolean bookmarked, boolean isMine) {
        return BoardResponseDTO.GetPostDTO.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrlList(toImageUrlList(ImageOrigin.POST.getValue(), post.getImageUuid(), post.getImageIndex()))
                .hashtagList(toPostHashtags(post))
                .likeCount(post.getLikeCount())
                .memberInfo(toMemberInfo(post.getMember()))
                .bookmarkCount(post.getBookmarkCount())
                .createdAt(post.getCreatedAt())
                .liked(liked)
                .bookmarked(bookmarked)
                .isMine(isMine)
                .build();
    }

    public static BoardResponseDTO.MemberInfo toMemberInfo(Member member) {
        return BoardResponseDTO.MemberInfo.builder()
                .memberId(member.getId())
                .name(member.getName())
                .profileImg(member.getProfileImg())
                .build();
    }

    public static BoardResponseDTO.MemberInfo toMemberInfo(Member member, TeacherInfo teacherInfo) {
        return BoardResponseDTO.MemberInfo.builder()
                .memberId(member.getId())
                .name(member.getName())
                .profileImg(member.getProfileImg())
                .level((teacherInfo == null) ? null : teacherInfo.getLevel().getLevel())
                .build();
    }

    public static Post toPost(BoardRequestDTO.SavePostDTO request, Member member) {
        return Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .member(member)
                .imageUuid(extractImageUuid(request.getImageUrlList()))
                .imageIndex(extractImageIndexs(request.getImageUrlList()))
                .likeCount(0)
                .bookmarkCount(0)
                .viewCount(0)
                .build();
    }

    public static String extractImageUuid(List<String> imageUrls) {
        return (imageUrls == null || imageUrls.isEmpty())
                ? null
                : imageUrls.get(0).substring(imageUrls.get(0).lastIndexOf("/") + 1, imageUrls.get(0).indexOf("_"));
    }

    public static List<String> extractImageIndexs(List<String> imageUrls) {
        return (imageUrls == null || imageUrls.isEmpty())
                ? null
                : imageUrls.stream().map(imageUrl -> (!imageUrl.contains("?"))
                        ? imageUrl.split("_")[1]
                        : imageUrl.substring(imageUrl.indexOf("_"), imageUrl.indexOf("?"))).toList();
    }

    public static List<String> toImageUrlList(String origin, String imageUuid, List<String> imageIndexs) {
        if (imageUuid == null || imageIndexs == null || imageIndexs.isEmpty()) {
            return Collections.emptyList();
        }

        // TODO: CloudFront 붙이기 !
        List<String> imageUrlList = new ArrayList<>();
        for (String index : imageIndexs) {
            imageUrlList.add(String.format("https://%s.s3.%s.amazonaws.com/%s/%s_%s", S3Config.BUCKET_NAME, S3Config.REGION, origin, imageUuid, index));
        }
        return imageUrlList;
    }

    public static Hashtag toHashtag(String hashtag) {
        return Hashtag.builder()
                .name(hashtag)
                .build();
    }

    public static PostHashtag toPostHashtag(Post post, Hashtag hashtag) {
        return PostHashtag.builder()
                .post(post)
                .hashtag(hashtag)
                .build();
    }

    public static List<String> toPostHashtags(Post post) {
        return post.getHashtags()
                .stream().map(hashtag ->
                        hashtag.getHashtag().getName())
                .toList();
    }

    public static BoardResponseDTO.GetPostsDTO.PostInfo toGetPostInfo(Post post, boolean bookmark, boolean like, Integer commentCount) {
        return new BoardResponseDTO.GetPostsDTO.PostInfo(post.getId(), post.getTitle(), post.getContent(), post.getBookmarkCount(), commentCount, post.getLikeCount(),
                like, bookmark, post.getCreatedAt());
    }

    public static BoardResponseDTO.GetPostsDTO toGetPostsDTO(Slice<Post> posts, Map<Long, Boolean> postLikeMap, Map<Long, Boolean> postBookmarkMap) {

        List<BoardResponseDTO.GetPostsDTO.PostInfo> postInfos = new ArrayList<>();

        posts.getContent().forEach(post -> {
            boolean like = postLikeMap.get(post.getId()) != null && postLikeMap.get(post.getId());
            boolean bookmark = postBookmarkMap.get(post.getId()) != null && postBookmarkMap.get(post.getId());
            Integer commentCount = post.getComments().size(); // TODO: query 나가는거 왜이러는지 찾아보기
            postInfos.add(BoardConverter.toGetPostInfo(post, bookmark, like, commentCount));
        });

        return BoardResponseDTO.GetPostsDTO.builder()
                .hasNext(posts.hasNext())
                .postList(postInfos)
                .build();
    }

  	public static BoardResponseDTO.SaveQuestionDTO toSaveQuestionDTO(Question question) {
        return BoardResponseDTO.SaveQuestionDTO.builder()
                .questionId(question.getId())
                .createdAt(question.getCreatedAt())
                .build();
	}

    public static Question toQuestion(BoardRequestDTO.SaveQuestionDTO request, Member member, Category category) {
        return Question.builder()
                .category(category)
                .member(member)
                .title(request.getTitle())
                .content(request.getContent())
                .solved(BooleanType.F)
                .likeCount(0)
                .viewCount(0)
                .bookmarkCount(0)
                .imageUuid(extractImageUuid(request.getImageUrlList()))
                .imageIndex(extractImageIndexs(request.getImageUrlList()))
                .build();
    }

    public static QuestionHashtag toQuestionHashtag(Question question, Hashtag hashtag) {
        return QuestionHashtag.builder()
                .question(question)
                .hashtag(hashtag)
                .build();
    }

    public static PostBookmark toSavePostBookmark(Post post, Member member) {
        return PostBookmark.builder()
                .bookmarked(BooleanType.F)
                .member(member)
                .post(post)
                .build();
    }

    public static BoardResponseDTO.TogglePostBookmarkDTO toTogglePostBookmarkDTO(PostBookmark bookmark) {
        return BoardResponseDTO.TogglePostBookmarkDTO.builder()
                .bookmark(bookmark.getBookmarked().isIdentifier())
                .updatedAt(bookmark.getUpdatedAt())
                .build();
    }

    public static PostLike toPostLike(Post post, Member member) {
        return PostLike.builder()
                .liked(BooleanType.F)
                .member(member)
                .post(post)
                .build();
    }

    public static QuestionLike toQuestionLike(Question question, Member member) {
        return QuestionLike.builder()
                .liked(BooleanType.F)
                .member(member)
                .question(question)
                .build();
    }

    public static BoardResponseDTO.TogglePostLikeDTO toTogglePostLikeDTO(PostLike like) {
        return BoardResponseDTO.TogglePostLikeDTO.builder()
                .like(like.getLiked().isIdentifier())
                .updatedAt(like.getUpdatedAt())
                .build();
    }

    public static BoardResponseDTO.EditQuestionDTO toEditQuestionDTO(Question question) {
        return BoardResponseDTO.EditQuestionDTO.builder()
                .questionId(question.getId())
                .updatedAt(question.getUpdatedAt())
                .build();
    }

    public static Answer toAnswer(Question question, Member member, BoardRequestDTO.SaveAnswerDTO request) {
        return Answer.builder()
                .question(question)
                .member(member)
                .content(request.getContent())
                .selected(BooleanType.F)
                .likeCount(0)
                .dislikeCount(0)
                .imageUuid(extractImageUuid(request.getImageUrlList()))
                .imageIndex(extractImageIndexs(request.getImageUrlList()))
                .build();
    }

    public static BoardResponseDTO.EditAnswerDTO toEditAnswerDTO(Answer answer) {
        return BoardResponseDTO.EditAnswerDTO.builder()
                .answerId(answer.getId())
                .updatedAt(answer.getUpdatedAt())
                .build();
    }

    public static BoardResponseDTO.SaveAnswerDTO toSaveAnswerDTO(Answer answer) {
        return BoardResponseDTO.SaveAnswerDTO.builder()
                .answerId(answer.getId())
                .createdAt(answer.getCreatedAt())
                .build();
    }

    public static BoardResponseDTO.DeleteQuestionDTO toDeleteQuestionDTO(Question question) {
        return BoardResponseDTO.DeleteQuestionDTO.builder()
                .questionId(question.getId())
                .deletedAt(question.getUpdatedAt())
                .build();
    }

    public static BoardResponseDTO.DeletePostDTO toDeletePostDTO(Post post) {
        return BoardResponseDTO.DeletePostDTO.builder()
                .postId(post.getId())
                .deletedAt(post.getUpdatedAt())
                .build();
    }

    public static BoardResponseDTO.ToggleQuestionLikeDTO toToggleQuestionLikeDTO(QuestionLike questionLike) {
        return BoardResponseDTO.ToggleQuestionLikeDTO.builder()
                .questionId(questionLike.getQuestion().getId())
                .liked(questionLike.getLiked().isIdentifier())
                .updatedAt(questionLike.getUpdatedAt())
                .build();
    }

    public static BoardResponseDTO.DeleteAnswerDTO toDeleteAnswerDTO(Answer answer) {
        return BoardResponseDTO.DeleteAnswerDTO.builder()
                .answerId(answer.getId())
                .deletedAt(answer.getUpdatedAt())
                .build();
    }

    public static QuestionBookmark toQuestionBookmark(Question questionToBookmark, Member member) {
        return QuestionBookmark.builder()
                .question(questionToBookmark)
                .member(member)
                .bookmarked(BooleanType.F)
                .build();
    }

    public static BoardResponseDTO.ToggleQuestionBookmarkDTO toToggleQuestionBookmarkDTO(QuestionBookmark questionBookmark) {
        return BoardResponseDTO.ToggleQuestionBookmarkDTO.builder()
                .questionId(questionBookmark.getQuestion().getId())
                .bookmarked(questionBookmark.getBookmarked().isIdentifier())
                .updatedAt(questionBookmark.getUpdatedAt())
                .build();
    }

    public static BoardResponseDTO.GetQuestionDTO toGetQuestionDTO(Question question, QuestionLike liked, QuestionBookmark bookmarked, boolean isMine) {
        return BoardResponseDTO.GetQuestionDTO.builder()
                .title(question.getTitle())
                .content(question.getContent())
                .category(question.getCategory().getName())
                .imageUrlList(toImageUrlList(ImageOrigin.QUESTION.getValue(), question.getImageUuid(), question.getImageIndex()))
                .hashtagList(toQuestionHashtags(question))
                .memberInfo(toMemberInfo(question.getMember()))
                .liked(liked != null && liked.getLiked().isIdentifier())
                .bookmarked(bookmarked != null && bookmarked.getBookmarked().isIdentifier())
                .likeCount(question.getLikeCount())
                .bookmarkCount(question.getBookmarkCount())
                .createdAt(question.getCreatedAt())
                .isMine(isMine)
                .build();
    }

    public static List<String> toQuestionHashtags(Question question) {
        return question.getHashtagList()
                .stream().map(questionHashtag -> questionHashtag.getHashtag().getName())
                .toList();
    }

    public static BoardResponseDTO.GetAnswersDTO toGetAnswersDTO(Slice<Answer> answers, List<AnswerLike> answerLikes,
                                                                 List<TeacherInfo> teacherInfos) {
        HashMap<Long, BooleanType> answerLiked = new HashMap<>();
        answerLikes.forEach(answerLike -> answerLiked.put(answerLike.getAnswer().getId(), answerLike.getLiked()));

        HashMap<Long, TeacherInfo> teacherInfoMap = new HashMap<>();
        teacherInfos.forEach(teacherInfo -> teacherInfoMap.put(teacherInfo.getId(), teacherInfo));

        List<BoardResponseDTO.GetAnswersDTO.AnswerInfo> answerInfos = answers.stream()
                .map(answer -> BoardResponseDTO.GetAnswersDTO.AnswerInfo.builder()
                        .answerId(answer.getId())
                        .content(answer.getContent())
                        .likeCount(answer.getLikeCount())
                        .dislikeCount(answer.getDislikeCount())
                        .liked(answerLiked.get(answer.getId()) == BooleanType.T)
                        .disliked(answerLiked.get(answer.getId()) == BooleanType.F)
                        .selected(answer.getSelected().isIdentifier())
                        .createdAt(answer.getCreatedAt())
                        .memberInfo(toMemberInfo(answer.getMember(), teacherInfoMap.get(answer.getMember().getId())))
                        .imageUrlList(toImageUrlList(ImageOrigin.ANSWER.getValue(), answer.getImageUuid(), answer.getImageIndex()))
                        .build())
                .toList();

        return BoardResponseDTO.GetAnswersDTO.builder()
                .hasNext(answers.hasNext())
                .answerList(answerInfos)
                .build();
    }

    public static AnswerLike toAnswerLike(Answer answer, Member member) {
        return AnswerLike.builder()
                .answer(answer)
                .member(member)
                .liked(null)
                .build();
    }

    public static CommentLike toCommentLike(Comment comment, Member member) {
        return CommentLike.builder()
                .comment(comment)
                .member(member)
                .liked(null)
                .build();
    }

    public static BoardResponseDTO.ToggleAnswerLikeDTO toToggleAnswerLikeDTO(AnswerLike answerLike) {
        return BoardResponseDTO.ToggleAnswerLikeDTO.builder()
                .answerId(answerLike.getAnswer().getId())
                .liked((answerLike.getLiked() == null) ? null : answerLike.getLiked().isIdentifier())
                .likedCount(answerLike.getAnswer().getLikeCount())
                .dislikedCount(answerLike.getAnswer().getDislikeCount())
                .updatedAt(answerLike.getUpdatedAt())
                .build();
    }

    public static BoardResponseDTO.ToggleCommentLikeDTO toToggleCommentLikeDTO(CommentLike commentLike) {
        return BoardResponseDTO.ToggleCommentLikeDTO.builder()
                .commentId(commentLike.getComment().getId())
                .liked((commentLike.getLiked() == null) ? null : commentLike.getLiked().isIdentifier())
                .likedCount(commentLike.getComment().getLikeCount())
                .dislikedCount(commentLike.getComment().getDislikeCount())
                .updatedAt(commentLike.getUpdatedAt())
                .build();
    }

    public static BoardResponseDTO.SelectAnswerDTO toSelectAnswerDTO(Answer answer) {
        return BoardResponseDTO.SelectAnswerDTO.builder()
                .selectedAnswerId(answer.getId())
                .updatedAt(answer.getUpdatedAt())
                .build();
    }

    public static BoardResponseDTO.SaveCommentDTO toSaveCommentDTO(Comment comment) {
        return BoardResponseDTO.SaveCommentDTO.builder()
                .commentId(comment.getId())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public static Comment toCommentDTO(BoardRequestDTO.SaveCommentDTO request, Member member, Post post, Comment comment) {
        return Comment.builder()
                .post(post)
                .member(member)
                .parent(comment)
                .content(request.getContent())
                .likeCount(0)
                .dislikeCount(0)
                .build();
    }

    public static BoardResponseDTO.GetQuestionsDTO.QuestionInfo toGetQuestionInfo(Question question, Answer selectedAnswer, boolean liked, boolean bookmarked, Integer answerCount) {
        return new BoardResponseDTO.GetQuestionsDTO.QuestionInfo(
                question.getId(),
                question.getTitle(),
                question.getContent(),
                question.getSolved().isIdentifier(),
                (selectedAnswer == null) ? null : selectedAnswer.getMember().getProfileImg(),
                question.getBookmarkCount(),
                answerCount,
                question.getLikeCount(),
                liked,
                bookmarked,
                question.getCreatedAt()
        );
    }

    public static BoardResponseDTO.GetQuestionsDTO toGetQuestionsDTO(Slice<Question> questionsPage, Map<Long, QuestionLike> questionLikeMap, Map<Long, QuestionBookmark> questionBookmarkMap, Map<Long, Answer> selectedAnswerMap) {

        List<BoardResponseDTO.GetQuestionsDTO.QuestionInfo> questionInfos = new ArrayList<>();

        questionsPage.getContent().forEach(question -> {
            Answer selectedAnswer = selectedAnswerMap.getOrDefault(question.getId(), null);
            QuestionLike questionLike = questionLikeMap.get(question.getId());
            QuestionBookmark questionBookmark = questionBookmarkMap.get(question.getId());
            boolean liked = (questionLike == null) ? false : questionLike.getLiked().isIdentifier();
            boolean bookmarked = (questionBookmark == null) ? false : questionBookmark.getBookmarked().isIdentifier();
            Integer answerCount = question.getAnswerList().size();
            questionInfos.add(BoardConverter.toGetQuestionInfo(question, selectedAnswer, liked, bookmarked, answerCount));
        });

        return BoardResponseDTO.GetQuestionsDTO.builder()
                .hasNext(questionsPage.hasNext())
                .questionList(questionInfos)
                .build();
    }
}
