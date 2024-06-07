package kr.co.teacherforboss.service.boardService;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.AuthHandler;
import kr.co.teacherforboss.apiPayload.exception.handler.BoardHandler;
import kr.co.teacherforboss.converter.BoardConverter;
import kr.co.teacherforboss.domain.Hashtag;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.PostHashtag;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.HashtagRepository;
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
    public Post editPost(Long postId, BoardRequestDTO.SavePostDTO request) {
        Post post = postRepository.findByIdAndStatus(postId, Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.POST_NOT_FOUND));

        Member member = authCommandService.getMember();

        if(post.getMember().getId() != member.getId()) {
            throw new AuthHandler(ErrorStatus.ACCESS_DENIED);
        }

        Post modifyPost = BoardConverter.toPost(request, member);
        post.setTitle(modifyPost.getTitle());
        post.setContent(modifyPost.getContent());
        post.setImageUrl(modifyPost.getImageUrl());

        editPostHashtags(post, request.getHashtagList());

        return post;
    }

    private void editPostHashtags(Post post, List<String> newHashtagList) {
        List<PostHashtag> postHashtagList = post.getHashtagList();

        List<PostHashtag> newPostHashtagList = new ArrayList<>();
        for (String tag : newHashtagList) {
            Hashtag newHashtag = BoardConverter.toHashtag(tag);
            PostHashtag newPostHashtag = BoardConverter.toPostHashtag(post, newHashtag);
            if (!hashtagRepository.existsByNameAndStatus(tag, Status.ACTIVE)) {
                hashtagRepository.save(newHashtag);
                postHashtagRepository.save(newPostHashtag);
            }
            newPostHashtagList.add(newPostHashtag);
        }

        postHashtagList.removeAll(newPostHashtagList);
        for (PostHashtag postHashtag : postHashtagList) {
            if (postHashtag.getStatus() == Status.ACTIVE) {
                postHashtag.setStatus(Status.INACTIVE);
            }
        }
    }
}