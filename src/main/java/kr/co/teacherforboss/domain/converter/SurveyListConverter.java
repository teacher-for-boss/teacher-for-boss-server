package kr.co.teacherforboss.domain.converter;

import jakarta.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.List;
import kr.co.teacherforboss.domain.enums.Survey;

public class SurveyListConverter implements AttributeConverter<List<Survey>, String> {

    private static final String DELIMITER = ";";

    @Override
    public String convertToDatabaseColumn(List<Survey> attribute) {
        return String.join(DELIMITER, attribute.stream().map(Object::toString).toList());
    }

    @Override
    public List<Survey> convertToEntityAttribute(String dbData) {
        return Arrays.stream(dbData.split(DELIMITER)).map(Survey::valueOf).toList();
    }
}
