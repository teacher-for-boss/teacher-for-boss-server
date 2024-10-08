package kr.co.teacherforboss.domain.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class JsonConverter<T> implements AttributeConverter<T, String> {

    private final Class<T> clazz;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonConverter(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String convertToDatabaseColumn(T attribute) {
        try {
            if (attribute == null) {
                return null;
            }
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting object to JSON", e);
        }
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null) {
                return null;
            }
            return objectMapper.readValue(dbData, clazz);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting JSON to object", e);
        }
    }
}
