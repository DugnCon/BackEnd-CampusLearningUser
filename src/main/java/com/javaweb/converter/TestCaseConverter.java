package com.javaweb.converter;

import java.util.List;
<<<<<<< HEAD

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

=======
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaweb.model.dto.TestCasesDTO;

<<<<<<< HEAD
//Class này dùng để đổi các một Object sang Json String giúp đơn giản hơn
=======
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
@Converter
public class TestCaseConverter implements AttributeConverter<List<TestCasesDTO>, String> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<TestCasesDTO> attribute) {
        try {
<<<<<<< HEAD
            return mapper.writeValueAsString(attribute); // List -> JSON string
=======
            if (attribute == null || attribute.isEmpty()) {
                return "[]";
            }
            return mapper.writeValueAsString(attribute);
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
        } catch (Exception e) {
            throw new RuntimeException("Error converting to JSON", e);
        }
    }

    @Override
    public List<TestCasesDTO> convertToEntityAttribute(String dbData) {
        try {
<<<<<<< HEAD
            return mapper.readValue(dbData, new TypeReference<List<TestCasesDTO>>() {}); // JSON string -> List
        } catch (Exception e) {
            throw new RuntimeException("Error reading JSON", e);
        }
    }
}

=======
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
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
