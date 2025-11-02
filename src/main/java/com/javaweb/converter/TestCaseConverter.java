package com.javaweb.converter;

import java.util.List;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaweb.model.dto.TestCasesDTO;

@Converter
public class TestCaseConverter implements AttributeConverter<List<TestCasesDTO>, String> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<TestCasesDTO> attribute) {
        try {
            if (attribute == null || attribute.isEmpty()) {
                return "[]";
            }
            return mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Error converting to JSON", e);
        }
    }

    @Override
    public List<TestCasesDTO> convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.trim().isEmpty() || "null".equals(dbData)) {
                return List.of();
            }
            return mapper.readValue(dbData, new TypeReference<List<TestCasesDTO>>() {});
        } catch (Exception e) {
            System.err.println("Error reading JSON from DB: '" + dbData + "'");
            throw new RuntimeException("Error reading JSON", e);
        }
    }
}