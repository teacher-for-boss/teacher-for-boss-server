package kr.co.teacherforboss.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import kr.co.teacherforboss.domain.enums.Survey;
import kr.co.teacherforboss.validation.annotation.CheckSurvey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckSurveyValidator implements ConstraintValidator<CheckSurvey, Integer> {

    private String message;
    private int question;

    @Override
    public void initialize(CheckSurvey constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.message = constraintAnnotation.message();
        this.question = constraintAnnotation.question();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        boolean isValid = !Survey.of(question, value).equals(Survey.NONE);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }

        return isValid;

    }
}
