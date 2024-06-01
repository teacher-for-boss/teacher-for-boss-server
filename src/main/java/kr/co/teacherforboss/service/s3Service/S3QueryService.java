package kr.co.teacherforboss.service.s3Service;

import kr.co.teacherforboss.web.dto.BoardRequestDTO;
import kr.co.teacherforboss.web.dto.BoardResponseDTO;
import kr.co.teacherforboss.web.dto.S3RequestDTO;
import kr.co.teacherforboss.web.dto.S3ResponseDTO;

public interface S3QueryService {
	S3ResponseDTO.GetPresignedUrlDTO getPresignedUrl(S3RequestDTO.GetPresignedUrlDTO request);
}
