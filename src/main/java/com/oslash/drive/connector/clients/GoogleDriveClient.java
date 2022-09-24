package com.oslash.drive.connector.clients;

import com.oslash.drive.connector.models.FilesResponse;
import feign.HeaderMap;
import feign.Param;
import feign.RequestLine;
import feign.Response;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Map;

public interface GoogleDriveClient {

    @RequestLine("GET /drive/v2/files?q='{folderId}'+in+parents&orderBy=createdDate desc&key={apiKey}")
    FilesResponse getFilesInFolder(URI uri, @Param("folderId") String folderId, @Param("apiKey") String apiKey);

    @RequestLine("GET /drive/v2/files/{fileId}?key={apiKey}&alt=media&source=downloadUrl")
    Response downloadFile(URI uri, @HeaderMap Map<String, Object> headerMap, @Param("fileId") String fileId, @Param("apiKey") String apiKey);
}