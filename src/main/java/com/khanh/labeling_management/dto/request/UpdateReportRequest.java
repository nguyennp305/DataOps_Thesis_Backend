package com.khanh.labeling_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateReportRequest {
    @NotNull(message = "Id is required")
    @NotEmpty(message = "Id is required")
    private String id;
    private String name;
    private String description;
    private String projectId;
    private String reportType;
    private String data;
}
