package kr.co.teacherforboss.web.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class S3ResponseDTO {

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class GetPresignedUrlDTO {
		List<String> presignedUrlList;
	}
}
