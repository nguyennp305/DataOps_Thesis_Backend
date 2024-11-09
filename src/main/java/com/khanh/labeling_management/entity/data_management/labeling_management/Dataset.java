package com.khanh.labeling_management.entity.data_management.labeling_management;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document("dataset")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dataset {
    private String id;
    private String name;
    private String description;
    private String projectId;
    private String labelType;
    private List<String> labelGroupIds = new ArrayList<>();
//    private List<String> labelIds = new ArrayList<>();
    private List<String> labeledImageIds = new ArrayList<>();
    private Date createdAt;
    private Date updatedAt;
    private String createdBy;
    private String updatedBy;
}
