package kr.co.teacherforboss.validation.validator;

import java.util.List;
import kr.co.teacherforboss.domain.enums.Survey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckSurveyValidatorForIntegerList extends AbstractCheckSurveyValidator<List<Integer>> {

    @Override
    protected boolean compare(List<Integer> value) {
        return value.stream().noneMatch(v -> Survey.of(question, v).equals(Survey.NONE));
    }
}
