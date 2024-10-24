package kr.co.teacherforboss.service.s3Service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import kr.co.teacherforboss.config.AwsS3Config;
import kr.co.teacherforboss.web.dto.S3ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3QueryServiceImpl implements S3QueryService {

	private final AmazonS3 amazonS3;

	private final long URL_EXPIRATION = 1000 * 60 * 10;	// 600s

	@Override
	public S3ResponseDTO.GetPresignedUrlDTO getPresignedUrl(String origin, String uuid, Integer lastIndex, Integer imageCount) {
		List<String> presignedUrlList = new ArrayList<>();
		String imageUuid = (uuid == null) ? createUuid() : uuid;

		for (int index = lastIndex + 1; index <= lastIndex + imageCount; index++) {
			String fileName = String.format("%s/%s_%d", origin, imageUuid, index);

			GeneratePresignedUrlRequest generatePresignedUrlRequest = getGeneratePresignedUrlRequest(AwsS3Config.BUCKET_NAME, fileName);
			URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

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

		generatePresignedUrlRequest.addRequestParameter(Headers.S3_CANNED_ACL, CannedAccessControlList.PublicRead.toString());

		return generatePresignedUrlRequest;
	}

	private Date getPresignedUrlExpiration() {
		return new Date(System.currentTimeMillis() + URL_EXPIRATION);
	}

	private String createUuid() {
		return UUID.randomUUID().toString();
	}
}
