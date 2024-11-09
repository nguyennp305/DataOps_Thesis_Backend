package com.khanh.labeling_management.service;

import com.khanh.labeling_management.dto.request.AssignTaskRequest;
import com.khanh.labeling_management.dto.request.CreateTaskRequest;
import com.khanh.labeling_management.dto.request.UpdateTaskRequest;
import com.khanh.labeling_management.entity.Task;
import com.khanh.labeling_management.model.BaseResponse;
import com.khanh.labeling_management.repository.ProjectRepository;
import com.khanh.labeling_management.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService extends BaseService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final MongoTemplate mongoTemplate;

    public BaseResponse<String> assignTask(AssignTaskRequest request) {
        if (!projectRepository.existsById(request.getProjectId())) {
            return makeBadRequestResponse("Project with given ID does not exist");
        }
        for (AssignTaskRequest.Task taskDto : request.getTasks()) {
            Optional<Task> taskOptional = taskRepository.findById(taskDto.getId());
            if (taskOptional.isEmpty()) {
                return makeBadRequestResponse("Task with given  ID does not exist");
            }
            Task task = taskOptional.get();
            if (StringUtils.isNotBlank(task.getStatus())) {
                System.out.println(task.getStatus());
                task.setStatus(taskDto.getStatus());
            }
            if (StringUtils.isNotBlank(taskDto.getAssigneeId())) {
                System.out.println(taskDto.getAssigneeId());
                task.setAssigneeId(taskDto.getAssigneeId());
            }
            if (task.getStartDate() != null) {
                System.out.println(task.getStartDate());
                task.setStartDate(task.getStartDate());
            }
            if (task.getEndDate() != null) {
                task.setEndDate(task.getEndDate());
            }
            taskRepository.save(task);
        }
        return makeSuccessResponse(String.format("Assigned %d tasks", request.getTasks().size()));
    }

    public BaseResponse<Task> createTask(CreateTaskRequest request) {
        if (StringUtils.isNotBlank(request.getProjectId())
            && !projectRepository.existsById(request.getProjectId())) {
            return makeBadRequestResponse("Project with given ID does not exist");
        }
        Task task = modelMapper.map(request, Task.class);
        task.setCreatedAt(new Date());
        task.setUpdatedAt(new Date());
        taskRepository.save(task);
        return makeSuccessResponse(task);
    }

    public BaseResponse<Task> updateTask(UpdateTaskRequest request) {
        Optional<Task> taskOptional = taskRepository.findById(request.getId());
        if (taskOptional.isEmpty()) {
            return makeBadRequestResponse("Task with given ID does not exist");
        }
        Task task = taskOptional.get();
        if (StringUtils.isNotBlank(request.getName())) {
            task.setName(request.getName());
        }
        if (StringUtils.isNotBlank(request.getProjectId())) {
            task.setProjectId(request.getProjectId());
        }
        if (StringUtils.isNotBlank(request.getAssigneeId())) {
            task.setAssigneeId(request.getAssigneeId());
        }
        if (request.getStartDate() != null) {
            task.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            task.setEndDate(request.getEndDate());
        }
        if (StringUtils.isNotBlank(request.getStatus())) {
            task.setStatus(request.getStatus());
        }
        task.setUpdatedAt(new Date());
        taskRepository.save(task);
        return makeSuccessResponse(task);
    }

    public BaseResponse<Task> getTask(String id) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isEmpty()) {
            return makeBadRequestResponse("Task with given ID does not exist");
        }
        return makeSuccessResponse(taskOptional.get());
    }

    public BaseResponse<String> deleteTask(String id) {
        if (!taskRepository.existsById(id)) {
            return makeBadRequestResponse("Task with given ID does not exist");
        }
        taskRepository.deleteById(id);
        return makeSuccessResponse("Deleted 1 task");
    }

    public BaseResponse<List<Task>> filter(
            String name, String projectId,
            Integer page, Integer size, String sortBy, Sort.Direction order
    ) {
        Pageable pageable = createPageable(page, size, sortBy, order);
        Query query = new Query();
        if (StringUtils.isNotBlank(name)) {
            query.addCriteria(Criteria.where("name").regex(name, "i"));
        }
        if (StringUtils.isNotBlank(projectId)) {
            query.addCriteria(Criteria.where("projectId").is(projectId));
        }
        long total = mongoTemplate.count(query, Task.class);
        query.with(pageable);
        List<Task> tasks = mongoTemplate.find(query, Task.class);
        return makeSuccessResponse(
                new PageImpl<>(tasks, pageable, total)
        );
    }

}
