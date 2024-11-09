package com.khanh.labeling_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDatasetRequest {
    private String name;
    private String description;
    private String projectId;
    private String labelType;
    private List<String> labelGroupIds = new ArrayList<>();
//    private List<String> labelIds = new ArrayList<>();
    private List<String> labeledImageIds = new ArrayList<>();
    private String createdBy;
}
