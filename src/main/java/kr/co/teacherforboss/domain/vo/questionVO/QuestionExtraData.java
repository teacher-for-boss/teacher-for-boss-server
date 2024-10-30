package kr.co.teacherforboss.domain.vo.questionVO;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import kr.co.teacherforboss.domain.Category;
import kr.co.teacherforboss.domain.enums.QuestionExtraDataUserType;
import kr.co.teacherforboss.web.dto.BoardRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"  // JSON에 "type" 필드를 추가하여 어떤 서브 클래스인지 지정
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = QuestionExtraData.MarketData.class, name = "market"), // 노하우, 상권
        @JsonSubTypes.Type(value = QuestionExtraData.TaxData.class, name = "tax"), // 세무
        @JsonSubTypes.Type(value = QuestionExtraData.LaborData.class, name = "labor") // 노무
})
public class QuestionExtraData {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MarketData extends QuestionExtraData {
        private QuestionExtraDataUserType bossType;
        private String businessType;
        private String location;
        private String customerType;
        private String storeInfo;
        private String budget;

        public static MarketData create(QuestionExtraDataUserType userType, BoardRequestDTO.QuestionExtraField extraField) {
            return new MarketData(
                    userType,
                    extraField.getSecondField(),
                    extraField.getThirdField(),
                    extraField.getFourthField(),
                    extraField.getFifthField(),
                    extraField.getSixthField()
            );
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TaxData extends QuestionExtraData {
        private QuestionExtraDataUserType taxBookKeepingStatus;
        private String businessType;
        private String branchInfo;
        private String employeeManagement;
        private String purchaseEvidence;
        private String salesScale;

        public static TaxData create(QuestionExtraDataUserType userType, BoardRequestDTO.QuestionExtraField extraField) {
            return new TaxData(
                    userType,
                    extraField.getSecondField(),
                    extraField.getThirdField(),
                    extraField.getFourthField(),
                    extraField.getFifthField(),
                    extraField.getSixthField()
            );
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LaborData extends QuestionExtraData {
        private QuestionExtraDataUserType contractStatus;
        private String businessType;
        private String employmentTypeAndDuration;
        private String workAndBreakHours;
        private String salaryAndAllowance;
        private String statutoryBenefits;

        public static LaborData create(QuestionExtraDataUserType userType, BoardRequestDTO.QuestionExtraField extraField) {
            return new LaborData(
                    userType,
                    extraField.getSecondField(),
                    extraField.getThirdField(),
                    extraField.getFourthField(),
                    extraField.getFifthField(),
                    extraField.getSixthField()
            );
        }
    }

    public static QuestionExtraData createQuestionExtraField(BoardRequestDTO.QuestionExtraField extraField, Category category) {
        QuestionExtraDataUserType userType = QuestionExtraDataUserType.validateUserType(extraField.getFirstField(), category.getId());
        return switch (category.getId().intValue()) {
            case 3, 6 -> MarketData.create(userType, extraField);
            case 1 -> TaxData.create(userType, extraField);
            case 2 -> LaborData.create(userType, extraField);
            default -> null;
        };
    }
}
