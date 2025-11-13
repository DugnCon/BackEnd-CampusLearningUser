package com.javaweb.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaweb.model.dto.Profile.ProfileInformation.SocialLinkDTO;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class SocialLinkConverter implements AttributeConverter<SocialLinkDTO, String> {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(SocialLinkDTO attribute) {
        try {
            return attribute == null ? "{}" : mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            return "{}";
        }
    }

    @Override
    public SocialLinkDTO convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.trim().isEmpty() || "null".equals(dbData)) {
                return new SocialLinkDTO();
            }
            return mapper.readValue(dbData, SocialLinkDTO.class); // ← QUAN TRỌNG: Map đúng class
        } catch (Exception e) {
            return new SocialLinkDTO();
        }
    }
}
