package kr.co.teacherforboss.domain.vo.questionVO;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"  // JSON에 "type" 필드를 추가하여 어떤 서브 클래스인지 지정
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = QuestionExtraData.MarketData.class, name = "market"),
        @JsonSubTypes.Type(value = QuestionExtraData.TaxData.class, name = "tax"),
        @JsonSubTypes.Type(value = QuestionExtraData.LaborData.class, name = "labor")
})
public class QuestionExtraData {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MarketData extends QuestionExtraData {
        private String bossType;
        private String businessType;
        private String location;
        private String customerType;
        private String storeInfo;
        private String budget;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TaxData extends QuestionExtraData {
        private String taxFilingStatus;
        private String businessInfo;
        private String branchInfo;
        private String employeeManagement;
        private String purchaseEvidence;
        private String salesScale;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LaborData extends QuestionExtraData {
        private String contractStatus;
        private String businessClassification;
        private String employmentTypeAndDuration;
        private String workAndBreakHours;
        private String salaryAndAllowance;
        private String statutoryBenefits;
    }
}
