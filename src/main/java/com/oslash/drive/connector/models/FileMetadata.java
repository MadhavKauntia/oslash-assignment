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
    private String mimeType;
    private Date createdTime;
    private Date modifiedTime;
    private String originalFilename;
    private String fileExtension;
    private String size;
}
