package kr.co.teacherforboss.service.boardService;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.BoardHandler;
import kr.co.teacherforboss.converter.BoardConverter;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.PostBookmark;
import kr.co.teacherforboss.domain.PostLike;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.CommentRepository;
import kr.co.teacherforboss.repository.PostBookmarkRepository;
import kr.co.teacherforboss.repository.PostLikeRepository;
import kr.co.teacherforboss.repository.PostRepository;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.web.dto.BoardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class BoardQueryServiceImpl implements BoardQueryService {

    private final AuthCommandService authCommandService;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostBookmarkRepository postBookmarkRepository;
    private final CommentRepository commentRepository;


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
    public BoardResponseDTO.GetPostListDTO getPostList(Long lastPostId, int size, String sortBy) {
        Member member = authCommandService.getMember();
        PageRequest pageRequest = PageRequest.of(0, size);
        Integer totalCount = postRepository.countAllByStatus(Status.ACTIVE);
        Slice<Post> postsPage;

        if (lastPostId == 0) {
            postsPage = switch (sortBy) {
                case "likes" -> postRepository.findSliceByIdLessThanOrderByLikeCountDesc(lastPostId, pageRequest);
                case "views" -> postRepository.findSliceByIdLessThanOrderByViewCountDesc(lastPostId, pageRequest);
                default -> postRepository.findSliceByIdLessThanOrderByCreatedAtDesc(lastPostId, pageRequest);
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
        // TODO : 좋아요 수, 북마크 수, 조회수 동시성 제어
        postsPage.getContent().forEach(post -> {
            boolean like = false;
            boolean bookmark = false;
            PostLike postLike = postLikeRepository.findByPostAndMemberAndStatus(post, member, Status.ACTIVE);
            PostBookmark postBookmark = postBookmarkRepository.findByPostAndMemberAndStatus(post, member, Status.ACTIVE);
            if (postLike != null) like = postLike.getLiked().isIdentifier();
            if (postBookmark != null) bookmark = postBookmark.getBookmarked().isIdentifier();
            Integer commentCount = commentRepository.countAllByPostAndStatus(post, Status.ACTIVE);
            postInfos.add(BoardConverter.toGetPostInfo(post, bookmark, like, commentCount));
        });

        return BoardConverter.toGetPostListDTO(totalCount, postInfos);
    }
}
