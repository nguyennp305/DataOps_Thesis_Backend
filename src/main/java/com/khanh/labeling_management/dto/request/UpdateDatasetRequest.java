package com.khanh.labeling_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDatasetRequest {
    @NotNull(message = "ID is required")
    @NotEmpty(message = "ID is required")
    private String id;
    private String name;
    private String description;
    private String projectId;
    private String labelType;
    private List<String> labelGroupIds = new ArrayList<>();
//    private List<String> labelIds = new ArrayList<>();
    private List<String> labeledImageIds = new ArrayList<>();
    private String updatedBy;
}
