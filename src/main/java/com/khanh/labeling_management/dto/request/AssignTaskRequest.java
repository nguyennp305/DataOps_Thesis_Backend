package com.khanh.labeling_management.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignTaskRequest {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Task {
        @NotNull(message = "Task id is required")
        @NotEmpty(message = "Task id is required")
        private String id;
        private String name;
        @NotNull(message = "Assignee id is required")
        @NotEmpty(message = "Assignee id is required")
        private String assigneeId;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
        private Date startDate;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
        private Date endDate;
        private String status;
    }

    @NotNull(message = "Project id is required")
    @NotEmpty(message = "Project id is required")
    private String projectId;
    private List<Task> tasks = new ArrayList<>();

}
