package com.oslash.drive.connector.service;

import com.oslash.drive.connector.models.FileMetadata;
import com.oslash.drive.connector.models.FilesResponse;
import com.oslash.drive.connector.models.StoreFilesMetadata;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

@SpringBootTest(classes = {GoogleDriveConnectorScheduler.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GoogleDriveConnectorSchedulerTest {

    @MockBean
    private FilesListAsyncHelper filesListAsyncHelper;

    @MockBean
    private ProcessEventsHelper processEventsHelper;

    @Autowired
    private GoogleDriveConnectorScheduler googleDriveConnectorScheduler;

    @BeforeAll
    public static void beforeAll() {
        StoreFilesMetadata.lastCheckTime = new Date();
        StoreFilesMetadata.filesInQueue = new ArrayList<>();
        StoreFilesMetadata.storedFiles = new HashSet<>();
        System.setProperty("events.threshold", "1");
    }

    @Test
    void checkAndUpdateChanges_noChanges() {
        FilesResponse filesResponse = FilesResponse.builder()
                .files(Collections.singletonList(
                                FileMetadata.builder()
                                        .originalFilename("file")
                                        .mimeType("application/json")
                                        .id("id")
                                        .build()
                        )
                ).build();
        Mockito.when(filesListAsyncHelper.getFiles(Mockito.anyString())).thenReturn(CompletableFuture.completedFuture(filesResponse));
        Mockito.doNothing().when(processEventsHelper).processEvents();
        Assertions.assertDoesNotThrow(() -> googleDriveConnectorScheduler.checkAndUpdateChanges());
    }
}
