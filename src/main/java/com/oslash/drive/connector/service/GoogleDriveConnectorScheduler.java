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

    @Autowired
    private FilesListAsyncHelper filesListAsyncHelper;

    @Autowired
    private ProcessEventsHelper processEventsHelper;

    /**
     *
     * scheduled cron job to check if new files have been added to the folder
     * if new files have been added and the total number of events becomes equal to the threshold,
     * the events are processed.
     */
    @Scheduled(cron = "#{@getCronExpression}", zone = Constants.UTC)
    public void checkAndUpdateChanges() throws ExecutionException, InterruptedException {
        logger.info("Started cron job at {}", new Date());
        long startTime = System.currentTimeMillis();
        String lastCheckTime = getLastCheckTime();
        StoreFilesMetadata.lastCheckTime = new Date();
        FilesResponse filesResponse = filesListAsyncHelper.getFiles(String.format("'%s' in parents and createdTime > '%s'", folderId, lastCheckTime)).get();
        List<FileMetadata> newFiles = filesResponse.getFiles().stream().filter(file -> !StoreFilesMetadata.storedFiles.contains(file.getId())).toList();
        newFiles.forEach(file -> {
            StoreFilesMetadata.filesInQueue.add(file);
            if(StoreFilesMetadata.filesInQueue.size() == Integer.parseInt(eventsThreshold)) processEventsHelper.processEvents();
        });
        logger.info("Successfully completed job within {}ms", System.currentTimeMillis() - startTime);
    }

    private String getLastCheckTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(StoreFilesMetadata.lastCheckTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return StringUtils.replaceChars(sdf.format(calendar.getTime()), ' ', 'T');
    }

}
