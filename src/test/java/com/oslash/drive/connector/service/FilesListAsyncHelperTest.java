package com.oslash.drive.connector.service;

import com.oslash.drive.connector.clients.GoogleDriveClient;
import com.oslash.drive.connector.models.FileMetadata;
import com.oslash.drive.connector.models.FilesResponse;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.util.ArrayList;

@SpringBootTest(classes = {FilesListAsyncHelper.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilesListAsyncHelperTest {

    @MockBean
    private GoogleDriveClient googleDriveClient;

    @Autowired
    private FilesListAsyncHelper filesListAsyncHelper;

    @Test
    void getFiles_feignException() {
        Mockito.doThrow(FeignException.class).when(googleDriveClient).getFilesInFolder(Mockito.any(), Mockito.any(), Mockito.any());
        Assertions.assertDoesNotThrow(() -> filesListAsyncHelper.getFiles("query"));
    }

    @Test
    void getFiles_success() {
        FilesResponse filesResponse = FilesResponse.builder().files(new ArrayList<>()).build();
        Mockito.when(googleDriveClient.getFilesInFolder(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(filesResponse);
        Assertions.assertDoesNotThrow(() -> filesListAsyncHelper.getFiles("query"));
    }
}
