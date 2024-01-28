package kr.co.teacherforboss.validation.validator;

import kr.co.teacherforboss.domain.enums.Survey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckSurveyValidatorForInteger extends AbstractCheckSurveyValidator<Integer> {

    @Override
    protected boolean compare(Integer value) {
        return !Survey.of(question, value).equals(Survey.NONE);
    }
}
