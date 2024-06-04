package kr.co.teacherforboss.converter;

import kr.co.teacherforboss.domain.Comment;
import kr.co.teacherforboss.domain.CommentLike;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.enums.BooleanType;
import kr.co.teacherforboss.web.dto.CommentRequestDTO;
import kr.co.teacherforboss.web.dto.CommentResponseDTO;

import java.time.LocalDateTime;

public class CommentConverter {

    public static CommentResponseDTO.SaveCommentResultDTO toSaveCommentResultDTO(Comment comment) {
        Long parentId = null;
        Comment parentComment = comment.getComment();
        if (parentComment != null) {
            parentId = parentComment.getId();
        }

        return CommentResponseDTO.SaveCommentResultDTO.builder()
                .postId(comment.getPost().getId())
                .parentId(parentId)
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public static Comment toCommentDTO(CommentRequestDTO.SaveCommentDTO request, Member member, Post post, Comment comment) {
        return Comment.builder()
                .post(post)
                .member(member)
                .comment(comment)
                .content(request.getContent())
                .likeCount(0)
                .dislikeCount(0)
                .build();
    }

    public static CommentResponseDTO.SaveCommentLikedResultDTO toSaveCommentLikedResultDTO(CommentLike like) {
        Boolean likeStatus = (like.getLiked() != null) ? like.getLiked().isIdentifier() : null;
        return CommentResponseDTO.SaveCommentLikedResultDTO.builder()
                .like(likeStatus)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static CommentLike toCommentLike(Comment comment, Member member) {
        return CommentLike.builder()
                .comment(comment)
                .member(member)
                .liked(BooleanType.T)
                .build();
    }
}
