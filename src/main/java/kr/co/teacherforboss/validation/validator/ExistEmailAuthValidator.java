package kr.co.teacherforboss.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import kr.co.teacherforboss.repository.EmailAuthRepository;
import kr.co.teacherforboss.validation.annotation.ExistEmailAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExistEmailAuthValidator implements ConstraintValidator<ExistEmailAuth, Long> {

    private final EmailAuthRepository emailAuthRepository;
    private String message;

    @Override
    public void initialize(ExistEmailAuth constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        boolean isValid = emailAuthRepository.existsById(value);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }

        return isValid;

    }
}
