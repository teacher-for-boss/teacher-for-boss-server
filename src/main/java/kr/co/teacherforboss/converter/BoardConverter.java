package kr.co.teacherforboss.converter;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.BoardHandler;
import kr.co.teacherforboss.config.AwsS3Config;
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
import kr.co.teacherforboss.domain.enums.QuestionExtraDataUserType;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.domain.vo.questionVO.QuestionExtraData;
import kr.co.teacherforboss.web.dto.BoardRequestDTO;
import kr.co.teacherforboss.web.dto.BoardResponseDTO;
import kr.co.teacherforboss.web.dto.HomeResponseDTO;
import kr.co.teacherforboss.web.dto.MypageResponseDTO;
import org.springframework.data.domain.Slice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BoardConverter {

    public static BoardResponseDTO.SavePostDTO toSavePostDTO(Post post) {
        return BoardResponseDTO.SavePostDTO.builder()
                .postId(post.getId())
                .createdAt(post.getCreatedAt())
                .build();
    }

    public static BoardResponseDTO.GetPostDTO toGetPostDTO(Post post, TeacherInfo teacherInfo,
                                                           boolean liked, boolean bookmarked, boolean isMine, int commentCount) {
        return BoardResponseDTO.GetPostDTO.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrlList(toImageUrlList(ImageOrigin.POST.getValue(), post.getImageUuid(), post.getImageIndex()))
                .hashtagList(toPostHashtags(post))
                .likeCount(post.getLikeCount())
                .memberInfo(toMemberInfo(post.getMember(), teacherInfo))
                .bookmarkCount(post.getBookmarkCount())
                .createdAt(post.getCreatedAt())
                .liked(liked)
                .bookmarked(bookmarked)
                .isMine(isMine)
                .commentCount(commentCount)
                .build();
    }

    public static BoardResponseDTO.EditPostDTO toEditPostDTO(Post post) {
        return BoardResponseDTO.EditPostDTO.builder()
                .postId(post.getId())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    public static BoardResponseDTO.MemberInfo toMemberInfo(Member member) {
        boolean isActive = member.getStatus() == Status.ACTIVE;

        return BoardResponseDTO.MemberInfo.builder()
                .memberId(isActive ? member.getId() : 0)
                .name(isActive ? member.getNickname() : "알 수 없음")
                .profileImg(isActive ? member.getProfileImg() : null)
                .build();
    }

    public static BoardResponseDTO.MemberInfo toMemberInfo(Member member, TeacherInfo teacherInfo) {
        boolean isActive = member.getStatus() == Status.ACTIVE;
        TeacherInfo validTeacherInfo = isActive ? teacherInfo : null;

        return BoardResponseDTO.MemberInfo.builder()
                .memberId(isActive ? member.getId() : 0)
                .name(isActive ? member.getNickname() : "알 수 없음")
                .profileImg(isActive ? member.getProfileImg() : null)
                .role(isActive ? member.getRole() : null)
                .level((validTeacherInfo != null) ? validTeacherInfo.getLevel().getLevel() : null)
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

    // TODO: 게시글 상세조회 시 imageUuid 던져주고, 글 수정할 때 presignedUrl 요청에 해당 uuid값 보내기
    public static String extractImageUuid(List<String> imageUrls) {
        return (imageUrls == null || imageUrls.isEmpty())
                ? null  // UUID.randomUUID().toString()
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
            imageUrlList.add(String.format("https://%s.s3.%s.amazonaws.com/%s/%s_%s", AwsS3Config.BUCKET_NAME, AwsS3Config.REGION, origin, imageUuid, index));
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
        QuestionExtraData extraContent = createQuestionExtraField(request.getExtraContent(), category);

        return Question.builder()
                .category(category)
                .member(member)
                .title(request.getTitle())
                .content(request.getContent())
                .extraContent(extraContent)
                .solved(BooleanType.F)
                .likeCount(0)
                .viewCount(0)
                .bookmarkCount(0)
                .imageUuid(extractImageUuid(request.getImageUrlList()))
                .imageIndex(extractImageIndexs(request.getImageUrlList()))
                .build();
    }

    private static QuestionExtraData createQuestionExtraField(BoardRequestDTO.QuestionExtraField extraContent, Category category) {
        QuestionExtraDataUserType userType = validateUserType(extraContent.getFirstField(), category.getId());
        String secondField = extraContent.getSecondField();
        String thirdField = extraContent.getThirdField();
        String fourthField = extraContent.getFourthField();
        String fifthField = extraContent.getFifthField();
        String sixthField = extraContent.getSixthField();

        return switch (category.getId().intValue()) {
            case 3, 6 -> QuestionExtraData.MarketData.create(userType, secondField, thirdField, fourthField, fifthField, sixthField);
            case 1 -> QuestionExtraData.TaxData.create(userType, secondField, thirdField, fourthField, fifthField, sixthField);
            case 2 -> QuestionExtraData.LaborData.create(userType, secondField, thirdField, fourthField, fifthField, sixthField);
            default -> null;
        };
    }


    private static QuestionExtraDataUserType validateUserType(int firstField, long categoryId) {
        QuestionExtraDataUserType userType = QuestionExtraDataUserType.of(firstField);

        boolean isValidUserType = switch ((int) categoryId) {
            case 3, 6 -> userType == QuestionExtraDataUserType.STORE_OWNER || userType == QuestionExtraDataUserType.ASPIRING_ENTREPRENEUR;
            case 1 -> userType == QuestionExtraDataUserType.TAX_FILLING || userType == QuestionExtraDataUserType.NO_TAX_FILLING;
            case 2 -> userType == QuestionExtraDataUserType.WITH_CONTRACT || userType == QuestionExtraDataUserType.WITHOUT_CONTRACT;
            default -> false;
        };

        if (!isValidUserType) {
            throw new BoardHandler(ErrorStatus.INVALID_EXTRA_CONTENT_FIELDS);
        }
        return userType;
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

    public static BoardResponseDTO.GetQuestionDTO toGetQuestionDTO(Question question, QuestionLike liked,
                                                                   QuestionBookmark bookmarked, boolean isMine, int answerCount) {
        return BoardResponseDTO.GetQuestionDTO.builder()
                .title(question.getTitle())
                .content(question.getContent())
                .extraContent(question.getExtraContent())
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
                .answerCount(answerCount)
                .build();
    }

    public static List<String> toQuestionHashtags(Question question) {
        return question.getHashtagList()
                .stream().map(questionHashtag -> questionHashtag.getHashtag().getName())
                .toList();
    }

    public static BoardResponseDTO.GetAnswersDTO toGetAnswersDTO(Slice<Answer> answers, List<AnswerLike> answerLikes,
                                                                 List<TeacherInfo> teacherInfos, Member member) {
        HashMap<Long, BooleanType> answerLiked = new HashMap<>();
        answerLikes.forEach(answerLike -> answerLiked.put(answerLike.getAnswer().getId(), answerLike.getLiked()));

        HashMap<Long, TeacherInfo> teacherInfoMap = new HashMap<>();
        teacherInfos.forEach(teacherInfo -> teacherInfoMap.put(teacherInfo.getMember().getId(), teacherInfo));

        List<BoardResponseDTO.GetAnswersDTO.AnswerInfo> answerInfos = answers.stream()
                .map(answer -> BoardResponseDTO.GetAnswersDTO.AnswerInfo.builder()
                        .answerId(answer.getId())
                        .content(answer.getContent())
                        .likeCount(answer.getLikeCount())
                        .dislikeCount(answer.getDislikeCount())
                        .liked(answerLiked.get(answer.getId()) == BooleanType.T)
                        .disliked(answerLiked.get(answer.getId()) == BooleanType.F)
                        .selectedAt(answer.getSelectedAt())
                        .createdAt(answer.getCreatedAt())
                        .memberInfo(toMemberInfo(answer.getMember(), teacherInfoMap.get(answer.getMember().getId())))
                        .imageUrlList(toImageUrlList(ImageOrigin.ANSWER.getValue(), answer.getImageUuid(), answer.getImageIndex()))
                        .isMine(answer.getMember().equals(member))
                        .build())
                .toList();

        return BoardResponseDTO.GetAnswersDTO.builder()
                .hasNext(answers.hasNext())
                .answerList(answerInfos)
                .build();
    }

    public static BoardResponseDTO.GetCommentsDTO toGetCommentsDTO(Slice<Comment> parentComments,
                                                                   List<Comment> childComments,
                                                                   List<CommentLike> commentLikes,
                                                                   List<TeacherInfo> teacherInfos,
                                                                   Member member) {
        HashMap<Long, BooleanType> commentLikedMap = new HashMap<>();
        commentLikes.forEach(commentLike -> commentLikedMap.put(commentLike.getComment().getId(), commentLike.getLiked()));

        HashMap<Long, TeacherInfo> teacherInfoMap = new HashMap<>();
        teacherInfos.forEach(teacherInfo -> teacherInfoMap.put(teacherInfo.getMember().getId(), teacherInfo));

        Map<Long, BoardResponseDTO.GetCommentsDTO.CommentInfo> parentCommentMap = new HashMap<>();
        Map<Long, BoardResponseDTO.GetCommentsDTO.CommentInfo> childCommentMap = new HashMap<>();
        List<BoardResponseDTO.GetCommentsDTO.CommentInfo> totalComments = new ArrayList<>();

        parentComments.forEach(comment -> {
            BoardResponseDTO.GetCommentsDTO.CommentInfo commentInfo = toCommentInfo(comment, teacherInfoMap, commentLikedMap, member);
            parentCommentMap.put(comment.getId(), commentInfo);
            totalComments.add(commentInfo);
        });

        childComments.forEach(comment -> {
            BoardResponseDTO.GetCommentsDTO.CommentInfo commentInfo = toCommentInfo(comment, teacherInfoMap, commentLikedMap, member);
            childCommentMap.put(comment.getId(), commentInfo);

            BoardResponseDTO.GetCommentsDTO.CommentInfo parentCommentInfo = parentCommentMap.get(comment.getParent().getId());
            if (parentCommentInfo != null) {
                parentCommentInfo.getChildren().add(commentInfo);
            }
        });

        totalComments.removeIf(comment -> comment.isDeleted() && comment.getChildren().isEmpty());


        return BoardResponseDTO.GetCommentsDTO.builder()
                .hasNext(parentComments.hasNext())
                .commentList(totalComments)
                .build();
    }

    public static BoardResponseDTO.GetCommentsDTO.CommentInfo toCommentInfo (Comment comment,
                                                                             Map<Long, TeacherInfo> teacherInfoMap,
                                                                             Map<Long, BooleanType> commentLikedMap,
                                                                             Member member) {

        TeacherInfo teacherInfo = teacherInfoMap.get(comment.getMember().getId());
        BoardResponseDTO.MemberInfo memberInfo = BoardConverter.toMemberInfo(comment.getMember(), teacherInfo);
        boolean isDeleted = comment.getStatus() == Status.INACTIVE;

        return BoardResponseDTO.GetCommentsDTO.CommentInfo.builder()
                .commentId(comment.getId())
                .content(!isDeleted ? comment.getContent() : "삭제된 댓글입니다.")
                .likeCount(!isDeleted ? comment.getLikeCount() : 0)
                .dislikeCount(!isDeleted ? comment.getDislikeCount() : 0)
                .liked(commentLikedMap.get(comment.getId()) == BooleanType.T)
                .disliked(commentLikedMap.get(comment.getId()) == BooleanType.F)
                .createdAt(comment.getCreatedAt())
                .memberInfo(!isDeleted ? memberInfo : null)
                .isMine(comment.getMember().equals(member))
                .isDeleted(isDeleted)
                .children(new ArrayList<>())
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

    public static Comment toComment(BoardRequestDTO.SaveCommentDTO request, Member member, Post post, Comment comment) {
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
        boolean isActive = selectedAnswer != null && selectedAnswer.getMember().getStatus() == Status.ACTIVE;

        return new BoardResponseDTO.GetQuestionsDTO.QuestionInfo(
                question.getId(),
                question.getCategory().getName(),
                question.getTitle(),
                question.getContent(),
                question.getSolved().isIdentifier(),
                isActive ? selectedAnswer.getMember().getProfileImg() : null,
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
            boolean liked = questionLike != null && questionLike.getLiked().isIdentifier();
            boolean bookmarked = questionBookmark != null && questionBookmark.getBookmarked().isIdentifier();
            Integer answerCount = question.getAnswerList().size();
            questionInfos.add(BoardConverter.toGetQuestionInfo(question, selectedAnswer, liked, bookmarked, answerCount));
        });

        return BoardResponseDTO.GetQuestionsDTO.builder()
                .hasNext(questionsPage.hasNext())
                .questionList(questionInfos)
                .build();
    }

    public static BoardResponseDTO.DeleteCommentDTO toDeleteCommentDTO(Comment comment) {
        return BoardResponseDTO.DeleteCommentDTO.builder()
                .commentId(comment.getId())
                .deletedAt(comment.getUpdatedAt())
                .build();
    }

    public static HomeResponseDTO.GetHotPostsDTO toGetHotPostsDTO(List<Post> hotPosts) {
        List<HomeResponseDTO.GetHotPostsDTO.HotPostInfo> hotPostInfos = new ArrayList<>();
        hotPosts.forEach(post -> hotPostInfos.add(new HomeResponseDTO.GetHotPostsDTO.HotPostInfo(post.getId(), post.getTitle())));
        return HomeResponseDTO.GetHotPostsDTO.builder()
                .hotPostList(hotPostInfos)
                .build();
    }

    public static HomeResponseDTO.GetHotQuestionsDTO toGetHotQuestionsDTO(List<Question> hotQuestions) {
        List<HomeResponseDTO.GetHotQuestionsDTO.HotQuestionInfo> hotQuestionInfos = new ArrayList<>();
        hotQuestions.forEach(question -> hotQuestionInfos.add(new HomeResponseDTO.GetHotQuestionsDTO.HotQuestionInfo(
                question.getId(), question.getCategory().getName(), question.getTitle(), question.getContent(), question.getAnswerList().size())));
        return HomeResponseDTO.GetHotQuestionsDTO.builder()
                .hotQuestionList(hotQuestionInfos)
                .build();
    }

    public static MypageResponseDTO.GetQuestionInfosDTO toGetQuestionInfosDTO(Slice<Question> questions, Member member) {
        return MypageResponseDTO.GetQuestionInfosDTO.builder()
                .hasNext(questions.hasNext())
                .questionList(questions.stream().map(question ->
                        new MypageResponseDTO.GetQuestionInfosDTO.QuestionInfo(
                                question.getId(),
                                question.getCategory().getName(),
                                question.getTitle(),
                                question.getContent(),
                                question.getSolved().isIdentifier(),
                                member.getProfileImg(),
                                question.getCreatedAt()))
                        .toList())
                .build();
    }

    public static MypageResponseDTO.GetQuestionInfosDTO toGetQuestionInfosDTO(Slice<Question> questions, Map<Long, Answer> selectedAnswerMap) {
        return MypageResponseDTO.GetQuestionInfosDTO.builder()
                .hasNext(questions.hasNext())
                .questionList(questions.stream().map(question -> {
                            Answer selectedAnswer = selectedAnswerMap.getOrDefault(question.getId(), null);
                            return new MypageResponseDTO.GetQuestionInfosDTO.QuestionInfo(
                                    question.getId(),
                                    question.getCategory().getName(),
                                    question.getTitle(),
                                    question.getContent(),
                                    question.getSolved().isIdentifier(),
                                    (selectedAnswer == null) ? null : selectedAnswer.getMember().getProfileImg(),
                                    question.getCreatedAt());
                        })
                        .toList())
                .build();
    }

    public static MypageResponseDTO.GetPostInfosDTO toGetPostInfosDTO(Slice<Post> posts, Map<Long, Boolean> postLikeMap, Map<Long, Boolean> postBookmarkMap) {

        List<MypageResponseDTO.GetPostInfosDTO.PostInfo> postInfos = new ArrayList<>();

        posts.getContent().forEach(post -> {
            boolean liked = postLikeMap.get(post.getId()) != null && postLikeMap.get(post.getId());
            boolean bookmarked = postBookmarkMap.get(post.getId()) != null && postBookmarkMap.get(post.getId());
            Integer commentCount = post.getComments().size(); // TODO: query 나가는거 왜이러는지 찾아보기
            postInfos.add(new MypageResponseDTO.GetPostInfosDTO.PostInfo(
                    post.getId(),
                    post.getTitle(),
                    post.getContent(),
                    post.getBookmarkCount(),
                    commentCount,
                    post.getLikeCount(),
                    liked,
                    bookmarked,
                    post.getCreatedAt()
            ));
        });

        return MypageResponseDTO.GetPostInfosDTO.builder()
                .hasNext(posts.hasNext())
                .postList(postInfos)
                .build();
    }

    public static MypageResponseDTO.GetChipInfoDTO toGetChipInfoDTO(Member member, long answerCount, long questionCount, long bookmarkCount, int points, int questionTicketCount) {
        return MypageResponseDTO.GetChipInfoDTO.builder()
                .memberRole(member.getRole())
                .answerCount(answerCount)
                .questionCount(questionCount)
                .bookmarkCount(bookmarkCount)
                .points(points)
                .questionTicketCount(questionTicketCount)
                .build();
    }
}
