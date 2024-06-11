package kr.co.teacherforboss.service.boardService;

import java.util.List;
import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.BoardHandler;
import kr.co.teacherforboss.converter.BoardConverter;
import kr.co.teacherforboss.domain.Answer;
import kr.co.teacherforboss.domain.AnswerLike;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.PostBookmark;
import kr.co.teacherforboss.domain.PostLike;
import kr.co.teacherforboss.domain.TeacherInfo;
import kr.co.teacherforboss.domain.common.BaseEntity;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.AnswerLikeRepository;
import kr.co.teacherforboss.repository.AnswerRepository;
import kr.co.teacherforboss.repository.PostBookmarkRepository;
import kr.co.teacherforboss.repository.PostLikeRepository;
import kr.co.teacherforboss.repository.PostRepository;
import kr.co.teacherforboss.repository.QuestionRepository;
import kr.co.teacherforboss.repository.TeacherInfoRepository;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.web.dto.BoardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardQueryServiceImpl implements BoardQueryService {

    private final AuthCommandService authCommandService;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostBookmarkRepository postBookmarkRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final AnswerLikeRepository answerLikeRepository;
    private final TeacherInfoRepository teacherInfoRepository;


    @Override
    @Transactional(readOnly = true)
    public BoardResponseDTO.GetPostDTO getPost(Long postId) {
        Member member = authCommandService.getMember();
        Post post = postRepository.findByIdAndStatus(postId, Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.POST_NOT_FOUND));

        String liked = "F";
        String bookmarked = "F";
        List<String> hashtagList = null;

        PostLike postLike = postLikeRepository.findByPostAndMemberAndStatus(post, member, Status.ACTIVE);
        if (postLike != null) {
            liked = String.valueOf(postLike.getLiked());
        }

        PostBookmark postBookmark = postBookmarkRepository.findByPostAndMemberAndStatus(post, member, Status.ACTIVE);
        if (postBookmark != null) {
            bookmarked = String.valueOf(postBookmark.getBookmarked());
        }
        if (!post.getHashtagList().isEmpty()) {
            hashtagList = BoardConverter.toPostHashtagList(post);
        }

        return BoardConverter.toGetPostDTO(post, hashtagList, liked, bookmarked);
    }

    @Override
    @Transactional(readOnly = true)
    public BoardResponseDTO.GetAnswersDTO getAnswers(Long questionId) {
        if (!questionRepository.existsByIdAndStatus(questionId, Status.ACTIVE))
            throw new BoardHandler(ErrorStatus.QUESTION_NOT_FOUND);

        // TODO: 무한스크롤 (커서 기반) 구현
        List<Answer> answers = answerRepository.findAllByQuestionIdAndStatusOrderByCreatedAt(questionId, Status.ACTIVE);
        List<Long> answerIds = answers.stream().map(BaseEntity::getId).toList();
        List<Long> memberIds = answers.stream().map(answer -> answer.getMember().getId()).toList();

        List<AnswerLike> answerLikes = answerLikeRepository.findAllByAnswerIdInAndStatus(answerIds, Status.ACTIVE);
        List<TeacherInfo> teacherInfos = teacherInfoRepository.findAllByMemberIdInAndStatus(memberIds, Status.ACTIVE);

        return BoardConverter.toGetAnswersDTO(answers, answerLikes, teacherInfos);
    }
}
