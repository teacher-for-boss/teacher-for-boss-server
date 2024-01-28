package kr.co.teacherforboss.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.domain.enums.LoginType;
import kr.co.teacherforboss.validation.annotation.CheckSocialType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckSocialTypeValidator implements ConstraintValidator<CheckSocialType, Integer> {

    private String message;

    @Override
    public void initialize(CheckSocialType constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        boolean isValid = LoginType.of(value).equals(LoginType.KAKAO) || LoginType.of(value).equals(LoginType.NAVER);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.SOCIAL_TYPE_BAD_REQUEST.toString()).addConstraintViolation();
        }

        return isValid;

    }
}
