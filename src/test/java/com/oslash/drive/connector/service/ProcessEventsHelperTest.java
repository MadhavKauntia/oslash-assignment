package com.oslash.drive.connector.service;

import com.oslash.drive.connector.models.FileMetadata;
import com.oslash.drive.connector.models.StoreFilesMetadata;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

@SpringBootTest(classes = {ProcessEventsHelper.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProcessEventsHelperTest {

    @MockBean
    private FileDownloadAsyncHelper fileDownloadAsyncHelper;

    @Autowired
    private ProcessEventsHelper processEventsHelper;

    @BeforeAll
    public static void beforeAll() {
        StoreFilesMetadata.filesInQueue = Collections.singletonList(FileMetadata.builder().id("id1").originalFilename("file1").mimeType("application/json").build());
        StoreFilesMetadata.storedFiles = new HashSet<>();
        StoreFilesMetadata.lastCheckTime = new Date();
        System.setProperty("output.folder", Paths.get("src", "test", "resources").toFile().getAbsolutePath());
        System.setProperty("google.drive.folder.id", "folder");
    }

    @Test
    void processEvents_success() {
        Mockito.when(fileDownloadAsyncHelper.downloadFile(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(CompletableFuture.completedFuture(null));
        Assertions.assertDoesNotThrow(() -> processEventsHelper.processEvents());
    }
}
