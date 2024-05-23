package kr.co.teacherforboss.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import kr.co.teacherforboss.validation.validator.CheckRoleValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = CheckRoleValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckRole {
    String message() default "유효하지 않은 역할 값입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
