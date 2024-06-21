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

    public static CommentResponseDTO.SaveCommentDTO toSaveCommentDTO(Comment comment) {
        Long parentId = null;
        Comment parentComment = comment.getParent();
        if (parentComment != null) {
            parentId = parentComment.getId();
        }

        return CommentResponseDTO.SaveCommentDTO.builder()
                .postId(comment.getPost().getId())
                .parentId(parentId)
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public static Comment toCommentDTO(CommentRequestDTO.SaveCommentDTO request, Member member, Post post, Comment comment) {
        return Comment.builder()
                .post(post)
                .member(member)
                .parent(comment)
                .content(request.getContent())
                .likeCount(0)
                .dislikeCount(0)
                .build();
    }

    public static CommentResponseDTO.SaveCommentLikeDTO toSaveCommentLikeDTO(CommentLike liked) {
        Boolean likeStatus = (liked.getLiked() != null) ? liked.getLiked().isIdentifier() : null;
        return CommentResponseDTO.SaveCommentLikeDTO.builder()
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
}
