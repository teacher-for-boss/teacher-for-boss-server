package kr.co.teacherforboss.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import kr.co.teacherforboss.validation.validator.CheckSurveyValidatorForInteger;
import kr.co.teacherforboss.validation.validator.CheckSurveyValidatorForIntegerList;

@Documented
@Constraint(validatedBy = { CheckSurveyValidatorForInteger.class, CheckSurveyValidatorForIntegerList.class })
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckSurvey {
    String message() default "사전정보 항목이 잘못되었습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int question() default 0;
}
