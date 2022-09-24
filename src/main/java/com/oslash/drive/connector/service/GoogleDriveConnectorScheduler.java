package com.oslash.drive.connector.service;

import com.google.common.collect.Lists;
import com.oslash.drive.connector.commons.Constants;
import com.oslash.drive.connector.models.FileMetadata;
import com.oslash.drive.connector.models.FilesResponse;
import com.oslash.drive.connector.models.StoreFilesMetadata;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class GoogleDriveConnectorScheduler {

    Logger logger = LoggerFactory.getLogger(GoogleDriveConnectorService.class);

    static List<FileMetadata> newFiles = new ArrayList<>();

    @Value("${google.drive.folder.id}")
    private String folderId;

    @Value("${max.concurrency.batch.size}")
    private String batchSize;

    @Value("${events.threshold}")
    private String eventsThreshold;

    @Value("${output.folder}")
    String outputFolder;

    @Autowired
    private FilesListAsyncHelper filesListAsyncHelper;

    @Autowired
    private FileDownloadAsyncHelper fileDownloadAsyncHelper;

    @Scheduled(cron = "#{@getCronExpression}", zone = Constants.UTC)
    public void checkAndUpdateChanges() throws ExecutionException, InterruptedException {
        logger.info("Started cron job at {}", new Date());
        long startTime = System.currentTimeMillis();
        String lastCheckTime = getLastCheckTime();
        StoreFilesMetadata.lastCheckTime = new Date();
        FilesResponse filesResponse = filesListAsyncHelper.getFiles(String.format("'%s' in parents and createdTime > '%s'", folderId, lastCheckTime)).get();
        if(!CollectionUtils.isEmpty(filesResponse.getFiles())) {
            List<FileMetadata> newFiles = filesResponse.getFiles().stream().filter(file -> !StoreFilesMetadata.storedFiles.contains(file.getId())).toList();
            StoreFilesMetadata.filesInQueue.addAll(newFiles);
            if(StoreFilesMetadata.filesInQueue.size() >= (eventsThreshold == null ? 10 : Integer.parseInt(eventsThreshold))) {
                processEventsAndClearQueue();
            }
        }
        logger.info("Successfully completed job within {}ms", System.currentTimeMillis() - startTime);
    }

    private String getLastCheckTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(StoreFilesMetadata.lastCheckTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return StringUtils.replaceChars(sdf.format(calendar.getTime()), ' ', 'T');
    }

    private void processEventsAndClearQueue() {
        for(List<FileMetadata> filesBatch : Lists.partition(StoreFilesMetadata.filesInQueue, Integer.parseInt(batchSize))) {
            List<CompletableFuture<Void>> downloadFutures = new ArrayList<>();
            filesBatch.forEach(file -> {
                StoreFilesMetadata.storedFiles.add(file.getId());
                downloadFutures.add(fileDownloadAsyncHelper.downloadFile(file.getId(), file.getOriginalFilename(), file.getMimeType(), StringUtils.join(outputFolder, folderId, "/")));
            });
            downloadFutures.forEach(CompletableFuture::join);
        }
        StoreFilesMetadata.filesInQueue = new ArrayList<>();
    }

}
