package com.oslash.drive.connector.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileMetadata {
    private String id;
    private String webContentLink;
    private String iconLink;
    private String thumbnailLink;
    private String title;
    private String mimeType;
    private Date createdDate;
    private Date modifiedDate;
    private String downloadUrl;
    private String originalFileName;
    private String fileExtension;
    private String fileSize;
}
