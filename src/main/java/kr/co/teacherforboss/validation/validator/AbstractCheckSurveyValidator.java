package kr.co.teacherforboss.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import kr.co.teacherforboss.validation.annotation.CheckSurvey;

public abstract class AbstractCheckSurveyValidator<T> implements ConstraintValidator<CheckSurvey, T> {

    private String message;
    protected int question;

    @Override
    public void initialize(CheckSurvey constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.message = constraintAnnotation.message();
        this.question = constraintAnnotation.question();
    }

    @Override
    public boolean isValid(T value, ConstraintValidatorContext context) {
        boolean isValid = compare(value);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }

        return isValid;
    }

    protected abstract boolean compare(T value);
}
