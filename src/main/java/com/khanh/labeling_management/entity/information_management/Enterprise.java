package com.khanh.labeling_management.entity.information_management;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "Enterprise")
@Setter
@Getter
@RequiredArgsConstructor
public class Enterprise {

    @Id
    private String id;
    private String name;
    private String email;
    private String status;
    private String description;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private boolean deleted;
}
