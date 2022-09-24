package com.oslash.drive.connector.service;

import com.google.common.collect.Lists;
import com.oslash.drive.connector.models.FileMetadata;
import com.oslash.drive.connector.models.FilesResponse;
import com.oslash.drive.connector.models.StoreFilesMetadata;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class GoogleDriveConnectorService implements InitializingBean {

    Logger logger = LoggerFactory.getLogger(GoogleDriveConnectorService.class);

    @Value("${google.drive.folder.id}")
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
        StoreFilesMetadata.lastCheckTime = new Date();
        FilesResponse files = filesListAsyncHelper.getFiles(String.format("'%s' in parents", folderId)).get();

        // download files to output folder
        int batchSize;
        try {
            batchSize = Integer.parseInt(this.batchSize);
        } catch (NumberFormatException e) {
            batchSize = 10;
        }
        for(List<FileMetadata> filesBatch : Lists.partition(files.getFiles(), batchSize)) {
            List<CompletableFuture<Void>> parallelDownloads = new ArrayList<>();
            filesBatch.forEach(file -> {
                StoreFilesMetadata.storedFiles.add(file.getId());
                parallelDownloads.add(fileDownloadAsyncHelper.downloadFile(file.getId(), file.getOriginalFilename(), file.getMimeType(), StringUtils.join(outputFolder, folderId, "/")));
            });
            parallelDownloads.forEach(CompletableFuture::join);
        }

    }


    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }
}
