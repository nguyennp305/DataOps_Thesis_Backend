package com.khanh.labeling_management.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("task")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    private String id;
    private String name;
    private String projectId;
    private String assigneeId;
    private Date startDate;
    private Date endDate;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    private String createdBy;
}
