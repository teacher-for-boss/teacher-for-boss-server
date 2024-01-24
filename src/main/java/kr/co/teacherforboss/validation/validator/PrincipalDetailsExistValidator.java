package kr.co.teacherforboss.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.config.jwt.PrincipalDetails;
import kr.co.teacherforboss.validation.annotation.ExistPrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PrincipalDetailsExistValidator implements ConstraintValidator<ExistPrincipalDetails, PrincipalDetails> {

    @Override
    public void initialize(ExistPrincipalDetails constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(PrincipalDetails value, ConstraintValidatorContext context) {
        if (value == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.INVALID_JWT_TOKEN.toString()).addConstraintViolation();

            return false;
        }
        return true;
    }
}