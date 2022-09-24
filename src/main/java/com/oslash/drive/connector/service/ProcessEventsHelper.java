package com.oslash.drive.connector.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.oslash.drive.connector.commons.Utils;
import com.oslash.drive.connector.models.FileMetadata;
import com.oslash.drive.connector.models.StoreFilesMetadata;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class ProcessEventsHelper {

    Logger logger = LoggerFactory.getLogger(GoogleDriveConnectorService.class);

    @Autowired
    private FileDownloadAsyncHelper fileDownloadAsyncHelper;

    @Value("${max.concurrency.batch.size}")
    private String batchSize;

    @Value("${output.folder}")
    String outputFolder;

    @Value("${google.drive.folder.id}")
    private String folderId;


    public void processEvents() {
        String jsonEventsFileAsString = null;
        String pathOfJsonFile = StringUtils.join(outputFolder, "/", folderId, "/", "events.json");
        try {
            jsonEventsFileAsString = Files.readString(Path.of(pathOfJsonFile));
        } catch (IOException e) {
            logger.error("Unexpected error: {} while reading events.json file", e.getMessage());
        }
        List<FileMetadata> fileMetadataList = new ArrayList<>();
        try {
            fileMetadataList = Utils.getObjectFromJSON(jsonEventsFileAsString, new TypeReference<List<FileMetadata>>() {
            });
        } catch (JsonProcessingException e) {
            logger.warn("events.json file is currently empty");
        }
        for(List<FileMetadata> filesBatch : Lists.partition(StoreFilesMetadata.filesInQueue, Integer.parseInt(batchSize))) {
            List<CompletableFuture<Void>> downloadFutures = new ArrayList<>();
            for(FileMetadata file : filesBatch) {
                StoreFilesMetadata.storedFiles.add(file.getId());
                fileMetadataList.add(file);
                downloadFutures.add(fileDownloadAsyncHelper.downloadFile(file.getId(), file.getOriginalFilename(), file.getMimeType(), StringUtils.join(outputFolder, folderId)));
            }
            downloadFutures.forEach(CompletableFuture::join);
        }
        try {
            Utils.writeJSONToFile(fileMetadataList, pathOfJsonFile);
        } catch (IOException e) {
            logger.error("events.json file not found at {}", pathOfJsonFile);
        }
        StoreFilesMetadata.filesInQueue = new ArrayList<>();
    }
}
