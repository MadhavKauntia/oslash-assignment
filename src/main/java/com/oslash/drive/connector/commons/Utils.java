package com.oslash.drive.connector.commons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    public static ObjectMapper getObjectMapper() { return jsonMapper; }

    public static <T> String getJSONStringFromObject(T object) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(object);
    }

    public static <T> T getObjectFromJSON(String json, Class<T> type) throws JsonProcessingException {
        return jsonMapper.readValue(json, type);
    }
}
