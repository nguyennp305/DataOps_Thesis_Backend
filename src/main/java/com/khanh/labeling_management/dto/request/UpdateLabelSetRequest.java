package com.khanh.labeling_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateLabelSetRequest {
    @NotNull(message = "Id is required")
    @NotEmpty(message = "Id must not be empty")
    private String id;
    private String name;
    private String description;
    private String projectId;
    private List<String> labelIds;
//    private String datasetId;
    private String updatedBy;
}
