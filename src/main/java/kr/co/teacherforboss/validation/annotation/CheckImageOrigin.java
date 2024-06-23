package kr.co.teacherforboss.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import kr.co.teacherforboss.validation.validator.CheckImageOriginValidator;

@Documented
@Constraint(validatedBy = CheckImageOriginValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckImageOrigin {
    String message() default "이미지 업로드 목적이 잘못되었습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
