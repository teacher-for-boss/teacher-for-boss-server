package kr.co.teacherforboss.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import kr.co.teacherforboss.validation.validator.CheckFirstEntryNotNullValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = CheckFirstEntryNotNullValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckFirstEntryNotNull {
    String message() default "첫 번째 항목은 필수입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
