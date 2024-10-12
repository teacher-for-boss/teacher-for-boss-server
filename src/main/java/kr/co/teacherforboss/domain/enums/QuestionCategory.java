package kr.co.teacherforboss.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuestionCategory {

	ALL(0, "전체"),
	TAX(1, "세무"),
	LABOR(2, "노무"),
	KNOW_HOW(3, "노하우"),
	MARKETING(4, "마케팅"),
	HYGIENE(5, "위생"),
	COMMERCIAL_RIGHTS(6, "상권"),
	INTERIOR(7, "인테리어")
	;

	private final int identifier;
	private final String name;

	public static long getIdentifier(String name) {
		switch (name) {
			case "세무": return 1;
			case "노무": return 2;
			case "노하우": return 3;
			case "마케팅": return 4;
			case "위생": return 5;
			case "상권": return 6;
			case "인테리어": return 7;
			default: return 0;
		}

	}
}
