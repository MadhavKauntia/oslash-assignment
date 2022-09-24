package com.oslash.drive.connector.commons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oslash.drive.connector.models.FileMetadata;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Utils {

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    public static ObjectMapper getObjectMapper() { return jsonMapper; }

    public static <T> String getJSONStringFromObject(T object) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(object);
    }

    public static <T> T getObjectFromJSON(String json, Class<T> type) throws JsonProcessingException {
        return jsonMapper.readValue(json, type);
    }

    public static <T> T getObjectFromJSON(String json, TypeReference<T> typeReference) throws JsonProcessingException {
        return jsonMapper.readValue(json, typeReference);
    }

    public static void writeJSONToFile(List<FileMetadata> files, String path) throws IOException {
        jsonMapper.writeValue(new File(path), files);
    }
}
