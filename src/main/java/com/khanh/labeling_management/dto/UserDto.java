package com.khanh.labeling_management.dto;

import com.khanh.labeling_management.entity.information_management.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String id;
    private String username;
    private String email;
    private User.Status status;
    private int active;
    private String roleName;
    private String createdBy;
    private String updatedBy;
    private String deletedBy;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private boolean deleted;
}
