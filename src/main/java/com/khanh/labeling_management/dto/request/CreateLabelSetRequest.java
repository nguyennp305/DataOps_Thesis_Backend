package com.khanh.labeling_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateLabelSetRequest {
    private String name;
    private String description;
    private String projectId;
    private List<String> labelIds;
//    private String datasetId;
    private String createdBy;
}
