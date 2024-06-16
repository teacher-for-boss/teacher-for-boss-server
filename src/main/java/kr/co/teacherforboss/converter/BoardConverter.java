package kr.co.teacherforboss.converter;

import java.util.List;
import kr.co.teacherforboss.domain.Answer;
import kr.co.teacherforboss.domain.Category;
import kr.co.teacherforboss.domain.Hashtag;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.PostBookmark;
import kr.co.teacherforboss.domain.PostHashtag;
import kr.co.teacherforboss.domain.PostLike;
import kr.co.teacherforboss.domain.QuestionLike;
import kr.co.teacherforboss.domain.enums.BooleanType;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.QuestionHashtag;
import kr.co.teacherforboss.web.dto.BoardRequestDTO;
import kr.co.teacherforboss.web.dto.BoardResponseDTO;

public class BoardConverter {

    public static BoardResponseDTO.SavePostDTO toSavePostDTO(Post post) {
        return BoardResponseDTO.SavePostDTO.builder()
                .postId(post.getId())
                .createdAt(post.getCreatedAt())
                .build();
    }

    public static BoardResponseDTO.GetPostDTO toGetPostDTO(Post post, List<String> hashtagList, String liked, String bookmarked) {
        return BoardResponseDTO.GetPostDTO.builder()
                .title(post.getTitle())
                .content(post.getContent())
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
                : imageUrls.stream().map(imageUrl -> imageUrl.split("_")[1]).toList();
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

    public static BoardResponseDTO.GetPostListDTO toGetPostListDTO(int postsCount, List<BoardResponseDTO.GetPostListDTO.PostInfo> postInfos) {
        return BoardResponseDTO.GetPostListDTO.builder()
                .totalCount(postsCount)
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
                .liked(BooleanType.T)
                .member(member)
                .post(post)
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
                .createdAt(question.getCreatedAt())
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
}
