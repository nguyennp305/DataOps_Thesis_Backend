package com.khanh.labeling_management.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("report")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Report {
    private String id;
    private String name;
    private String description;
    private Date createdAt;
    private String createdBy;
    private Date updatedAt;
    private String projectId;
    private String reportType;
    private String data;
}
