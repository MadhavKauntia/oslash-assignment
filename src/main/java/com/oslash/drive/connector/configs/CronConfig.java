package com.oslash.drive.connector.configs;

import com.oslash.drive.connector.commons.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class CronConfig {

    @Value("${periodic.check.time.in.mins}")
    String periodicCheckTimeInMins;

    @Bean
    String getCronExpression() {
        if(StringUtils.isBlank(periodicCheckTimeInMins)) {
            return Constants.DEFAULT_CRON_EXPRESSION;
        }
        return String.format("0 0/%s * * * *", periodicCheckTimeInMins);
    }
}
