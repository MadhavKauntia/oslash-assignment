package com.oslash.drive.connector.commons;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Constants {
    public static final String GOOGLE_DRIVE_BASE_URI = "https://www.googleapis.com";
    public static final String HEADER_ACCEPT = "Accept";
    public static final String BACKGROUND_THREAD_PREFIX = "bgThread-";
    public static final String SERVICE_TASK_EXECUTOR = "serviceTaskExecutor";
    public static final String DEFAULT_CRON_EXPRESSION = "0 0/30 * * * *";
    public static final String UTC = "UTC";
}
