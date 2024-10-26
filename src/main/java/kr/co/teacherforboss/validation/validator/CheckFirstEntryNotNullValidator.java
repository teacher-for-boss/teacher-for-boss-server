package kr.co.teacherforboss.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import kr.co.teacherforboss.validation.annotation.CheckFirstEntryNotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CheckFirstEntryNotNullValidator implements ConstraintValidator<CheckFirstEntryNotNull, Map<String, Object>> {

    private String message;

    @Override
    public void initialize(CheckFirstEntryNotNull constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Map<String, Object> value, ConstraintValidatorContext context) {
        boolean isValid = value != null;

        if (isValid && !value.isEmpty()) {
            Object firstEntry = value.entrySet().iterator().next().getValue();
            if (firstEntry instanceof Number) {
                isValid = true;
            } else {
                isValid = false;
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            }
        } else if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }

        return isValid;
    }
}
