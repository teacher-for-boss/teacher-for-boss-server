package kr.co.teacherforboss.service.s3Service;

import kr.co.teacherforboss.web.dto.S3ResponseDTO;

public interface S3QueryService {
	S3ResponseDTO.GetPresignedUrlDTO getPresignedUrl(String origin, String uuid, Integer lastIndex, Integer imageCount);
}
