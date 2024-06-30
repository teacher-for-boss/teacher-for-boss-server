package kr.co.teacherforboss.service.boardService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kr.co.teacherforboss.converter.CommentConverter;
import kr.co.teacherforboss.domain.Comment;
import kr.co.teacherforboss.repository.CommentLikeRepository;
import kr.co.teacherforboss.repository.CommentRepository;
import kr.co.teacherforboss.repository.QuestionLikeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
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
import kr.co.teacherforboss.domain.QuestionBookmark;
import kr.co.teacherforboss.domain.QuestionHashtag;
import kr.co.teacherforboss.domain.QuestionLike;
import kr.co.teacherforboss.domain.common.BaseEntity;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.AnswerRepository;
import kr.co.teacherforboss.repository.CategoryRepository;
import kr.co.teacherforboss.repository.HashtagRepository;
import kr.co.teacherforboss.repository.PostBookmarkRepository;
import kr.co.teacherforboss.repository.PostHashtagRepository;
import kr.co.teacherforboss.repository.PostLikeRepository;
import kr.co.teacherforboss.repository.PostRepository;
import kr.co.teacherforboss.repository.QuestionBookmarkRepository;
import kr.co.teacherforboss.repository.QuestionHashtagRepository;
import kr.co.teacherforboss.repository.QuestionRepository;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.web.dto.BoardRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final QuestionLikeRepository questionLikeRepository;
    private final AnswerRepository answerRepository;
    private final QuestionBookmarkRepository questionBookmarkRepository;

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
        Member member = authCommandService.getMember();
        Post post = postRepository.findByIdAndMemberIdAndStatus(postId, member.getId(), Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.POST_NOT_FOUND));

        post.editPost(request.getTitle(), request.getContent(), BoardConverter.extractImageIndexs(request.getImageUrlList()));
        editPostHashtags(post, request.getHashtagList());
        return post;
    }

    private void editPostHashtags(Post post, List<String> newHashtagList) {
        List<String> originalHashtagList = post.getHashtags().stream()
                .map(postHashtag -> postHashtag.getHashtag().getName()).toList();

        List<String> hashtagsToBeAdded = new ArrayList<>(newHashtagList);
        hashtagsToBeAdded.removeAll(originalHashtagList);
        hashtagsToBeAdded.forEach(tag -> {
            Hashtag hashtag = hashtagRepository.findByNameAndStatus(tag, Status.ACTIVE);
            if (hashtag == null) {
                hashtag = hashtagRepository.save(BoardConverter.toHashtag(tag));
            }
            PostHashtag postHashtag = BoardConverter.toPostHashtag(post, hashtag);
            postHashtagRepository.save(postHashtag);
        });

        List<String> hashtagsToBeRemoved = new ArrayList<>(originalHashtagList);
        hashtagsToBeRemoved.removeAll(newHashtagList);
        hashtagsToBeRemoved.forEach(tag -> post.getHashtags().stream()
                .filter(postHashtag -> postHashtag.getHashtag().getName().equals(tag))
                .findFirst()
                .ifPresent(PostHashtag::softDelete));
    }

    @Override
    @Transactional
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
    public PostBookmark togglePostBookmark(Long postId) {
        Member member = authCommandService.getMember();
        Post post = postRepository.findByIdAndStatus(postId, Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.POST_NOT_FOUND));
        PostBookmark bookmark = postBookmarkRepository.findByPostIdAndMemberIdAndStatus(post.getId(), member.getId(), Status.ACTIVE)
                .orElse(BoardConverter.toSavePostBookmark(post, member));

        bookmark.toggleBookmarked();
        return postBookmarkRepository.save(bookmark);
    }

    @Override
    @Transactional
    public PostLike togglePostLike(Long postId) {
        Member member = authCommandService.getMember();
        Post post = postRepository.findByIdAndStatus(postId, Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.POST_NOT_FOUND));
        PostLike like = postLikeRepository.findByPostIdAndMemberIdAndStatus(post.getId(), member.getId(), Status.ACTIVE)
                        .orElse(BoardConverter.toPostLike(post, member));
        like.toggleLiked();
        return postLikeRepository.save(like);
    }

    @Override
    @Transactional
    public Post deletePost(Long postId) {
        Member member = authCommandService.getMember();
        Post post = postRepository.findByIdAndMemberIdAndStatus(postId, member.getId(), Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.POST_NOT_FOUND));

        List<Long> comments = post.getComments().stream().map(BaseEntity::getId).toList();
        postHashtagRepository.softDeletePostHashtagByPostId(postId);
        postLikeRepository.softDeletePostLikeByPostId(postId);
        postBookmarkRepository.softDeletePostBookmarksByPostId(postId);

        commentLikeRepository.softDeleteCommentLikeByComments(comments);
        commentRepository.softDeleteCommentsByPostId(postId);
        post.softDelete();
        return postRepository.save(post);
    }

    @Override
    @Transactional
    public Question editQuestion(Long questionId, BoardRequestDTO.EditQuestionDTO request) {
        Member member = authCommandService.getMember();
        Category category = categoryRepository.findByIdAndStatus(request.getCategoryId(), Status.ACTIVE);
        Question editedQuestion = questionRepository.findByIdAndMemberIdAndStatus(questionId, member.getId(), Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.QUESTION_NOT_FOUND))
                .editQuestion(category, request.getTitle(), request.getContent(), BoardConverter.extractImageIndexs(request.getImageUrlList()));

        editedQuestion.getHashtagList().forEach(BaseEntity::softDelete);
        List<QuestionHashtag> editedQuestionHashtags = new ArrayList<>();
        if (request.getHashtagList() != null) {
            Set<String> editHashtags = new HashSet<>(request.getHashtagList());
            for (String tag : editHashtags) {
                Hashtag hashtag = hashtagRepository.findByNameAndStatus(tag, Status.ACTIVE);
                if (hashtag == null) {
                    hashtag = hashtagRepository.save(BoardConverter.toHashtag(tag));
                }
                QuestionHashtag questionHashtag = BoardConverter.toQuestionHashtag(editedQuestion, hashtag);
                editedQuestionHashtags.add(questionHashtag);
            }
        }

        questionRepository.save(editedQuestion);
        questionHashtagRepository.saveAll(editedQuestionHashtags);

        return editedQuestion;
    }

    @Override
    @Transactional
    public Answer saveAnswer(long questionId, BoardRequestDTO.SaveAnswerDTO request) {
        Member member = authCommandService.getMember();
        Question question = questionRepository.findByIdAndStatus(questionId, Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.QUESTION_NOT_FOUND));

        Answer answer = BoardConverter.toAnswer(question, member, request);
        return answerRepository.save(answer);
    }

    @Override
    @Transactional
    public Answer editAnswer(Long questionId, Long answerId, BoardRequestDTO.EditAnswerDTO request) {
        Member member = authCommandService.getMember();
        if (!questionRepository.existsByIdAndStatus(questionId, Status.ACTIVE))
            throw new BoardHandler(ErrorStatus.QUESTION_NOT_FOUND);

        Answer answer = answerRepository.findByIdAndMemberIdAndStatus(answerId, member.getId(), Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.ANSWER_NOT_FOUND));

        return answer.editAnswer(request.getContent(), BoardConverter.extractImageIndexs(request.getImageUrlList()));
    }

    @Override
    @Transactional
    public Question deleteQuestion(Long questionId) {
        Member member = authCommandService.getMember();
        Question questionToDelete = questionRepository.findByIdAndMemberIdAndStatus(questionId, member.getId(), Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.QUESTION_NOT_FOUND));

        questionToDelete.softDelete();
        answerRepository.softDeleteAnswersByQuestionId(questionToDelete.getId());

        return questionToDelete;
    }

    @Override
    public QuestionLike toggleQuestionLike(Long questionId) {
        Member member = authCommandService.getMember();
        Question questionToLike = questionRepository.findByIdAndStatus(questionId, Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.QUESTION_NOT_FOUND));
        QuestionLike questionLike = questionLikeRepository.findByQuestionIdAndMemberIdAndStatus(questionToLike.getId(), member.getId(), Status.ACTIVE)
                .orElse(BoardConverter.toQuestionLike(questionToLike, member));

        questionLike.toggleLiked();
        return questionLikeRepository.save(questionLike);
    }

    @Override
    @Transactional
    public Answer deleteAnswer(Long questionId, Long answerId) {
        Member member = authCommandService.getMember();
        Answer answerToDelete = answerRepository.findByIdAndQuestionIdAndMemberIdAndStatus(answerId, questionId, member.getId(), Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.ANSWER_NOT_FOUND));

        answerToDelete.softDelete();
        return answerToDelete;
    }

    @Override
    public QuestionBookmark toggleQuestionBookmark(Long questionId) {
        Member member = authCommandService.getMember();
        Question questionToBookmark = questionRepository.findByIdAndStatus(questionId, Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.QUESTION_NOT_FOUND));
        QuestionBookmark questionBookmark = questionBookmarkRepository.findByQuestionIdAndMemberIdAndStatus(questionToBookmark.getId(), member.getId(), Status.ACTIVE)
                .orElse(BoardConverter.toQuestionBookmark(questionToBookmark, member));

        questionBookmark.toggleLiked();
        return questionBookmarkRepository.save(questionBookmark);
    }

    @Override
    @Transactional
    public Comment saveComment(Long postId, BoardRequestDTO.SaveCommentDTO request) {
        Member member = authCommandService.getMember();
        Post post = postRepository.findByIdAndStatus(postId, Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.POST_NOT_FOUND));

        Comment parentComment = null;
        if(request.getParentId() != null) {
            parentComment = commentRepository.findByIdAndStatus(request.getParentId(), Status.ACTIVE);
            if (parentComment == null) throw new BoardHandler(ErrorStatus.COMMENT_NOT_FOUND);
        }

        Comment comment = CommentConverter.toCommentDTO(request, member, post, parentComment);
        return commentRepository.save(comment);
    }
}
