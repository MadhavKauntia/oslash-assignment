package com.oslash.drive.connector.models;

import lombok.NoArgsConstructor;

import java.util.*;

@NoArgsConstructor
public class StoreFilesMetadata {
    public static Set<String> storedFiles = new HashSet<>();
    public static Date lastCheckTime;
    public static List<FileMetadata> filesInQueue = new ArrayList<>();
}
