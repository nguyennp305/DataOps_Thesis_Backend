package com.khanh.labeling_management.controller.information_management;

import com.khanh.labeling_management.dto.ProjectDto;
import com.khanh.labeling_management.dto.request.AddMemberToProjectRequest;
import com.khanh.labeling_management.dto.request.CreateProjectRequest;
import com.khanh.labeling_management.dto.request.UpdateProjectRequest;
import com.khanh.labeling_management.entity.data_management.labeling_management.Project;
import com.khanh.labeling_management.model.BaseResponse;
import com.khanh.labeling_management.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("api/project")
@RequiredArgsConstructor
public class ProjectController extends BaseController {
    private final ProjectService projectService;

    @PostMapping("add-member")
    public ResponseEntity<BaseResponse<ProjectDto>> addMember(@RequestBody @Valid AddMemberToProjectRequest request) {
        return createResponseEntity(projectService.addMemberToProject(request));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<ProjectDto>> createProject(@RequestBody @Valid CreateProjectRequest request) {
        return createResponseEntity(projectService.createProject(request));
    }

    @PutMapping
    public ResponseEntity<BaseResponse<ProjectDto>> updateProject(@RequestBody @Valid UpdateProjectRequest request) {
        return createResponseEntity(projectService.updateProject(request));
    }

    @GetMapping("{id}")
    public ResponseEntity<BaseResponse<ProjectDto>> getProject(@PathVariable String id) {
        return createResponseEntity(projectService.getProject(id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<BaseResponse<String>> deleteProject(@PathVariable String id) {
        return createResponseEntity(projectService.deleteProject(id));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<ProjectDto>>> searchProjects(
            @RequestParam(required = false) String ids,
            @RequestParam(required = false) String memberId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
//            @RequestParam(required = false) String createdUserId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss") Date startTimeFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss") Date startTimeTo,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss") Date endTimeFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss") Date endTimeTo,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss") Date createTimeFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss") Date createTimeTo,
            @RequestParam(required = false) Boolean isDelete,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) Sort.Direction order
    ) {
        List<String> idList = null;
        if (StringUtils.isNotBlank(ids)) {
            idList = Arrays.asList(ids.split(","));
        }
        return createResponseEntity(projectService.searchProject(
                idList,
                memberId,
                name, description, startTimeFrom, startTimeTo,
                endTimeFrom, endTimeTo, createTimeFrom, createTimeTo,
                isDelete, page, size, orderBy, order));
    }

}
