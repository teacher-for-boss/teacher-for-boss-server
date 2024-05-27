package kr.co.teacherforboss.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class S3RequestDTO {

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class GetPresignedUrlDTO {

		@NotNull(message = "이미지 저장 목적은 필수 입력값입니다.")
		String type;

		@NotNull(message = "고유번호는 필수 입력값입니다.")
		Long id;

		@NotNull(message = "저장할 이미지 개수는 필수 입력값입니다.")
		Integer imageCount;
	}
}
