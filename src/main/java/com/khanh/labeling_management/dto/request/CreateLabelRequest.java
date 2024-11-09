package com.khanh.labeling_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateLabelRequest {
    @NotNull(message = "Name is required")
    @NotEmpty(message = "Name is required")
    private String name;
    private String description;
    private String projectId;
//    private String datasetId;
    private String createdBy;
}
