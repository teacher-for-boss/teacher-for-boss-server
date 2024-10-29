package kr.co.teacherforboss.domain.vo.questionVO;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import kr.co.teacherforboss.domain.enums.QuestionExtraDataUserType;
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

        public static MarketData create(QuestionExtraDataUserType bossType, String businessType, String location, String customerType, String storeInfo, String budget) {
            return new MarketData(bossType, businessType, location, customerType, storeInfo, budget);
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

        public static TaxData create(QuestionExtraDataUserType taxBookKeepingStatus, String businessType, String branchInfo, String employeeManagement, String purchaseEvidence, String salesScale) {
            return new TaxData(taxBookKeepingStatus, businessType, branchInfo, employeeManagement, purchaseEvidence, salesScale);
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

        public static LaborData create(QuestionExtraDataUserType contractStatus, String businessType, String employmentTypeAndDuration, String workAndBreakHours, String salaryAndAllowance, String statutoryBenefits) {
            return new LaborData(contractStatus, businessType, employmentTypeAndDuration, workAndBreakHours, salaryAndAllowance, statutoryBenefits);
        }
    }
}
