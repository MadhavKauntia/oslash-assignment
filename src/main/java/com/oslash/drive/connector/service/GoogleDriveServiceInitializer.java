package com.oslash.drive.connector.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class GoogleDriveServiceInitializer implements InitializingBean {

    @Autowired
    private GoogleDriveConnectorService googleDriveConnectorService;

    private void init() throws ExecutionException, InterruptedException {
        googleDriveConnectorService.init();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }
}
