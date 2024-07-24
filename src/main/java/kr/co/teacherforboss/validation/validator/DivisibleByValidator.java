package kr.co.teacherforboss.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import kr.co.teacherforboss.validation.annotation.CheckTrueOrFalse;
import kr.co.teacherforboss.validation.annotation.DivisibleBy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DivisibleByValidator implements ConstraintValidator<DivisibleBy, Integer> {

    private String message;
    private double divisor;

    @Override
    public void initialize(DivisibleBy constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.message = constraintAnnotation.message();
        this.divisor = constraintAnnotation.divisor();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        boolean isValid = value != null && value % divisor == 0;

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }

        return isValid;

    }
}
