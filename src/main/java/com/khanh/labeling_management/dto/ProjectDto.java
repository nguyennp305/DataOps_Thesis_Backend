package com.khanh.labeling_management.dto;

import com.khanh.labeling_management.entity.information_management.Enterprise;
import com.khanh.labeling_management.entity.information_management.User;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto {
    private String id;
    private String enterpriseId;
    private String name;
    private String description;

    private Date startAt;
    private Date endAt;

    private List<UserDto> members = new ArrayList<>();

//    private String createdUserId;
    private String status;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private boolean deleted = false;
    private Enterprise enterprise;
}
