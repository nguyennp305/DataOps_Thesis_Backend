package com.khanh.labeling_management.entity.data_management.labeling_management;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "Project")
@Setter
@Getter
@RequiredArgsConstructor
public class Project {
    private String id;
    private String enterpriseId;
    private String name;
    private String description;

    private Date startAt;
    private Date endAt;

    private List<String> memberIds;

//    private String createdUserId;
    private String status;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private boolean deleted = false;
}
