package kr.co.teacherforboss.service.boardService;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.BoardHandler;
import kr.co.teacherforboss.converter.BoardConverter;
import kr.co.teacherforboss.domain.Hashtag;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.PostBookmark;
import kr.co.teacherforboss.domain.PostHashtag;
import kr.co.teacherforboss.domain.enums.BooleanType;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.HashtagRepository;
import kr.co.teacherforboss.repository.PostBookmarkRepository;
import kr.co.teacherforboss.repository.PostHashtagRepository;
import kr.co.teacherforboss.repository.PostRepository;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.web.dto.BoardRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BoardCommandServiceImpl implements BoardCommandService {
    private final AuthCommandService authCommandService;
    private final PostRepository postRepository;
    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final PostBookmarkRepository postBookmarkRepository;

    @Override
    @Transactional
    public Post savePost(BoardRequestDTO.SavePostDTO request) {
        Member member = authCommandService.getMember();
        Post post = BoardConverter.toPost(request, member);
        List<PostHashtag> postHashtags = new ArrayList<>();
        // TODO: 유저가 동시에 같은 해시태그 값을 저장하면?
        if (request.getHashtagList() != null) {
            Set<String> uniqueHashtags = new HashSet<>(request.getHashtagList());
            for (String tag : uniqueHashtags) {
                Hashtag hashtag = hashtagRepository.findByNameAndStatus(tag, Status.ACTIVE);
                if (hashtag == null) {
                    hashtag = hashtagRepository.save(BoardConverter.toHashtag(tag));
                }
                PostHashtag postHashtag = BoardConverter.toPostHashtag(post, hashtag);
                postHashtags.add(postHashtag);
            }
        }
        postRepository.save(post);
        postHashtagRepository.saveAll(postHashtags);
        return post;
    }

    @Override
    @Transactional
    public PostBookmark saveBookmark(Long postId) {
        Member member = authCommandService.getMember();
        Post post = postRepository.findByIdAndStatus(postId, Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.POST_NOT_FOUND));
        PostBookmark bookmark = postBookmarkRepository.findByPostAndMemberAndStatus(post, member, Status.ACTIVE);

        if (bookmark == null) {
            bookmark = BoardConverter.toBookmark(post, member);
        }
        else {
            if (bookmark.getBookmarked().equals(BooleanType.T)) bookmark.setBookmarked(BooleanType.F);
            else bookmark.setBookmarked(BooleanType.T);
        }
        postBookmarkRepository.save(bookmark);
        return bookmark;
    }
}
