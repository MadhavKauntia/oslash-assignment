package com.oslash.drive.connector.service;

import com.oslash.drive.connector.clients.GoogleDriveClient;
import com.oslash.drive.connector.commons.Constants;
import com.oslash.drive.connector.models.FilesResponse;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

@Component
public class FilesListAsyncHelper {

    Logger logger = LoggerFactory.getLogger(FilesListAsyncHelper.class);

    @Value("${google.api.key}")
    private String apiKey;

    @Autowired
    private GoogleDriveClient googleDriveClient;

    /**
     *
     * @param query - query to call the Google Drive API
     * @return list of files which satisfy the query
     */
    @Async(Constants.SERVICE_TASK_EXECUTOR)
    protected CompletableFuture<FilesResponse> getFiles(String query) {
        long startTime = System.currentTimeMillis();
        FilesResponse response = null;
        try {
            response = googleDriveClient.getFilesInFolder(URI.create(Constants.GOOGLE_DRIVE_BASE_URI), query, apiKey);
            logger.info("Successfully fetched {} files within {} ms", response.getFiles().size(), System.currentTimeMillis() - startTime);
        } catch (FeignException e) {
            logger.error("Exception occurred while fetching response from Google Drive API: {}", e.getMessage());
        }
        return CompletableFuture.completedFuture(response);
    }
}
