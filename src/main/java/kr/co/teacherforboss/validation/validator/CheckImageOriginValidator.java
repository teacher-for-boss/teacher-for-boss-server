package kr.co.teacherforboss.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import kr.co.teacherforboss.domain.enums.ImageOrigin;
import kr.co.teacherforboss.validation.annotation.CheckImageOrigin;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckImageOriginValidator implements ConstraintValidator<CheckImageOrigin, String> {

    private String message;

    @Override
    public void initialize(CheckImageOrigin constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean isValid = value != null && !value.isEmpty() &&
                (value.equals(ImageOrigin.PROFILE.getValue()) ||
                        value.equals(ImageOrigin.POST.getValue()) || value.equals(ImageOrigin.COMMENT.getValue()) ||
                        value.equals(ImageOrigin.QUESTION.getValue()) || value.equals(ImageOrigin.ANSWER.getValue()));

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }

        return isValid;
    }
}
