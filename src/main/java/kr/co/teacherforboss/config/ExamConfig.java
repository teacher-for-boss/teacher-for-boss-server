package kr.co.teacherforboss.config;

import kr.co.teacherforboss.domain.enums.ExamQuarter;
import kr.co.teacherforboss.domain.enums.ExamType;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class ExamConfig {

    public static final ExamType EXAM_TYPE = ExamType.MID;
    public static final ExamQuarter EXAM_QUARTER = ExamQuarter.QUARTER2;
}
