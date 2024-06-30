package kr.co.teacherforboss.converter;

import kr.co.teacherforboss.domain.Comment;
import kr.co.teacherforboss.domain.CommentLike;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.web.dto.BoardRequestDTO;
import kr.co.teacherforboss.web.dto.BoardResponseDTO;

import java.time.LocalDateTime;

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

    public static BoardResponseDTO.ToggleCommentLikeDTO toToggleCommentLikeDTO(CommentLike liked) {
        Boolean likeStatus = (liked.getLiked() != null) ? liked.getLiked().isIdentifier() : null;
        return BoardResponseDTO.ToggleCommentLikeDTO.builder()
                .liked(likeStatus)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static BoardResponseDTO.SaveCommentLikedDTO toSaveCommentLikedDTO(CommentLike liked) {
        Boolean likeStatus = (liked.getLiked() != null) ? liked.getLiked().isIdentifier() : null;
        return BoardResponseDTO.SaveCommentLikedDTO.builder()
                .liked(likeStatus)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
