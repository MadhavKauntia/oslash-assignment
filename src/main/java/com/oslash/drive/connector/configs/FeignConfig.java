package com.oslash.drive.connector.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oslash.drive.connector.clients.GoogleDriveClient;
import feign.Feign;
import feign.Request;
import feign.Retryer;
import feign.Target;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableFeignClients
public class FeignConfig {

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    @Bean
    public GoogleDriveClient googleDriveClient() {
        return buildClient(
                GoogleDriveClient.class,
                1000,
                10000,
                500,
                2000,
                3
        );
    }

    private <T> T buildClient(Class<T> type, long connectTimeoutInMillis, long readTimeoutInMillis,
                              long retryPeriodInMillis, long retryMaxPeriodInMillis, int retryMaxAttempts) {
        return Feign.builder()
                .options(new Request.Options(connectTimeoutInMillis, TimeUnit.MILLISECONDS, readTimeoutInMillis, TimeUnit.MILLISECONDS, true))
                .encoder(new JacksonEncoder())
                .decoder(new ResponseEntityDecoder(new JacksonDecoder(jsonMapper)))
                .retryer(new Retryer.Default(
                        retryPeriodInMillis,
                        retryMaxPeriodInMillis,
                        retryMaxAttempts
                ))
                .target(Target.EmptyTarget.create(type));
    }
}
