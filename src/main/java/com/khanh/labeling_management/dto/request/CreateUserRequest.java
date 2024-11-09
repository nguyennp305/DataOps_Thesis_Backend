package com.khanh.labeling_management.dto.request;

import com.khanh.labeling_management.entity.information_management.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {
    @NotNull(message = "username is required")
    @NotEmpty(message = "username is required")
    private String username;

    @NotEmpty(message = "password is required")
    @NotNull(message = "password is required")
    private String password;

    @NotEmpty(message = "roleId is required")
    @NotNull(message = "roleId is required")
    private String roleId;

    private String email;
}
