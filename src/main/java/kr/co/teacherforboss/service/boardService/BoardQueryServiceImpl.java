package kr.co.teacherforboss.service.boardService;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.BoardHandler;
import kr.co.teacherforboss.converter.BoardConverter;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.PostBookmark;
import kr.co.teacherforboss.domain.PostLike;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.PostBookmarkRepository;
import kr.co.teacherforboss.repository.PostLikeRepository;
import kr.co.teacherforboss.repository.PostRepository;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.web.dto.BoardRequestDTO;
import kr.co.teacherforboss.web.dto.BoardResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardQueryServiceImpl implements BoardQueryService {

    private final AuthCommandService authCommandService;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostBookmarkRepository postBookmarkRepository;
    private final AmazonS3 amazonS3;

    @Value("${cloud.s3.bucket}")
    private String bucket;

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
    @Transactional
    public BoardResponseDTO.GetPresignedUrlDTO getPresignedUrl(BoardRequestDTO.GetPresignedUrlDTO request) {
        LocalDateTime timestamp = LocalDateTime.now();
        String type = request.getType();
        log.info("image file pattern timestamp => " + timestamp);

        List<String> presignedUrlList = new ArrayList<>();

        for (int index = 1; index <= request.getImageCount(); index++) {

            String fileName = String.format("%s/%s/%s_%d", type, request.getId(), timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")), index);

            GeneratePresignedUrlRequest generatePresignedUrlRequest = getGeneratePresignedUrlRequest(bucket, fileName);
            URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

            log.info("File Name => " + fileName);

            presignedUrlList.add(url.toString());
        }

        return BoardResponseDTO.GetPresignedUrlDTO.builder()
                .presignedUrlList(presignedUrlList)
                .build();
    }

    private GeneratePresignedUrlRequest getGeneratePresignedUrlRequest(String bucket, String fileName) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, fileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(getPresignedUrlExpiration());

        generatePresignedUrlRequest.addRequestParameter(
                Headers.S3_CANNED_ACL,
                CannedAccessControlList.PublicRead.toString()
        );

        return generatePresignedUrlRequest;
    }

    private Date getPresignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 2; // 120 s
        expiration.setTime(expTimeMillis);

        return expiration;
    }

}
