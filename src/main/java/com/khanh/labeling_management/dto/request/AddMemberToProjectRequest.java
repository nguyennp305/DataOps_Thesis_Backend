package com.khanh.labeling_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddMemberToProjectRequest {
    @NotNull(message = "projectId is required")
    @NotEmpty(message = "projectId is required")
    private String projectId;
    @NotNull(message = "userId is required")
    @NotEmpty(message = "userId is required")
    private String userId;
}
