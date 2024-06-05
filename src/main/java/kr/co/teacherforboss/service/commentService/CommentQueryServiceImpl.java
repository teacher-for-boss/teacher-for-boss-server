package kr.co.teacherforboss.service.commentService;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.BoardHandler;
import kr.co.teacherforboss.converter.CommentConverter;
import kr.co.teacherforboss.converter.MemberConverter;
import kr.co.teacherforboss.domain.Comment;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.CommentRepository;
import kr.co.teacherforboss.repository.PostRepository;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.web.dto.CommentResponseDTO;
import kr.co.teacherforboss.web.dto.MemberResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentQueryServiceImpl implements CommentQueryService {
    private final AuthCommandService authCommandService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional(readOnly = true)
    public CommentResponseDTO.GetCommentListDTO getCommentListDTO(Long postId) {
        Member member = authCommandService.getMember();
        Post post = postRepository.findByIdAndStatus(postId, Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.POST_NOT_FOUND));
        Integer totalCount = commentRepository.countAllByStatus(Status.ACTIVE);

        List<Comment> comments = commentRepository.findAllByPostAndStatus(post, Status.ACTIVE);
        List<CommentResponseDTO.GetCommentListDTO.CommentInfo> commentInfos = comments.stream()
                .map(comment -> {
                    MemberResponseDTO.GetMemberProfileDTO memberInfo = MemberConverter.toGetMemberProfileDTO(comment.getMember());
                    return CommentConverter.toGetCommentInfo(post, comment, memberInfo);
                }).toList();

        return CommentConverter.toGetCommentListDTO(totalCount, commentInfos);
    }
}
