package com.oslash.drive.connector.service;

import com.oslash.drive.connector.clients.GoogleDriveClient;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.HashMap;

@SpringBootTest(classes = {FileDownloadAsyncHelper.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FileDownloadAsyncHelperTest {

    @MockBean
    private GoogleDriveClient googleDriveClient;

    @Autowired
    private FileDownloadAsyncHelper fileDownloadAsyncHelper;

    @BeforeAll
    public static void beforeAll() {
        System.setProperty("google.api.key", "234898246");
    }

    @Test
    void downloadFile_feignException() {
        Mockito.doThrow(FeignException.class).when(googleDriveClient).downloadFile(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        Assertions.assertDoesNotThrow(() -> fileDownloadAsyncHelper.downloadFile("id", "file", "application/png", "/"));
    }

    @Test
    void downloadFile_success() {
        Mockito.when(googleDriveClient.downloadFile(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(Response.builder()
                        .request(Request.create(Request.HttpMethod.GET, "www.googleapis.com", new HashMap<>(), (Request.Body) null, null))
                        .body("{\"payload\": \"abcd\"}", Charset.defaultCharset())
                        .status(HttpStatus.SC_OK)
                        .build());
        Assertions.assertDoesNotThrow(() -> fileDownloadAsyncHelper.downloadFile("fileId", "fileName", "application/json", Paths.get("src", "test", "resources").toFile().getAbsolutePath()));
    }
    @Test
    void downloadFile_fileNotFoundException() {
        Mockito.when(googleDriveClient.downloadFile(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(Response.builder()
                        .request(Request.create(Request.HttpMethod.GET, "www.googleapis.com", new HashMap<>(), (Request.Body) null, null))
                        .body("{\"payload\": \"abcd\"}", Charset.defaultCharset())
                        .status(HttpStatus.SC_OK)
                        .build());
        Assertions.assertDoesNotThrow(() -> fileDownloadAsyncHelper.downloadFile("fileId", "fileName", "application/json", "/"));
    }

}
