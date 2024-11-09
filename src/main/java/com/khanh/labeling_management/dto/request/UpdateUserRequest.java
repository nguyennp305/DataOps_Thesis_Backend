package com.khanh.labeling_management.dto.request;

import com.khanh.labeling_management.entity.information_management.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    @NotNull(message = "id is required")
    @NotEmpty(message = "id is required")
    private String id;
    private String username;
    private String password;
    private String roleId;
    private String email;
    private User.Status status;
}
