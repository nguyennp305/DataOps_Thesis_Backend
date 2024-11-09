package com.khanh.labeling_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateReportRequest {
    private String name;
    private String description;
    private String createdBy;
    private String projectId;
    private String reportType;
    private String data;
}
