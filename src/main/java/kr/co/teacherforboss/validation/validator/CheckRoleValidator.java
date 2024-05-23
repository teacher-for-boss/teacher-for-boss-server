package kr.co.teacherforboss.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.domain.enums.Purpose;
import kr.co.teacherforboss.domain.enums.Role;
import kr.co.teacherforboss.validation.annotation.CheckRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckRoleValidator implements ConstraintValidator<CheckRole, Integer> {

    private String message;

    @Override
    public void initialize(CheckRole constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        boolean isValid = Role.of(value).equals(Role.BOSS) || Role.of(value).equals(Role.TEACHER);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }

        return isValid;
    }
}
