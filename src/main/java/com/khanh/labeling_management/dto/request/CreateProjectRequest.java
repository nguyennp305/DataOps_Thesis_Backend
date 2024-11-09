package com.khanh.labeling_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProjectRequest {
    private String enterpriseId;
    private String name;
    private String description;
//    private String createdUserId;
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date startDate;
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date endDate;
    private List<String> memberIds = new ArrayList<>();
}
