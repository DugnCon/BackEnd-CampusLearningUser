package com.javaweb.converter;

import java.util.List;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaweb.model.dto.TestCasesDTO;

@Converter
public class JsonConverter implements AttributeConverter<Object, String> {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Object attribute) {
        try {
            if (attribute == null) return null;
            return mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Error converting to JSON", e);
        }
    }

    @Override
    public Object convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.trim().isEmpty() || "null".equals(dbData)) {
                return null;
            }
            return mapper.readValue(dbData, Object.class);
        } catch (Exception e) {
            throw new RuntimeException("Error reading JSON", e);
        }
    }
}
