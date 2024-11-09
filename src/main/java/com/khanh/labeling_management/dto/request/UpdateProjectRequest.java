package com.khanh.labeling_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProjectRequest {
    @NotNull(message = "id is required")
    @NotEmpty(message = "id is required")
    private String id;
    private String enterpriseId;
    private String name;
    private String description;
//    private String createdUserId;
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date startAt;
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date endAt;
    private List<String> memberIds;
}
