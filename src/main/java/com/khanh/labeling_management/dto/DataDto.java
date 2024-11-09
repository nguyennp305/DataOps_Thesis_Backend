package com.khanh.labeling_management.dto;

import com.khanh.labeling_management.entity.data_management.labeling_management.Data;
import com.khanh.labeling_management.entity.data_management.labeling_management.LabelCropMulti;
import com.khanh.labeling_management.entity.data_management.labeling_management.LabeledImage;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document("data")
@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class DataDto {
    private String id;
    private String name;
    private String imageUrl;
    private String projectId;
//    private String datasetId;
    private String status;
    private List<LabeledImage> labeledImages;
    private String description;
    private List<String> labeledIdClassification = new ArrayList<>();
    private Date createdAt;
    private Date updatedAt;
    private String createdBy;
    private String updatedBy;
}
