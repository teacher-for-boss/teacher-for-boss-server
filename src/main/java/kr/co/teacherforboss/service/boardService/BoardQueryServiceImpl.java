package kr.co.teacherforboss.service.boardService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.BoardHandler;
import kr.co.teacherforboss.converter.BoardConverter;
import kr.co.teacherforboss.domain.Answer;
import kr.co.teacherforboss.domain.AnswerLike;
import kr.co.teacherforboss.domain.Comment;
import kr.co.teacherforboss.domain.CommentLike;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.PostBookmark;
import kr.co.teacherforboss.domain.PostLike;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.QuestionBookmark;
import kr.co.teacherforboss.domain.QuestionLike;
import kr.co.teacherforboss.domain.TeacherInfo;
import kr.co.teacherforboss.domain.common.BaseEntity;
import kr.co.teacherforboss.domain.enums.BooleanType;
import kr.co.teacherforboss.domain.enums.QuestionCategory;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.AnswerLikeRepository;
import kr.co.teacherforboss.repository.AnswerRepository;
import kr.co.teacherforboss.repository.CommentLikeRepository;
import kr.co.teacherforboss.repository.CommentRepository;
import kr.co.teacherforboss.repository.PostBookmarkRepository;
import kr.co.teacherforboss.repository.PostLikeRepository;
import kr.co.teacherforboss.repository.PostRepository;
import kr.co.teacherforboss.repository.QuestionBookmarkRepository;
import kr.co.teacherforboss.repository.QuestionLikeRepository;
import kr.co.teacherforboss.repository.QuestionRepository;
import kr.co.teacherforboss.repository.TeacherInfoRepository;
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
    private final AnswerLikeRepository answerLikeRepository;
    private final TeacherInfoRepository teacherInfoRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    @Override
    @Transactional(readOnly = true)
    public BoardResponseDTO.GetPostDTO getPost(Long postId) {
        Member member = authCommandService.getMember();
        Post post = postRepository.findByIdAndStatus(postId, Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.POST_NOT_FOUND))
                .increaseViewCount();

        boolean liked = false;
        boolean bookmarked = false;
        List<String> hashtagList = null;
        boolean isMine = member.equals(post.getMember());

        PostLike postLike = postLikeRepository.findByPostIdAndMemberIdAndStatus(post.getId(), member.getId(), Status.ACTIVE).orElse(null);
        if (postLike != null) {
            liked = postLike.getLiked().isIdentifier();
        }

        PostBookmark postBookmark = postBookmarkRepository.findByPostIdAndMemberIdAndStatus(post.getId(), member.getId(), Status.ACTIVE).orElse(null);
        if (postBookmark != null) {
            bookmarked = postBookmark.getBookmarked().isIdentifier();
        }

        postRepository.save(post);
        return BoardConverter.toGetPostDTO(post, liked, bookmarked, isMine);
    }

    @Override
    @Transactional(readOnly = true)
    public BoardResponseDTO.GetPostsDTO getPosts(Long lastPostId, int size, String sortBy) {
        Member member = authCommandService.getMember();
        PageRequest pageRequest = PageRequest.of(0, size);
        Slice<Post> posts;

        if (lastPostId == 0) {
            posts = switch (sortBy) {
                case "likes" -> postRepository.findSliceByStatusOrderByLikeCountDesc(Status.ACTIVE, pageRequest);
                case "views" -> postRepository.findSliceByStatusOrderByViewCountDesc(Status.ACTIVE, pageRequest);
                default -> postRepository.findSliceByStatusOrderByCreatedAtDesc(Status.ACTIVE, pageRequest);
            };
        }
        else {
            posts = switch (sortBy) {
                case "likes" -> postRepository.findSliceByIdLessThanOrderByLikeCountDesc(lastPostId, pageRequest);
                case "views" -> postRepository.findSliceByIdLessThanOrderByViewCountDesc(lastPostId, pageRequest);
                default -> postRepository.findSliceByIdLessThanOrderByCreatedAtDesc(lastPostId, pageRequest);
            };
        }

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

    @Override
    @Transactional(readOnly = true)
    public BoardResponseDTO.GetQuestionDTO getQuestion(Long questionId) {
        Member member = authCommandService.getMember();
        Question question = questionRepository.findByIdAndStatus(questionId, Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.QUESTION_NOT_FOUND));

        boolean isMine = member.equals(question.getMember());
        QuestionLike questionLike = questionLikeRepository.findByQuestionIdAndMemberIdAndStatus(question.getId(), member.getId(), Status.ACTIVE).orElse(null);
        QuestionBookmark questionBookmark = questionBookmarkRepository.findByQuestionIdAndMemberIdAndStatus(question.getId(), member.getId(), Status.ACTIVE).orElse(null);

        return BoardConverter.toGetQuestionDTO(question, questionLike, questionBookmark, isMine);
    }

    @Override
    @Transactional(readOnly = true)
    public BoardResponseDTO.GetAnswersDTO getAnswers(Long questionId, Long lastAnswerId, int size) {
        if (!questionRepository.existsByIdAndStatus(questionId, Status.ACTIVE))
            throw new BoardHandler(ErrorStatus.QUESTION_NOT_FOUND);

        Member member = authCommandService.getMember();

        PageRequest pageRequest = PageRequest.of(0, size);
        Slice<Answer> answers;

        if (lastAnswerId == 0) {
            answers = answerRepository.findSliceByQuestionIdAndStatusOrderByCreatedAtDesc(questionId, Status.ACTIVE, pageRequest);
        }
        else {
            answers = answerRepository.findSliceByIdLessThanAndQuestionIdAndStatusOrderByCreatedAtDesc(lastAnswerId, questionId, pageRequest);
        }


        List<Long> answerIds = answers.stream().map(BaseEntity::getId).toList();
        List<Long> memberIds = answers.stream().map(answer -> answer.getMember().getId()).toList();

        List<AnswerLike> answerLikes = answerLikeRepository.findAllByAnswerIdInAndStatus(answerIds, Status.ACTIVE);
        List<TeacherInfo> teacherInfos = teacherInfoRepository.findAllByMemberIdInAndStatus(memberIds, Status.ACTIVE);

        return BoardConverter.toGetAnswersDTO(answers, answerLikes, teacherInfos, member);
    }

    @Override
    @Transactional
    public BoardResponseDTO.GetCommentsDTO getComments(Long postId, Long lastCommentId, int size) {
        if (!postRepository.existsByIdAndStatus(postId, Status.ACTIVE)) {
            throw new BoardHandler(ErrorStatus.POST_NOT_FOUND);
        }

        Member member = authCommandService.getMember();

        PageRequest pageRequest = PageRequest.of(0, size);
        Slice<Comment> parentComments;

        if (lastCommentId == 0) {
            parentComments = commentRepository.findSliceByPostIdAndParentIdIsNullAndStatusOrderByCreatedAtDesc(postId, pageRequest, Status.ACTIVE);
        } else {
            parentComments = commentRepository.findSliceByIdLessThanAndPostIdAndParentIdIsNullOrderByCreatedAtDesc(postId, lastCommentId, pageRequest);
        }

        List<Long> parentCommentIds = parentComments.stream().map(BaseEntity::getId).toList();
        List<Comment> childComments = commentRepository.findAllByPostIdAndParentIdInAndStatus(postId, parentCommentIds);

        List<Comment> allComments = Stream.concat(parentComments.stream(), childComments.stream()).toList();
        List<Long> allCommentIds = allComments.stream().map(BaseEntity::getId).toList();

        List<Long> memberIds = allComments.stream().map(comment -> comment.getMember().getId()).toList();
        List<CommentLike> commentLikes = commentLikeRepository.findAllByCommentIdInAndStatus(allCommentIds, Status.ACTIVE);
        List<TeacherInfo> teacherInfos = teacherInfoRepository.findAllByMemberIdInAndStatus(memberIds, Status.ACTIVE);

        return BoardConverter.toGetCommentsDTO(member, parentComments, childComments, commentLikes, teacherInfos);
    }


  @Override
  @Transactional(readOnly = true)
    public BoardResponseDTO.GetQuestionsDTO getQuestions(Long lastQuestionId, int size, String sortBy, String category) {
        Member member = authCommandService.getMember();
        PageRequest pageRequest = PageRequest.of(0, size);
        // TODO : 카테고리 전체 조회가 안됨 -> 곧 수정할게요
        Long categoryId = QuestionCategory.getIdentifier(category);
        Slice<Question> questionsPage;

        if (lastQuestionId == 0) {
            questionsPage = switch (sortBy) {
                case "likes" -> questionRepository.findSliceByCategoryIdAndStatusOrderByLikeCountDescCreatedAtDesc(categoryId, Status.ACTIVE, pageRequest);
                case "views" -> questionRepository.findSliceByCategoryIdAndStatusOrderByViewCountDescCreatedAtDesc(categoryId, Status.ACTIVE, pageRequest);
                default -> questionRepository.findSliceByCategoryIdAndStatusOrderByCreatedAtDesc(categoryId, Status.ACTIVE, pageRequest);
            };
        } else {
            questionsPage = switch (sortBy) {
                case "likes" -> questionRepository.findSliceByIdLessThanOrderByLikeCountDesc(categoryId, lastQuestionId, pageRequest);
                case "views" -> questionRepository.findSliceByIdLessThanOrderByViewCountDesc(categoryId, lastQuestionId, pageRequest);
                default -> questionRepository.findSliceByIdLessThanOrderByCreatedAtDesc(categoryId, lastQuestionId, pageRequest);
            };
        }

        List<QuestionLike> questionLikes = questionLikeRepository.findByQuestionInAndMemberIdAndStatus(questionsPage.getContent(), member.getId(), Status.ACTIVE);
        List<QuestionBookmark> questionBookmarks = questionBookmarkRepository.findByQuestionInAndMemberIdAndStatus(questionsPage.getContent(), member.getId(), Status.ACTIVE);
        List<Answer> selectedAnswers = answerRepository.findByQuestionInAndSelected(questionsPage.getContent(), BooleanType.T);

        Map<Long, QuestionLike> questionLikeMap = questionLikes.stream()
                .collect(Collectors.toMap(like -> like.getQuestion().getId(), like -> like));
        Map<Long, QuestionBookmark> questionBookmarkMap = questionBookmarks.stream()
                .collect(Collectors.toMap(bookmark -> bookmark.getQuestion().getId(), bookmark -> bookmark));
        Map<Long, Answer> selectedAnswerMap = selectedAnswers.stream()
                .collect(Collectors.toMap(answer -> answer.getQuestion().getId(), answer -> answer));

        return BoardConverter.toGetQuestionsDTO(questionsPage, questionLikeMap, questionBookmarkMap, selectedAnswerMap);
    }

    @Override
    @Transactional(readOnly = true)
    public BoardResponseDTO.GetPostsDTO searchPosts(String keyword, Long lastPostId, int size) {
        Member member = authCommandService.getMember();

        PageRequest pageRequest = PageRequest.of(0, size);
        Slice<Post> posts;

        posts = lastPostId == 0 ?
                postRepository.findSliceByTitleContainingOrContentContainingAndStatusOrderByCreatedAtDesc(keyword, keyword, Status.ACTIVE, pageRequest)
                : postRepository.findSliceByIdLessThanAndKeywordOrderByCreatedAtDesc(keyword, lastPostId, pageRequest);

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
