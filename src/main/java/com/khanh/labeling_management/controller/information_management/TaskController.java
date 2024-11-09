package com.khanh.labeling_management.controller.information_management;

import com.khanh.labeling_management.dto.request.AssignTaskRequest;
import com.khanh.labeling_management.dto.request.CreateTaskRequest;
import com.khanh.labeling_management.dto.request.UpdateTaskRequest;
import com.khanh.labeling_management.entity.Task;
import com.khanh.labeling_management.model.BaseResponse;
import com.khanh.labeling_management.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/task")
@RequiredArgsConstructor
public class TaskController extends BaseController {

    private final TaskService taskService;

    @PostMapping("assign")
    public ResponseEntity<BaseResponse<String>> assignTask(@RequestBody @Valid AssignTaskRequest assignTaskRequest) {
        return createResponseEntity(taskService.assignTask(assignTaskRequest));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<Task>> createTask(@RequestBody @Valid CreateTaskRequest request) {
        return createResponseEntity(taskService.createTask(request));
    }

    @PutMapping
    public ResponseEntity<BaseResponse<Task>> updateTask(@RequestBody @Valid UpdateTaskRequest request) {
        return createResponseEntity(taskService.updateTask(request));
    }

    @GetMapping("{id}")
    public ResponseEntity<BaseResponse<Task>> getTask(@PathVariable String id) {
        return createResponseEntity(taskService.getTask(id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<BaseResponse<String>> deleteTask(@PathVariable String id) {
        return createResponseEntity(taskService.deleteTask(id));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<Task>>> filter(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String projectId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Sort.Direction order) {
        return createResponseEntity(taskService.filter(name, projectId, page, size, sortBy, order));
    }

}
