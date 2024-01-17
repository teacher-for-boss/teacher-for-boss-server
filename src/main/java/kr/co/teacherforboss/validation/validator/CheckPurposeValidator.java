package kr.co.teacherforboss.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.domain.enums.Purpose;
import kr.co.teacherforboss.validation.annotation.CheckPurpose;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckPurposeValidator implements ConstraintValidator<CheckPurpose, Integer> {

    @Override
    public void initialize(CheckPurpose constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        boolean isValid = !Purpose.of(value).equals(Purpose.NONE);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.INVALID_MAIL_PURPOSE.toString()).addConstraintViolation();
        }

        return isValid;

    }
}
