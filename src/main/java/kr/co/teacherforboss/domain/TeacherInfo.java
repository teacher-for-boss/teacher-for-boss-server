package kr.co.teacherforboss.domain;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import kr.co.teacherforboss.domain.common.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TeacherInfo extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "memberId")
	private Member member;

	@NotNull
	@Column(length = 100)
	private String businessNum;

	@NotNull
	@Column(length = 20)
	private String representative;

	@Column(updatable = false, nullable = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Getter
	private LocalDateTime openDate;

	@NotNull
	@Column(length = 20)
	private String field;

	@NotNull
	@Column
	private Integer career;

	@NotNull
	@Column(length = 40)
	private String introduction;

	@NotNull
	@Column(length = 30)
	private String keywords;

	// TODO: 티쳐 level - enum으로 관리할건지?

}
