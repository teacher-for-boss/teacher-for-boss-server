package kr.co.teacherforboss.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import kr.co.teacherforboss.validation.annotation.CheckStringLengthLimit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CheckStringLengthLimitValidator implements ConstraintValidator<CheckStringLengthLimit, Map<String, Object>> {

    private String message;
    private int max;

    @Override
    public void initialize(CheckStringLengthLimit constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.message = constraintAnnotation.message();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Map<String, Object> value, ConstraintValidatorContext context) {
        boolean isValid = true;

        if (value != null) {
            for (Object entryValue : value.values()) {
                if (entryValue instanceof String && ((String) entryValue).length() > max) {
                    isValid = false;
                    break;
                }
            }
        }

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }

        return isValid;
    }
}
