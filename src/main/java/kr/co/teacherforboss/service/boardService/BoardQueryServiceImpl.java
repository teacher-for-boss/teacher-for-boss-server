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
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.PostBookmark;
import kr.co.teacherforboss.domain.PostLike;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.QuestionBookmark;
import kr.co.teacherforboss.domain.QuestionLike;
import kr.co.teacherforboss.domain.enums.Status;
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
    public BoardResponseDTO.GetQuestionDTO getQuestion(Long questionId) {
        Member member = authCommandService.getMember();
        Question question = questionRepository.findByIdAndStatus(questionId, Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.QUESTION_NOT_FOUND));

        QuestionLike questionLike = questionLikeRepository.findByQuestionIdAndMemberIdAndStatus(question.getId(), member.getId(), Status.ACTIVE).orElse(null);
        QuestionBookmark questionBookmark = questionBookmarkRepository.findByQuestionIdAndMemberIdAndStatus(question.getId(), member.getId(), Status.ACTIVE).orElse(null);

        List<String> hashtagList = (question.getHashtagList().isEmpty()) ? null : BoardConverter.toQuestionHashtagList(question);

        return BoardConverter.toGetQuestionDTO(question, questionLike, questionBookmark, hashtagList);
    }
}
