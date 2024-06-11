package kr.co.teacherforboss.web.dto;

import jakarta.annotation.Nullable;
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

		@Nullable
		String uuid;

		@NotNull(message = "마지막 저장 인덱스는 필수 입력값입니다.")
		Integer lastIndex;

		@NotNull(message = "저장할 이미지 개수는 필수 입력값입니다.")
		Integer imageCount;
	}
}
