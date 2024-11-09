package com.khanh.labeling_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotNull(message = "Tên tài khoản không được để trống")
    @NotEmpty(message = "Tên tài khoản không được để trống")
    private String username;
    @NotNull(message = "Mật khẩu không được để trống")
    @NotEmpty(message = "Mật khẩu khoản không được để trống")
    private String password;
}
