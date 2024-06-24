package kr.co.teacherforboss.service.boardService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.BoardHandler;
import kr.co.teacherforboss.converter.BoardConverter;
import kr.co.teacherforboss.domain.Answer;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.PostBookmark;
import kr.co.teacherforboss.domain.PostLike;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.QuestionBookmark;
import kr.co.teacherforboss.domain.QuestionLike;
import kr.co.teacherforboss.domain.enums.BooleanType;
import kr.co.teacherforboss.domain.enums.QuestionCategory;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.AnswerRepository;
import kr.co.teacherforboss.repository.PostBookmarkRepository;
import kr.co.teacherforboss.repository.PostLikeRepository;
import kr.co.teacherforboss.repository.PostRepository;
import kr.co.teacherforboss.repository.QuestionBookmarkRepository;
import kr.co.teacherforboss.repository.QuestionLikeRepository;
import kr.co.teacherforboss.repository.QuestionRepository;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.web.dto.BoardResponseDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardQueryServiceImpl implements BoardQueryService {

    private final AuthCommandService authCommandService;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostBookmarkRepository postBookmarkRepository;
    private final QuestionRepository questionRepository;
    private final QuestionLikeRepository questionLikeRepository;
    private final QuestionBookmarkRepository questionBookmarkRepository;
    private final AnswerRepository answerRepository;

    @Override
    @Transactional(readOnly = true)
    public BoardResponseDTO.GetPostDTO getPost(Long postId) {
        Member member = authCommandService.getMember();
        Post post = postRepository.findByIdAndStatus(postId, Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.POST_NOT_FOUND));

        String liked = "F";
        String bookmarked = "F";
        List<String> hashtagList = null;

        PostLike postLike = postLikeRepository.findByPostIdAndMemberIdAndStatus(post.getId(), member.getId(), Status.ACTIVE).orElse(null);
        if (postLike != null) {
            liked = String.valueOf(postLike.getLiked());
        }

        PostBookmark postBookmark = postBookmarkRepository.findByPostIdAndMemberIdAndStatus(post.getId(), member.getId(), Status.ACTIVE).orElse(null);
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
    public BoardResponseDTO.GetPostListDTO getPostList(Long lastPostId, int size, String sortBy) {
        Member member = authCommandService.getMember();
        PageRequest pageRequest = PageRequest.of(0, size);
        Integer totalCount = postRepository.countAllByStatus(Status.ACTIVE);
        Slice<Post> postsPage;

        if (lastPostId == 0) {
            postsPage = switch (sortBy) {
                case "likes" -> postRepository.findSliceByIdLessThanOrderByLikeCountDesc(pageRequest);
                case "views" -> postRepository.findSliceByIdLessThanOrderByViewCountDesc(pageRequest);
                default -> postRepository.findSliceByIdLessThanOrderByCreatedAtDesc(pageRequest);
            };
        }
        else {
            postsPage = switch (sortBy) {
                case "likes" -> postRepository.findSliceByIdLessThanOrderByLikeCountDescWithLastPostId(lastPostId, pageRequest);
                case "views" -> postRepository.findSliceByIdLessThanOrderByViewCountDescWithLastPostId(lastPostId, pageRequest);
                default -> postRepository.findSliceByIdLessThanOrderByCreatedAtDescWithLastPostId(lastPostId, pageRequest);
            };
        }

        List<BoardResponseDTO.GetPostListDTO.PostInfo> postInfos = new ArrayList<>();

        List<PostLike> postLikes = postLikeRepository.findByPostInAndStatus(postsPage.getContent(), Status.ACTIVE);
        List<PostBookmark> postBookmarks = postBookmarkRepository.findByPostInAndStatus(postsPage.getContent(), Status.ACTIVE);

        Map<Long, PostLike> postLikeMap = postLikes.stream()
                .collect(Collectors.toMap(like -> like.getPost().getId(), like -> like));
        Map<Long, PostBookmark> postBookmarkMap = postBookmarks.stream()
                .collect(Collectors.toMap(bookmark -> bookmark.getPost().getId(), bookmark -> bookmark));

        // TODO : 좋아요 수, 북마크 수, 조회수 동시성 제어
        postsPage.getContent().forEach(post -> {
            PostLike postLike = postLikeMap.get(post.getId());
            PostBookmark postBookmark = postBookmarkMap.get(post.getId());
            boolean like = (postLike == null) ? false : postLike.getLiked().isIdentifier();
            boolean bookmark = (postBookmark == null) ? false : postBookmark.getBookmarked().isIdentifier();
            Integer commentCount = post.getCommentList().size();
            postInfos.add(BoardConverter.toGetPostInfo(post, bookmark, like, commentCount));
        });

        return BoardConverter.toGetPostListDTO(totalCount, postInfos);
    }

    @Override
    @Transactional(readOnly = true)
    public BoardResponseDTO.GetQuestionListDTO getQuestionList(Long lastQuestionId, int size, String sortBy, String category) {
        Member member = authCommandService.getMember();
        PageRequest pageRequest = PageRequest.of(0, size);
        Long categoryId = QuestionCategory.getIdentifier(category);
        Integer totalCount = questionRepository.countAllByCategoryIdAndStatus(categoryId, Status.ACTIVE);
        Slice<Question> questionsPage;

        if (lastQuestionId == 0) {
            questionsPage = switch (sortBy) {
                case "likes" -> questionRepository.findSliceByIdLessThanOrderByLikeCountDesc(categoryId, pageRequest);
                case "views" -> questionRepository.findSliceByIdLessThanOrderByViewCountDesc(categoryId, pageRequest);
                default -> questionRepository.findSliceByIdLessThanOrderByCreatedAtDesc(categoryId, pageRequest);
            };
        } else {
            questionsPage = switch (sortBy) {
                case "likes" -> questionRepository.findSliceByIdLessThanOrderByLikeCountDescWithLastQuestionId(categoryId, lastQuestionId, pageRequest);
                case "views" -> questionRepository.findSliceByIdLessThanOrderByViewCountDescWithLastQuestionId(categoryId, lastQuestionId, pageRequest);
                default -> questionRepository.findSliceByIdLessThanOrderByCreatedAtDescWithLastQuestionId(categoryId, lastQuestionId, pageRequest);
            };
        }

        List<BoardResponseDTO.GetQuestionListDTO.QuestionInfo> questionInfos = new ArrayList<>();

        List<QuestionLike> questionLikes = questionLikeRepository.findByQuestionInAndStatus(questionsPage.getContent(), Status.ACTIVE);
        List<QuestionBookmark> questionBookmarks = questionBookmarkRepository.findByQuestionInAndStatus(questionsPage.getContent(), Status.ACTIVE);

        Map<Long, QuestionLike> questionLikeMap = questionLikes.stream()
                .collect(Collectors.toMap(like -> like.getQuestion().getId(), like -> like));
        Map<Long, QuestionBookmark> questionBookmarkMap = questionBookmarks.stream()
                .collect(Collectors.toMap(bookmark -> bookmark.getQuestion().getId(), bookmark -> bookmark));

        questionsPage.getContent().forEach(question -> {
            Answer selectedTeacher = answerRepository.findByQuestionIdAndSelected(question.getId(), BooleanType.T)
                    .orElse(null);
            QuestionLike questionLike = questionLikeMap.get(question.getId());
            QuestionBookmark questionBookmark = questionBookmarkMap.get(question.getId());
            boolean liked = (questionLike == null) ? false : questionLike.getLiked().isIdentifier();
            boolean bookmarked = (questionBookmark == null) ? false : questionBookmark.getBookmarked().isIdentifier();
            Integer answerCount = question.getAnswerList().size();
            questionInfos.add(BoardConverter.toGetQuestionInfo(question, selectedTeacher, liked, bookmarked, answerCount));
        });

        return BoardConverter.toGetQuestionListDTO(totalCount, questionInfos);
    }

    @Override
    public BoardResponseDTO.SearchQuestionDTO searchQuestion(String keyword, Long lastQuestionId, int size) {
        Member member = authCommandService.getMember();
        PageRequest pageRequest = PageRequest.of(0, size);
        Integer totalCount = questionRepository.countAllByTitleContainingAndStatus(keyword, Status.ACTIVE);
        Slice<Question> questionsPage;

        if (lastQuestionId == 0) {
            questionsPage = questionRepository.findSliceByTitleContainingOrderByCreatedAtDesc(keyword, pageRequest);
        } else {
            questionsPage = questionRepository.findSliceByTitleContainingOrderByCreatedAtDescWithLastQuestionId(keyword, lastQuestionId, pageRequest);
        }

        List<BoardResponseDTO.SearchQuestionDTO.QuestionInfo> questionInfos = new ArrayList<>();

        List<QuestionLike> questionLikes = questionLikeRepository.findByQuestionInAndStatus(questionsPage.getContent(), Status.ACTIVE);
        List<QuestionBookmark> questionBookmarks = questionBookmarkRepository.findByQuestionInAndStatus(questionsPage.getContent(), Status.ACTIVE);

        Map<Long, QuestionLike> questionLikeMap = questionLikes.stream()
                .collect(Collectors.toMap(like -> like.getQuestion().getId(), like -> like));
        Map<Long, QuestionBookmark> questionBookmarkMap = questionBookmarks.stream()
                .collect(Collectors.toMap(bookmark -> bookmark.getQuestion().getId(), bookmark -> bookmark));

        questionsPage.getContent().forEach(question -> {
            Answer selectedTeacher = answerRepository.findByQuestionIdAndSelected(question.getId(), BooleanType.T)
                    .orElse(null);
            QuestionLike questionLike = questionLikeMap.get(question.getId());
            QuestionBookmark questionBookmark = questionBookmarkMap.get(question.getId());
            boolean liked = (questionLike == null) ? false : questionLike.getLiked().isIdentifier();
            boolean bookmarked = (questionBookmark == null) ? false : questionBookmark.getBookmarked().isIdentifier();
            Integer answerCount = question.getAnswerList().size();
            questionInfos.add(BoardConverter.toSearchQuestionInfo(question, selectedTeacher, liked, bookmarked, answerCount));
        });

        return BoardConverter.toSearchQuestionDTO(totalCount, questionInfos);
    }
}
