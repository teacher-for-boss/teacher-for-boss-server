package kr.co.teacherforboss.service.s3Service;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

	@Value("${cloud.s3.bucket}")
	private String bucket;
	private final long URL_EXPIRATION = 1000 * 60 * 2;	// 120s
	private final String FILE_DATETIME_PATTERN = "yyyy-MM-dd-HH-mm-ss";

	@Override
	public S3ResponseDTO.GetPresignedUrlDTO getPresignedUrl(S3RequestDTO.GetPresignedUrlDTO request) {
		LocalDateTime timestamp = LocalDateTime.now();
		String type = request.getType();
		// log.info("Image File Pattern Timestamp => " + timestamp);

		List<String> presignedUrlList = new ArrayList<>();

		for (int index = 1; index <= request.getImageCount(); index++) {

			String fileName = String.format("%s/%s/%s_%d", type, request.getId(), timestamp.format(DateTimeFormatter.ofPattern(FILE_DATETIME_PATTERN)), index);

			GeneratePresignedUrlRequest generatePresignedUrlRequest = getGeneratePresignedUrlRequest(bucket, fileName);
			URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

			// log.info("File Name => " + fileName);

			presignedUrlList.add(url.toString());
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
}
