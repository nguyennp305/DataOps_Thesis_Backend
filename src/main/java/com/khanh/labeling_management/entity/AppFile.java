package com.khanh.labeling_management.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("file")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppFile {
    private String id;
    private String name;
    private String originalName;
    private String path;
    private String mimeType;
    private String type;
    private String extension;
    private Long size;
    private Date createdAt;
    private Date updatedAt;
}
