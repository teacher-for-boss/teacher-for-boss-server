package kr.co.teacherforboss.service.boardService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.AuthHandler;
import kr.co.teacherforboss.apiPayload.exception.handler.BoardHandler;
import kr.co.teacherforboss.converter.BoardConverter;
import kr.co.teacherforboss.domain.Answer;
import kr.co.teacherforboss.domain.Category;
import kr.co.teacherforboss.domain.Hashtag;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.PostBookmark;
import kr.co.teacherforboss.domain.PostHashtag;
import kr.co.teacherforboss.domain.PostLike;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.QuestionHashtag;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.AnswerRepository;
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
    private final AnswerRepository answerRepository;

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
        List<PostHashtag> existPostHashtags = post.getHashtagList();
        existPostHashtags.forEach(ph -> ph.revertSoftDelete(Status.INACTIVE));

        for (String tag : newHashtagList) {
            Hashtag hashtag = hashtagRepository.findOptionalByNameAndStatus(tag, Status.ACTIVE)
                    .orElseGet(() -> {
                        Hashtag newHashtag = BoardConverter.toHashtag(tag);
                        hashtagRepository.save(newHashtag);
                        return newHashtag;
                    });

            Optional<PostHashtag> existingPostHashtag = existPostHashtags.stream()
                    .filter(ph -> ph.getHashtag().equals(hashtag))
                    .findFirst();

            if (existingPostHashtag.isPresent()) {
                existingPostHashtag.get().revertSoftDelete(Status.ACTIVE);
            } else {
                PostHashtag newPostHashtag = BoardConverter.toPostHashtag(post, hashtag);
                existPostHashtags.add(newPostHashtag);
                postHashtagRepository.save(newPostHashtag);
            }
        }
    }

    @Override
    public Question saveQuestion(BoardRequestDTO.SaveQuestionDTO request) {
        if (request.getImageCount() > 0 && request.getImageTimestamp() == null)
            throw new BoardHandler(ErrorStatus.INVALID_IMAGE_TIMESTAMP);

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
        Category category = categoryRepository.findByIdAndStatus(request.getCategoryId(), Status.ACTIVE);
        Question editQuestion = questionRepository.findById(questionId)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.QUESTION_NOT_FOUND))
                .editQuestion(category, request.getTitle(), request.getContent(), request.getImageCount(), request.getImageTimestamp());

        // TODO : 수정되고 난 후 아예 안 쓰이는 해시태그 비활성화?
        questionHashtagRepository.softDeleteAllByQuestionId(questionId);
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

    @Override
    @Transactional
    public Answer saveAnswer(long questionId, BoardRequestDTO.SaveAnswerDTO request) {
        if (request.getImageCount() > 0 && request.getImageTimestamp() == null)
            throw new BoardHandler(ErrorStatus.INVALID_IMAGE_TIMESTAMP);

        Member member = authCommandService.getMember();
        Question question = questionRepository.findByIdAndStatus(questionId, Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.QUESTION_NOT_FOUND));

        Answer answer = BoardConverter.toAnswer(question, member, request);
        return answerRepository.save(answer);
    }
}
