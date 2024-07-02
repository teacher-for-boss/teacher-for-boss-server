package kr.co.teacherforboss.service.boardService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.BoardHandler;
import kr.co.teacherforboss.converter.BoardConverter;
import kr.co.teacherforboss.converter.MemberConverter;
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
import kr.co.teacherforboss.domain.enums.Role;
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
import kr.co.teacherforboss.web.dto.MemberResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        String liked = "F";
        String bookmarked = "F";
        List<String> hashtagList = null;
        boolean isMine = member.equals(post.getMember());

        PostLike postLike = postLikeRepository.findByPostIdAndMemberIdAndStatus(post.getId(), member.getId(), Status.ACTIVE).orElse(null);
        if (postLike != null) {
            liked = String.valueOf(postLike.getLiked());
        }

        PostBookmark postBookmark = postBookmarkRepository.findByPostIdAndMemberIdAndStatus(post.getId(), member.getId(), Status.ACTIVE).orElse(null);
        if (postBookmark != null) {
            bookmarked = String.valueOf(postBookmark.getBookmarked());
        }
        if (!post.getHashtags().isEmpty()) {
            hashtagList = BoardConverter.toPostHashtags(post);
        }

        postRepository.save(post);
        return BoardConverter.toGetPostDTO(post, hashtagList, liked, bookmarked, isMine);
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

        PageRequest pageRequest = PageRequest.of(0, size);
        Slice<Answer> answers;

        if (lastAnswerId == 0) {
            answers = answerRepository.findSliceByStatusOrderByCreatedAtDesc(Status.ACTIVE, pageRequest);
        }
        else {
            answers = answerRepository.findSliceByIdLessThanAndStatusOrderByCreatedAtDesc(lastAnswerId, pageRequest);
        }


        List<Long> answerIds = answers.stream().map(BaseEntity::getId).toList();
        List<Long> memberIds = answers.stream().map(answer -> answer.getMember().getId()).toList();

        List<AnswerLike> answerLikes = answerLikeRepository.findAllByAnswerIdInAndStatus(answerIds, Status.ACTIVE);
        List<TeacherInfo> teacherInfos = teacherInfoRepository.findAllByMemberIdInAndStatus(memberIds, Status.ACTIVE);

        return BoardConverter.toGetAnswersDTO(answers, answerLikes, teacherInfos);
    }

    @Override
    @Transactional
    public BoardResponseDTO.GetCommentsDTO getComments(Long postId, Long lastCommentId, int size) {
        if (!postRepository.existsByIdAndStatus(postId, Status.ACTIVE)) {
            throw new BoardHandler(ErrorStatus.POST_NOT_FOUND);
        }

        PageRequest pageRequest = PageRequest.of(0, size);
        Slice<Comment> comments;

        if (lastCommentId == 0) {
            comments = commentRepository.findSliceByPostIdAndParentIsNullAndStatusOrderByCreatedAtDesc(postId, Status.ACTIVE, pageRequest);
        } else {
            comments = commentRepository.findSliceByPostIdAndIdLessThanAndParentIsNullAndStatusOrderByCreatedAtDesc(postId, lastCommentId, Status.ACTIVE, pageRequest);
        }

        List<Long> commentIds = comments.stream().map(BaseEntity::getId).toList();
        List<Long> memberIds = comments.stream().map(comment -> comment.getMember().getId()).toList();

        List<Comment> allComments = commentRepository.findAllByPostIdAndParentIdInAndStatus(postId, commentIds, Status.ACTIVE);

        List<CommentLike> commentLikes = commentLikeRepository.findAllByCommentIdInAndStatus(commentIds, Status.ACTIVE);
        List<TeacherInfo> teacherInfos = teacherInfoRepository.findAllByMemberIdInAndStatus(memberIds, Status.ACTIVE);

        return BoardConverter.toGetCommentsDTO(comments, allComments, commentLikes, teacherInfos);
    }
}
