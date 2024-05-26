package kr.co.teacherforboss.converter;

import kr.co.teacherforboss.domain.Hashtag;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.PostBookmark;
import kr.co.teacherforboss.domain.PostHashtag;
import kr.co.teacherforboss.domain.enums.BooleanType;
import kr.co.teacherforboss.web.dto.BoardRequestDTO;
import kr.co.teacherforboss.web.dto.BoardResponseDTO;

import java.util.List;

public class BoardConverter {

    public static BoardResponseDTO.SavePostDTO toSavePostDTO(Post post) {
        return BoardResponseDTO.SavePostDTO.builder()
                .postId(post.getId())
                .createdAt(post.getCreatedAt())
                .build();
    }

    public static BoardResponseDTO.GetPostDTO toGetPostDTO(Post post, List<String> hashtagList, String liked, String bookmarked) {;
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
        String imageUrlList = null;
        if (request.getImageUrlList() != null) {
            imageUrlList = String.join(";", request.getImageUrlList());
        }
        return Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .member(member)
                .imageUrl(imageUrlList)
                .likeCount(0)
                .bookmarkCount(0)
                .viewCount(0)
                .build();
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

    public static PostBookmark toBookmark(Post post, Member member) {
        return PostBookmark.builder()
                .bookmarked(BooleanType.T)
                .member(member)
                .post(post)
                .build();
    }

    public static BoardResponseDTO.SaveBookmarkDTO toSaveBookmarkDTO(PostBookmark bookmark) {
        return BoardResponseDTO.SaveBookmarkDTO.builder()
                .bookmark(BooleanType.of(bookmark.getBookmarked()))
                .updatedAt(bookmark.getUpdatedAt())
                .build();
    }
}
