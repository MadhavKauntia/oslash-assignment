package com.oslash.drive.connector.clients;

import com.oslash.drive.connector.models.FilesResponse;
import feign.Param;
import feign.RequestLine;

import java.net.URI;

public interface GoogleDriveClient {

    @RequestLine("GET /drive/v2/files?q='{folderId}'+in+parents&orderBy=createdDate desc&key={apiKey}")
    FilesResponse getFilesInFolder(URI uri, @Param("folderId") String folderId, @Param("apiKey") String apiKey);
}