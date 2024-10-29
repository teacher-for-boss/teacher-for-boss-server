package kr.co.teacherforboss.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuestionExtraDataUserType {

	NONE(0, ""),
	STORE_OWNER(1, "매장 운영 중"),
	ASPIRING_ENTREPRENEUR(2, "예비 자영업자"),
	TAX_FILLING(3, "세무 기장 진행 중"),
	NO_TAX_FILLING(4, "세무 기장 진행X"),
	WITH_CONTRACT(5, "근로계약서 작성"),
	WITHOUT_CONTRACT(6, "근로계약서 미작성")
	;

	private final int identifier;
	private final String userType;

	public static QuestionExtraDataUserType of(int identifier) {
		if (identifier == 1) return STORE_OWNER;
		if (identifier == 2) return ASPIRING_ENTREPRENEUR;
		if (identifier == 3) return TAX_FILLING;
		if (identifier == 4) return NO_TAX_FILLING;
		if (identifier == 5) return WITH_CONTRACT;
		if (identifier == 6) return WITHOUT_CONTRACT;
		return NONE;
	}
}
