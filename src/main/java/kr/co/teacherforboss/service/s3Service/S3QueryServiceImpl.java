package kr.co.teacherforboss.service.s3Service;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

import kr.co.teacherforboss.web.dto.S3RequestDTO;
import kr.co.teacherforboss.web.dto.S3ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3QueryServiceImpl implements S3QueryService{

	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;
	private final long URL_EXPIRATION = 1000 * 60 * 2;	// 120s
	private final String FILE_DATETIME_PATTERN = "yyyy-MM-dd-HH-mm-ss";

	@Override
	public S3ResponseDTO.GetPresignedUrlDTO getPresignedUrl(S3RequestDTO.GetPresignedUrlDTO request) {
		String type = request.getType();

		List<String> presignedUrlList = new ArrayList<>();
		String fileName;
		GeneratePresignedUrlRequest generatePresignedUrlRequest;
		URL url;

		switch (type) {
			case "profile":
				fileName = String.format("%s/%s", type, createFileId());

				generatePresignedUrlRequest = getGeneratePresignedUrlRequest(bucket, fileName);
				url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

				presignedUrlList.add(url.toString());
				break;
			default:
				for (int index = 1; index <= request.getImageCount(); index++) {
					LocalDateTime timestamp = LocalDateTime.now();

					fileName = String.format("%s/%s/%s_%d", type, request.getId(), timestamp.format(DateTimeFormatter.ofPattern(FILE_DATETIME_PATTERN)), index);

					generatePresignedUrlRequest = getGeneratePresignedUrlRequest(bucket, fileName);
					url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

					presignedUrlList.add(url.toString());
				}
		}

		return S3ResponseDTO.GetPresignedUrlDTO.builder()
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
		return new Date(System.currentTimeMillis() + URL_EXPIRATION);
	}

	private String createFileId() {
		return UUID.randomUUID().toString();
	}
}
