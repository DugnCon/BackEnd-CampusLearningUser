package com.javaweb.converter;

import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaweb.model.dto.TestCasesDTO;

//Class này dùng để đổi các một Object sang Json String giúp đơn giản hơn
@Converter
public class TestCaseConverter implements AttributeConverter<List<TestCasesDTO>, String> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<TestCasesDTO> attribute) {
        try {
            return mapper.writeValueAsString(attribute); // List -> JSON string
        } catch (Exception e) {
            throw new RuntimeException("Error converting to JSON", e);
        }
    }

    @Override
    public List<TestCasesDTO> convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, new TypeReference<List<TestCasesDTO>>() {}); // JSON string -> List
        } catch (Exception e) {
            throw new RuntimeException("Error reading JSON", e);
        }
    }
}

