package com.oslash.drive.connector.service;

import com.oslash.drive.connector.clients.GoogleDriveClient;
import com.oslash.drive.connector.models.FilesResponse;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class GoogleDriveConnectorService implements InitializingBean {

    @Value("${GOOGLE_API_KEY}")
    private String apiKey;

    @Value("${GOOGLE_DRIVE_FOLDER_ID}")
    private String folderId;

    @Autowired
    private GoogleDriveClient googleDriveClient;

    private void init() {
        FilesResponse response = googleDriveClient.getFilesInFolder(URI.create("https://www.googleapis.com"), folderId, apiKey);
        System.out.println(response);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }
}
