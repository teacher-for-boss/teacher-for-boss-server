package kr.co.teacherforboss.service.boardService;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.BoardHandler;
import kr.co.teacherforboss.converter.BoardConverter;
import kr.co.teacherforboss.domain.Category;
import kr.co.teacherforboss.domain.Hashtag;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.PostBookmark;
import kr.co.teacherforboss.domain.PostHashtag;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.QuestionHashtag;
import kr.co.teacherforboss.domain.PostLike;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.QuestionHashtag;
import kr.co.teacherforboss.domain.enums.BooleanType;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.QuestionHashtag;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.CategoryRepository;
import kr.co.teacherforboss.repository.HashtagRepository;
import kr.co.teacherforboss.repository.PostBookmarkRepository;
import kr.co.teacherforboss.repository.PostHashtagRepository;
import kr.co.teacherforboss.repository.PostLikeRepository;
import kr.co.teacherforboss.repository.PostRepository;
import kr.co.teacherforboss.repository.QuestionHashtagRepository;
import kr.co.teacherforboss.repository.QuestionRepository;
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
    private final QuestionRepository questionRepository;
    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final QuestionHashtagRepository questionHashtagRepository;
    private final CategoryRepository categoryRepository;
    private final PostBookmarkRepository postBookmarkRepository;
    private final PostLikeRepository postLikeRepository;

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
    public Question saveQuestion(BoardRequestDTO.SaveQuestionDTO request) {
        Member member = authCommandService.getMember();
        Category category = categoryRepository.findByIdAndStatus(request.getCategoryId(), Status.ACTIVE);
        Question question = BoardConverter.toQuestion(request, member, category);

        List<QuestionHashtag> questionHashtags = new ArrayList<>();
        if (request.getHashtagList() != null) {
            Set<String> hashtags = new HashSet<>(request.getHashtagList());
            for (String tag : hashtags) {
                Hashtag hashtag = hashtagRepository.findByNameAndStatus(tag, Status.ACTIVE);
                if (hashtag == null) {
                    hashtag = hashtagRepository.save(BoardConverter.toHashtag(tag));
                }
                QuestionHashtag questionHashtag = BoardConverter.toQuestionHashtag(question, hashtag);
                questionHashtags.add(questionHashtag);
            }
        }
        questionRepository.save(question);
        questionHashtagRepository.saveAll(questionHashtags);

        return question;
    }


    @Override
    @Transactional
    public PostBookmark savePostBookmark(Long postId) {
        Member member = authCommandService.getMember();
        Post post = postRepository.findByIdAndStatus(postId, Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.POST_NOT_FOUND));
        PostBookmark bookmark = postBookmarkRepository.findByPostAndMemberAndStatus(post, member, Status.ACTIVE);

        if (bookmark == null) {
            bookmark = BoardConverter.toSavePostBookmark(post, member);
        }
        bookmark.toggleBookmarked();
        postBookmarkRepository.save(bookmark);
        return bookmark;
    }

    @Override
    @Transactional
    public PostLike savePostLike(long postId) {
        Member member = authCommandService.getMember();
        Post post = postRepository.findByIdAndStatus(postId, Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.POST_NOT_FOUND));
        PostLike like = postLikeRepository.findByPostAndMemberAndStatus(post, member, Status.ACTIVE);

        if (like == null) {
            like = BoardConverter.toPostLike(post, member);
        }
        like.toggleLiked();
        postLikeRepository.save(like);
        return like;
    }

    @Override
    @Transactional
    public Question editQuestion(Long questionId, BoardRequestDTO.EditQuestionDTO request) {
        Member member = authCommandService.getMember();
        Category category = categoryRepository.findAllByIdAndStatus(request.getCategoryId(), Status.ACTIVE);
        Question editQuestion = questionRepository.findById(questionId).get().editQuestion(request, category);

        // TODO : 수정되고 난 후 아예 안 쓰이는 해시태그 비활성화?
        questionHashtagRepository.deleteAllByQuestionId(questionId);
        List<QuestionHashtag> editQuestionHashtags = new ArrayList<>();
        if (request.getHashtagList() != null) {
            Set<String> editHashtags = new HashSet<>(request.getHashtagList());
            for (String tag : editHashtags) {
                Hashtag hashtag = hashtagRepository.findByNameAndStatus(tag, Status.ACTIVE);
                if (hashtag == null) {
                    hashtag = hashtagRepository.save(BoardConverter.toHashtag(tag));
                }
                QuestionHashtag questionHashtag = BoardConverter.toQuestionHashtag(editQuestion, hashtag);
                editQuestionHashtags.add(questionHashtag);
            }
        }

        questionRepository.save(editQuestion);
        questionHashtagRepository.saveAll(editQuestionHashtags);

        return editQuestion;
    }
}
