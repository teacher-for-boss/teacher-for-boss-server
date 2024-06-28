package kr.co.teacherforboss.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kr.co.teacherforboss.config.S3Config;
import kr.co.teacherforboss.domain.Answer;
import kr.co.teacherforboss.domain.AnswerLike;
import kr.co.teacherforboss.domain.Category;
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
import org.springframework.data.domain.Slice;

public class BoardConverter {

    public static BoardResponseDTO.SavePostDTO toSavePostDTO(Post post) {
        return BoardResponseDTO.SavePostDTO.builder()
                .postId(post.getId())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    public static BoardResponseDTO.GetPostDTO toGetPostDTO(Post post, List<String> hashtagList, String liked, String bookmarked) {
        return BoardResponseDTO.GetPostDTO.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrlList(toImageUrlList(ImageOrigin.POST.getValue(), post.getImageUuid(), post.getImageIndex()))
                .hashtagList(hashtagList)
                .likeCount(post.getLikeCount())
                .memberInfo(toMemberInfo(post.getMember()))
                .bookmarkCount(post.getBookmarkCount())
                .createdAt(post.getCreatedAt())
                .liked(liked)
                .bookmarked(bookmarked)
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

    public static List<String> toPostHashtagList(Post post) {
        return post.getHashtagList()
                .stream().map(hashtag ->
                        hashtag.getHashtag().getName())
                .toList();
    }

    public static BoardResponseDTO.GetPostListDTO.PostInfo toGetPostInfo(Post post, boolean bookmark, boolean like, Integer commentCount) {
        return new BoardResponseDTO.GetPostListDTO.PostInfo(post.getId(), post.getTitle(), post.getContent(), post.getBookmarkCount(), commentCount, post.getLikeCount(),
                like, bookmark, post.getCreatedAt());
    }

    public static BoardResponseDTO.GetPostListDTO toGetPostListDTO(Slice<Post> posts, Map<Long, Boolean> postLikeMap, Map<Long, Boolean> postBookmarkMap) {

        List<BoardResponseDTO.GetPostListDTO.PostInfo> postInfos = new ArrayList<>();

        posts.getContent().forEach(post -> {
            boolean like = postLikeMap.get(post.getId()) != null && postLikeMap.get(post.getId());
            boolean bookmark = postBookmarkMap.get(post.getId()) != null && postBookmarkMap.get(post.getId());
            Integer commentCount = post.getCommentList().size(); // TODO: query 나가는거 왜이러는지 찾아보기
            postInfos.add(BoardConverter.toGetPostInfo(post, bookmark, like, commentCount));
        });

        return BoardResponseDTO.GetPostListDTO.builder()
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

    public static BoardResponseDTO.SavePostBookmarkDTO toSavePostBookmarkDTO(PostBookmark bookmark) {
        return BoardResponseDTO.SavePostBookmarkDTO.builder()
                .bookmark(BooleanType.T.isIdentifier())
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

    public static BoardResponseDTO.SavePostLikeDTO toSavePostLikeDTO(PostLike like) {
        return BoardResponseDTO.SavePostLikeDTO.builder()
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

    public static BoardResponseDTO.LikeQuestionDTO toLikeQuestionDTO(QuestionLike questionLike) {
        return BoardResponseDTO.LikeQuestionDTO.builder()
                .questionId(questionLike.getQuestion().getId())
                .liked(questionLike.getLiked())
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

    public static BoardResponseDTO.BookmarkQuestionDTO toBookmarkQuestionDTO(QuestionBookmark questionBookmark) {
        return BoardResponseDTO.BookmarkQuestionDTO.builder()
                .questionId(questionBookmark.getQuestion().getId())
                .bookmarked(questionBookmark.getBookmarked())
                .updatedAt(questionBookmark.getUpdatedAt())
                .build();
    }

    public static BoardResponseDTO.GetQuestionDTO toGetQuestionDTO(Question question, QuestionLike liked, QuestionBookmark bookmarked, List<String> hashtagList) {
        return BoardResponseDTO.GetQuestionDTO.builder()
                .title(question.getTitle())
                .content(question.getContent())
                .category(question.getCategory().getName())
                .imageUrlList(toImageUrlList(ImageOrigin.QUESTION.getValue(), question.getImageUuid(), question.getImageIndex()))
                .hashtagList(hashtagList)
                .memberInfo(toMemberInfo(question.getMember()))
                .liked((liked == null) ? BooleanType.F : liked.getLiked())
                .bookmarked((bookmarked == null) ? BooleanType.F : bookmarked.getBookmarked())
                .likeCount(question.getLikeCount())
                .bookmarkCount(question.getBookmarkCount())
                .createdAt(question.getCreatedAt())
                .build();
    }

    public static List<String> toQuestionHashtagList(Question question) {
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
}
