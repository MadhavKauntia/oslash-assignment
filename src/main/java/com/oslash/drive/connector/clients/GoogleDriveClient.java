package com.oslash.drive.connector.clients;

import com.oslash.drive.connector.models.FilesResponse;
import feign.HeaderMap;
import feign.Param;
import feign.RequestLine;
import feign.Response;

import java.net.URI;
import java.util.Map;

public interface GoogleDriveClient {

    @RequestLine("GET /drive/v3/files?q={q}&fields=*&key={apiKey}")
    FilesResponse getFilesInFolder(URI uri, @Param("q") String q, @Param("apiKey") String apiKey);

    @RequestLine("GET /drive/v3/files/{fileId}?key={apiKey}&alt=media&source=downloadUrl")
    Response downloadFile(URI uri, @HeaderMap Map<String, Object> headerMap, @Param("fileId") String fileId, @Param("apiKey") String apiKey);
}