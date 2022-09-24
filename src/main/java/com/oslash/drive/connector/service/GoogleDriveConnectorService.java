package com.oslash.drive.connector.service;

import com.oslash.drive.connector.models.FilesResponse;
import com.oslash.drive.connector.models.StoreFilesMetadata;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.concurrent.ExecutionException;

@Service
public class GoogleDriveConnectorService implements InitializingBean {

    Logger logger = LoggerFactory.getLogger(GoogleDriveConnectorService.class);

    @Autowired
    private ProcessEventsHelper processEventsHelper;

    @Value("${google.drive.folder.id}")
    private String folderId;

    @Value("${events.threshold}")
    private String eventsThreshold;

    @Value("${output.folder}")
    String outputFolder;

    @Autowired
    private FilesListAsyncHelper filesListAsyncHelper;

    @Autowired
    private FileDownloadAsyncHelper fileDownloadAsyncHelper;

    private void init() throws ExecutionException, InterruptedException {
        try {
            new File(StringUtils.join(outputFolder, "/", folderId)).mkdir();
            Files.createFile(Path.of(StringUtils.join(outputFolder, "/", folderId, "/", "events.json")));
        } catch (IOException e) {
            // ignore as this means the events.json file is already present
        }
        StoreFilesMetadata.lastCheckTime = new Date();
        FilesResponse files = filesListAsyncHelper.getFiles(String.format("'%s' in parents", folderId)).get();
        files.getFiles().forEach(file -> {
            StoreFilesMetadata.filesInQueue.add(file);
            if(StoreFilesMetadata.filesInQueue.size() == Integer.parseInt(eventsThreshold)) processEventsHelper.processEvents();
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }
}
