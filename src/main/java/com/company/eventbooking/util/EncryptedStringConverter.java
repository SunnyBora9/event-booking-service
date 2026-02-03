package com.company.eventbooking.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class EncryptedStringConverter implements AttributeConverter<String,String> {
    @Override
    public String convertToDatabaseColumn(String value) {
        return CryptoUtil.encrypt(value);
    }

    @Override
    public String convertToEntityAttribute(String value) {
        return CryptoUtil.decrypt(value);

    }

}
