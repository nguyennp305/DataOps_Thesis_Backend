package com.khanh.labeling_management.entity.data_management.labeling_management;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document("label-set")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabelSet {
    private String id;
    private String name;
    private String description;
    private String projectId;
    private List<String> labelIds;
//    private String datasetId;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
}
