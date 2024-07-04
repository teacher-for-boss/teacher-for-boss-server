package kr.co.teacherforboss.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuestionCategory {

	ALL(0, "전체"),
	MARKETING(1, "마케팅"),
	HYGIENE(2, "위생"),
	COMMERCIAL_RIGHTS(3, "상권"),
	MANAGEMENT(4, "운영"),
	HUMAN_RESOURCES(5, "직원관리"),
	INTERIOR(6, "인테리어"),
	POLICY(7, "정책")
	;

	private final int identifier;
	private final String name;

	public static long getIdentifier(String name) {
		switch (name) {
			case "마케팅": return 1;
			case "위생": return 2;
			case "상권": return 3;
			case "운영": return 4;
			case "직원관리": return 5;
			case "인테리어": return 6;
			case "정책": return 7;
			default: return 0;
		}

	}
}
