package com.khanh.labeling_management.model.input;

import lombok.Data;

@Data
public class UserInput {
    private String id;
    private String employeeId;
    private String username;
    private String password;
    private String role;
    private String email;
    private String name;
}
