package com.khanh.labeling_management.entity.data_management.labeling_management;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("label")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Label {
    private String id;
    private String name;
    private String description;
    private String projectId;
//    private String datasetId;
    private Date createdAt;
    private Date updatedAt;
    private String createdBy;
    private String updatedBy;
}
