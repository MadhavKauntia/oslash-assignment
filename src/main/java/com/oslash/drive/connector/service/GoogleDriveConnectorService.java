package com.oslash.drive.connector.service;

import com.google.common.collect.Lists;
import com.oslash.drive.connector.models.FileMetadata;
import com.oslash.drive.connector.models.FilesResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class GoogleDriveConnectorService implements InitializingBean {

    Logger logger = LoggerFactory.getLogger(GoogleDriveConnectorService.class);

    @Value("${GOOGLE_DRIVE_FOLDER_ID}")
    private String folderId;

    @Value("${output.folder}")
    String outputFolder;

    @Value("{max.concurrency.batch.size}")
    String batchSize;

    @Autowired
    private FilesListAsyncHelper filesListAsyncHelper;

    @Autowired
    private FileDownloadAsyncHelper fileDownloadAsyncHelper;

    private void init() throws ExecutionException, InterruptedException {
        // fetch list of files for the folder
        FilesResponse files = filesListAsyncHelper.getFilesForFolder(folderId).get();

        // download files to output folder
        int batchSize;
        try {
            batchSize = Integer.parseInt(this.batchSize);
        } catch (NumberFormatException e) {
            batchSize = 10;
        }
        for(List<FileMetadata> filesBatch : Lists.partition(files.getItems(), batchSize)) {
            List<CompletableFuture<Void>> parallelDownloads = new ArrayList<>();
            filesBatch.forEach(file -> parallelDownloads.add(fileDownloadAsyncHelper.downloadFile(file.getId(), file.getTitle(), file.getMimeType(), StringUtils.join(outputFolder, folderId, "/"))));
            parallelDownloads.forEach(CompletableFuture::join);
        }

        // initiate cron

    }


    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }
}
