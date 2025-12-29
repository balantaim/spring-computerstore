/*
 * Copyright 2025 Martin Atanasov.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.martinatanasov.computerstore.utils.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

@Converter(autoApply = false)
public class JsonToMapConverter implements AttributeConverter<Map<String, Object>, String> {

    public JsonToMapConverter() {
        this.jsonMapper = JsonMapper.builder().build();
    }

    private final JsonMapper jsonMapper;

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return jsonMapper.writeValueAsString(attribute);
        } catch (JacksonException e) {
            throw new IllegalArgumentException("Error converting Map to JSON string", e);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return jsonMapper.readValue(dbData, Map.class);
        } catch (JacksonException e) {
            throw new IllegalArgumentException("Error converting JSON string to Map", e);
        }
    }

}
