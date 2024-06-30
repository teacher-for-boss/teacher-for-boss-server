package kr.co.teacherforboss.service.commentService;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.BoardHandler;
import kr.co.teacherforboss.converter.CommentConverter;
import kr.co.teacherforboss.converter.MemberConverter;
import kr.co.teacherforboss.domain.Comment;
import kr.co.teacherforboss.domain.CommentLike;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.TeacherInfo;
import kr.co.teacherforboss.domain.enums.BooleanType;
import kr.co.teacherforboss.domain.enums.Role;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.CommentLikeRepository;
import kr.co.teacherforboss.repository.CommentRepository;
import kr.co.teacherforboss.repository.PostRepository;
import kr.co.teacherforboss.repository.TeacherInfoRepository;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.web.dto.CommentResponseDTO;
import kr.co.teacherforboss.web.dto.MemberResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommentQueryServiceImpl implements CommentQueryService {
    private final AuthCommandService authCommandService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final TeacherInfoRepository teacherInfoRepository;

    @Override
    @Transactional(readOnly = true)
    public CommentResponseDTO.GetCommentListDTO getCommentListDTO(Long postId) {
        Member member = authCommandService.getMember();

        Post post = postRepository.findByIdAndStatus(postId, Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.POST_NOT_FOUND));

        Integer totalCount = commentRepository.countAllByPostAndStatus(post, Status.ACTIVE);

        List<Comment> comments = commentRepository.findAllByPostAndStatus(post, Status.ACTIVE);
        Map<Long, String> teacherLevelMap = getTeacherLevelMap(comments);

        List<CommentLike> commentLikes = commentLikeRepository.findByMemberAndCommentInAndStatus(member, comments, Status.ACTIVE);
        Map<Long, Boolean> commentLikedMap = getCommentLikedMap(comments, commentLikes);

        Map<Long, CommentResponseDTO.GetCommentListDTO.CommentInfo> parentCommentMap = new HashMap<>();
        List<CommentResponseDTO.GetCommentListDTO.CommentInfo> commentList = new ArrayList<>();

        for (Comment comment : comments) {
            String level = null;
            if (comment.getMember().getRole() == Role.TEACHER) {
                level = teacherLevelMap.get(comment.getMember().getId());
            }

            Boolean isLiked = commentLikedMap.get(comment.getId());

            MemberResponseDTO.MemberInfoDTO memberInfo = MemberConverter.toMemberInfoDTO(comment.getMember(), level);
            CommentResponseDTO.GetCommentListDTO.CommentInfo commentInfo;

            if (comment.getParent() == null) {
                commentInfo = CommentConverter.toGetCommentInfo(comment, isLiked, memberInfo, new ArrayList<>());
                commentList.add(commentInfo);
                parentCommentMap.put(comment.getId(), commentInfo);
            } else {
                commentInfo = CommentConverter.toGetCommentInfo(comment, isLiked, memberInfo, null);
                CommentResponseDTO.GetCommentListDTO.CommentInfo parentCommentInfo = parentCommentMap.get(comment.getParent().getId());
                if (parentCommentInfo != null) {
                    parentCommentInfo.getChildren().add(commentInfo);
                }
            }
        }

        return CommentConverter.toGetCommentListDTO(totalCount, commentList);
    }

    private Map<Long, String> getTeacherLevelMap(List<Comment> comments) {
        Set<Member> teacherMembers = new HashSet<>();
        for (Comment comment : comments) {
            if (comment.getMember().getRole() == Role.TEACHER) {
                teacherMembers.add(comment.getMember());
            }
        }

        Map<Long, String> teacherLevelMap = new HashMap<>();
        if (!teacherMembers.isEmpty()) {
            List<TeacherInfo> teacherInfos = teacherInfoRepository.findByMemberInAndStatus(new ArrayList<>(teacherMembers), Status.ACTIVE);
            for (TeacherInfo teacherInfo : teacherInfos) {
                teacherLevelMap.put(teacherInfo.getMember().getId(), teacherInfo.getLevel().getLevel());
            }
        }
        return teacherLevelMap;
    }

    private Map<Long, Boolean> getCommentLikedMap(List<Comment> comments, List<CommentLike> commentLikes) {
        Map<Long, Boolean> commentLikedMap = new HashMap<>();

        for (Comment comment : comments) {
            commentLikedMap.put(comment.getId(), null);
        }

        for (CommentLike commentLike : commentLikes) {
            Boolean isLiked = null;
            if (commentLike.getLiked() == BooleanType.T) isLiked = true;
            else if (commentLike.getLiked() == BooleanType.F) isLiked = false;
            commentLikedMap.put(commentLike.getComment().getId(), isLiked);
        }

        return commentLikedMap;
    }
}
