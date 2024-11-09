package com.khanh.labeling_management.dto.response;

import com.khanh.labeling_management.entity.information_management.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private String tokenType;
    private User user;
}
