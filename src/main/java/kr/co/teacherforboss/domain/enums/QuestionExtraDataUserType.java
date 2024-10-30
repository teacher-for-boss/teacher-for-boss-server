package kr.co.teacherforboss.domain.enums;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.BoardHandler;
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

	public static QuestionExtraDataUserType validate(int identifier, long categoryId) {
		QuestionExtraDataUserType userType = QuestionExtraDataUserType.of(identifier);

		boolean isValidUserType = switch ((int) categoryId) {
			case 3, 6 -> userType == QuestionExtraDataUserType.STORE_OWNER || userType == QuestionExtraDataUserType.ASPIRING_ENTREPRENEUR;
			case 1 -> userType == QuestionExtraDataUserType.TAX_FILLING || userType == QuestionExtraDataUserType.NO_TAX_FILLING;
			case 2 -> userType == QuestionExtraDataUserType.WITH_CONTRACT || userType == QuestionExtraDataUserType.WITHOUT_CONTRACT;
			default -> false;
		};

		if (!isValidUserType) {
			throw new BoardHandler(ErrorStatus.INVALID_EXTRA_CONTENT_FIELDS);
		}
		return userType;
	}
}
