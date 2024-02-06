package kr.co.teacherforboss.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import kr.co.teacherforboss.validation.annotation.CheckTrueOrFalse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckTrueOrFalseValidator implements ConstraintValidator<CheckTrueOrFalse, String> {

    private String message;

    @Override
    public void initialize(CheckTrueOrFalse constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean isValid = value != null && !value.isEmpty() && (value.equals("T") || value.equals("F"));

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }

        return isValid;

    }
}
