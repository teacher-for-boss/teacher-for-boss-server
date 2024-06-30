package kr.co.teacherforboss.converter;

import kr.co.teacherforboss.domain.Comment;
import kr.co.teacherforboss.domain.CommentLike;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.enums.BooleanType;
import kr.co.teacherforboss.web.dto.BoardRequestDTO;
import kr.co.teacherforboss.web.dto.BoardResponseDTO;
import kr.co.teacherforboss.web.dto.MemberResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public class CommentConverter {

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

    public static BoardResponseDTO.SaveCommentLikedDTO toSaveCommentLikedDTO(CommentLike liked) {
        Boolean likeStatus = (liked.getLiked() != null) ? liked.getLiked().isIdentifier() : null;
        return BoardResponseDTO.SaveCommentLikedDTO.builder()
                .liked(likeStatus)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static CommentLike toCommentLiked(Comment comment, Member member, BooleanType liked) {
        return CommentLike.builder()
                .comment(comment)
                .member(member)
                .liked(liked)
                .build();
    }

    public static BoardResponseDTO.GetCommentListDTO toGetCommentListDTO(int commentsCount,
                                                                         List<BoardResponseDTO.GetCommentListDTO.CommentInfo> commentInfos) {
        return BoardResponseDTO.GetCommentListDTO.builder()
                .totalCount(commentsCount)
                .commentList(commentInfos)
                .build();
    }

    public static BoardResponseDTO.GetCommentListDTO.CommentInfo toGetCommentInfo(Comment comment, Boolean isLiked, MemberResponseDTO.MemberInfoDTO memberInfo, List<BoardResponseDTO.GetCommentListDTO.CommentInfo> children) {
        return new BoardResponseDTO.GetCommentListDTO.CommentInfo(
                comment.getId(),
                comment.getContent(),
                isLiked,
                comment.getLikeCount(),
                comment.getDislikeCount(),
                comment.getCreatedAt(),
                memberInfo,
                children
        );
    }
}
