package kr.co.teacherforboss.service.mypageService;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.MemberHandler;
import kr.co.teacherforboss.converter.BoardConverter;
import kr.co.teacherforboss.domain.Answer;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.PostBookmark;
import kr.co.teacherforboss.domain.PostLike;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.enums.BooleanType;
import kr.co.teacherforboss.domain.enums.Role;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.AnswerRepository;
import kr.co.teacherforboss.repository.PostBookmarkRepository;
import kr.co.teacherforboss.repository.PostLikeRepository;
import kr.co.teacherforboss.repository.PostRepository;
import kr.co.teacherforboss.repository.QuestionRepository;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.web.dto.BoardResponseDTO;
import kr.co.teacherforboss.web.dto.MypageResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MypageQueryServiceImpl implements MypageQueryService {

    private final AuthCommandService authCommandService;
    private final QuestionRepository questionRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostBookmarkRepository postBookmarkRepository;
    private final AnswerRepository answerRepository;

    @Override
    @Transactional(readOnly = true)
    public MypageResponseDTO.GetQuestionInfosDTO getMyQuestions(Long lastQuestionId, int size) {
        Member member = authCommandService.getMember();
        if (!member.getRole().equals(Role.BOSS)) throw new MemberHandler(ErrorStatus.MEMBER_ROLE_INVALID);

        PageRequest pageRequest = PageRequest.of(0, size);

        Slice<Question> questionsPage = lastQuestionId == 0
                ? questionRepository.findSliceByMemberIdOrderByCreatedAtDesc(member.getId(), pageRequest)
                : questionRepository.findSliceByIdLessThanAndMemberIdOrderByCreatedAtDesc(lastQuestionId, member.getId(), pageRequest);

        List<Answer> selectedAnswers = answerRepository.findByQuestionInAndSelected(questionsPage.getContent(), BooleanType.T);
        Map<Long, Answer> selectedAnswerMap = selectedAnswers.stream()
                .collect(Collectors.toMap(answer -> answer.getQuestion().getId(), answer -> answer));

        return BoardConverter.toGetQuestionInfosDTO(questionsPage, selectedAnswerMap);
    }

    @Override
    @Transactional
    public MypageResponseDTO.GetQuestionInfosDTO getAnsweredQuestions(Long lastQuestionId, int size) {
        Member member = authCommandService.getMember();
        PageRequest pageRequest = PageRequest.of(0, size);

        if (!member.getRole().equals(Role.TEACHER)) throw new MemberHandler(ErrorStatus.MEMBER_ROLE_INVALID);

        Slice<Question> questions = lastQuestionId == 0
                ? questionRepository.findAnsweredQuestionsSliceByMemberIdOrderByCreatedAtDesc(member.getId(), pageRequest)
                : questionRepository.findAnsweredQuestionsSliceByIdLessThanAndMemberIdOrderByCreatedAtDesc(member.getId(), lastQuestionId, pageRequest);
        return BoardConverter.toGetQuestionInfosDTO(questions, member);
    }

    @Override
    @Transactional(readOnly = true)
    public BoardResponseDTO.GetPostsDTO getCommentedPosts(Long lastPostId, int size) {
        Member member = authCommandService.getMember();
        PageRequest pageRequest = PageRequest.of(0, size);

        Slice<Post> posts =  lastPostId == 0
                ? postRepository.findCommentedPostsSliceByMemberIdOrderByCreatedAtDesc(member.getId(), pageRequest)
                : postRepository.findCommentedPostsSliceByIdLessThanAndMemberIdOrderByCreatedAtDesc(member.getId(), lastPostId, pageRequest);

        List<PostLike> postLikes = postLikeRepository.findByPostInAndMemberIdAndStatus(posts.getContent(),
                member.getId(), Status.ACTIVE);
        List<PostBookmark> postBookmarks = postBookmarkRepository.findByPostInAndMemberIdAndStatus(posts.getContent(),
                member.getId(), Status.ACTIVE);

        Map<Long, Boolean> postLikeMap = postLikes.stream()
                .collect(Collectors.toMap(like -> like.getPost().getId(), like -> like.getLiked().isIdentifier()));
        Map<Long, Boolean> postBookmarkMap = postBookmarks.stream()
                .collect(Collectors.toMap(bookmark -> bookmark.getPost().getId(), bookmark -> bookmark.getBookmarked().isIdentifier()));

        // TODO : 좋아요 수, 북마크 수, 조회수 동시성 제어

        return BoardConverter.toGetPostsDTO(posts, postLikeMap, postBookmarkMap);
    }
}
