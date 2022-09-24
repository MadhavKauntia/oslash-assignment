package com.oslash.drive.connector.service;

import com.oslash.drive.connector.models.FileMetadata;
import com.oslash.drive.connector.models.FilesResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

@SpringBootTest(classes = {GoogleDriveConnectorService.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GoogleDriveConnectorServiceTest {

    @BeforeAll
    public static void beforeAll() {
        System.setProperty("google.api.key", "234898246");
        System.setProperty("google.drive.folder.id", "folder");
        System.setProperty("events.threshold", "1");
        System.setProperty("output.folder", Paths.get("src", "test", "resources").toFile().getAbsolutePath());
    }

    @MockBean
    private ProcessEventsHelper processEventsHelper;

    @MockBean
    private FilesListAsyncHelper filesListAsyncHelper;

    @Autowired
    private GoogleDriveConnectorService googleDriveConnectorService;

    @Test
    void testInit_success() {
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
        Assertions.assertDoesNotThrow(() -> googleDriveConnectorService.init());
    }

    @Test
    void testInit_fail() {
        Mockito.when(filesListAsyncHelper.getFiles(Mockito.anyString())).thenReturn(CompletableFuture.completedFuture(null));
        Mockito.doNothing().when(processEventsHelper).processEvents();
        Assertions.assertDoesNotThrow(() -> googleDriveConnectorService.init());
    }
}
