package com.oslash.drive.connector.service;

import com.oslash.drive.connector.clients.GoogleDriveClient;
import com.oslash.drive.connector.commons.Constants;
import feign.FeignException;
import feign.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class FileDownloadAsyncHelper {

    Logger logger = LoggerFactory.getLogger(FileDownloadAsyncHelper.class);

    @Value("${google.api.key}")
    private String apiKey;

    @Autowired
    private GoogleDriveClient googleDriveClient;

    @Async(Constants.SERVICE_TASK_EXECUTOR)
    protected CompletableFuture<Void> downloadFile(String fileId, String fileTitle, String fileMimeType, String downloadPath) {
        long startTime = System.currentTimeMillis();
        try {
            Map<String, Object> headerMap = new HashMap<>();
            headerMap.put(Constants.HEADER_ACCEPT, fileMimeType);
            Response response = googleDriveClient.downloadFile(URI.create(Constants.GOOGLE_DRIVE_BASE_URI), headerMap, fileId, apiKey);
            new File(downloadPath).mkdir();
            try(OutputStream os = new FileOutputStream(StringUtils.join(downloadPath, fileTitle))) {
                response.body().asInputStream().transferTo(os);
                os.flush();
                logger.info("Successfully download file: {} to location: {} within {}ms", fileTitle, downloadPath, System.currentTimeMillis() - startTime);
            } catch (FileNotFoundException e) {
                logger.error("File not found while writing to file {}", downloadPath);
            } catch (IOException e) {
                logger.error("Unexpected exception while writing {} to file: {}", fileId, e.getMessage());
            }
        } catch (FeignException e) {
            // if file size is too large, this call might time out
            logger.error("Exception {} while downloading file from Google Drive API", e.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }
}
