package com.khanh.labeling_management.service;

import com.khanh.labeling_management.dto.ProjectDto;
import com.khanh.labeling_management.dto.UserDto;
import com.khanh.labeling_management.dto.request.AddMemberToProjectRequest;
import com.khanh.labeling_management.dto.request.CreateProjectRequest;
import com.khanh.labeling_management.dto.request.UpdateProjectRequest;
import com.khanh.labeling_management.entity.data_management.labeling_management.Project;
import com.khanh.labeling_management.entity.information_management.Enterprise;
import com.khanh.labeling_management.entity.information_management.User;
import com.khanh.labeling_management.model.BaseResponse;
import com.khanh.labeling_management.repository.EnterpriseRepository;
import com.khanh.labeling_management.repository.ProjectRepository;
import com.khanh.labeling_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService extends BaseService {

    private final ProjectRepository projectRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private final ModelMapper modelMapper;

    public BaseResponse<ProjectDto> addMemberToProject(AddMemberToProjectRequest request) {
        try {
            if (!userRepository.existsById(request.getUserId())) {
                return makeBadRequestResponse("User with given ID does not exist");
            }
            Optional<Project> projectOptional = projectRepository.findById(request.getProjectId());
            if (projectOptional.isEmpty()) {
                return makeBadRequestResponse("Project with given ID does not exist");
            }
            Project project = projectOptional.get();
            List<String> memberIds = project.getMemberIds();
            if (memberIds == null) {
                memberIds = new ArrayList<>();
            }
            if (memberIds.contains(request.getUserId())) {
                return makeBadRequestResponse("User is already a member of this project");
            }
            memberIds.add(request.getUserId());
            project.setMemberIds(memberIds);
            projectRepository.save(project);

            ProjectDto projectDto = modelMapper.map(project, ProjectDto.class);
            List<User> users = userRepository.findAllByIdIn(memberIds);
            projectDto.setMembers(users.stream().map(user -> modelMapper.map(user, UserDto.class)).collect(Collectors.toList()));
            return makeSuccessResponse(projectDto);
        } catch (Exception e) {
            log.error("[AddMemberToProject]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

    public BaseResponse<ProjectDto> createProject(CreateProjectRequest request) {
        try {
            if (StringUtils.isNotBlank(request.getEnterpriseId())
                && !enterpriseRepository.existsById(request.getEnterpriseId())) {
                return makeBadRequestResponse("Enterprise with given ID does not exist");
            }
//            if (StringUtils.isNotBlank(request.getCreatedUserId())
//                && !userRepository.existsById(request.getCreatedUserId())) {
//                return makeBadRequestResponse("User with given ID does not exist");
//            }
            List<String> memberIds = new ArrayList<>();
            if (request.getMemberIds() != null) {
                for (String memberId : request.getMemberIds()) {
                    if (!userRepository.existsById(memberId)) {
                        return makeBadRequestResponse("User with given ID does not exist");
                    }
                }
                memberIds.addAll(request.getMemberIds());
            }
//            if (!memberIds.contains(request.getCreatedUserId())) {
//                memberIds.add(request.getCreatedUserId());
//            }
            Project project = new Project();
            project.setEnterpriseId(request.getEnterpriseId());
            project.setName(request.getName());
            project.setDescription(request.getDescription());
//            project.setCreatedUserId(request.getCreatedUserId());
            project.setStartAt(request.getStartDate());
            project.setEndAt(request.getEndDate());
            project.setMemberIds(memberIds);
            project.setCreatedAt(new Date());
            project.setUpdatedAt(new Date());
            projectRepository.save(project);

            ProjectDto projectDto = modelMapper.map(project, ProjectDto.class);
            List<User> users = userRepository.findAllByIdIn(memberIds);
            projectDto.setMembers(users.stream().map(user -> modelMapper.map(user, UserDto.class)).collect(Collectors.toList()));
            return makeSuccessResponse(projectDto);
        } catch (Exception e) {
            log.error("[CreateProject]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

    public BaseResponse<ProjectDto> updateProject(UpdateProjectRequest request) {
        try {
            if (StringUtils.isNotBlank(request.getId()) && !projectRepository.existsById(request.getId())) {
                return makeBadRequestResponse("Project with given ID does not exist");
            }
            if (StringUtils.isNotBlank(request.getEnterpriseId())
                    && !enterpriseRepository.existsById(request.getEnterpriseId())) {
                return makeBadRequestResponse("Enterprise with given ID does not exist");
            }
//            if (StringUtils.isNotBlank(request.getCreatedUserId())
//                    && !userRepository.existsById(request.getCreatedUserId())) {
//                return makeBadRequestResponse("User with given ID does not exist");
//            }
            Project project = projectRepository.findById(request.getId()).get();
            if (StringUtils.isNotBlank(request.getEnterpriseId())) {
                project.setEnterpriseId(request.getEnterpriseId());
            }
            if (StringUtils.isNotBlank(request.getName())) {
                project.setName(request.getName());
            }
            if (StringUtils.isNotBlank(request.getDescription())) {
                project.setDescription(request.getDescription());
            }
//            if (StringUtils.isNotBlank(request.getCreatedUserId())) {
//                project.setCreatedUserId(request.getCreatedUserId());
//            }
            if (request.getStartAt() != null) {
                project.setStartAt(request.getStartAt());
            }
            if (request.getEndAt() != null) {
                project.setEndAt(request.getEndAt());
            }
            if (request.getMemberIds() != null && !request.getMemberIds().isEmpty()) {
                for (String memberId : request.getMemberIds()) {
                    if (!userRepository.existsById(memberId)) {
                        return makeBadRequestResponse("User with given ID does not exist");
                    }
                }
                project.setMemberIds(request.getMemberIds());
            }
            project.setUpdatedAt(new Date());
            projectRepository.save(project);

            ProjectDto projectDto = modelMapper.map(project, ProjectDto.class);
            List<User> users = userRepository.findAllByIdIn(project.getMemberIds());
            projectDto.setMembers(users.stream().map(user -> modelMapper.map(user, UserDto.class)).collect(Collectors.toList()));
            return makeSuccessResponse(projectDto);
        } catch (Exception e) {
            log.error("[UpdateProject]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

    public BaseResponse<ProjectDto> getProject(String id) {
        try {
            Optional<Project> projectOptional = projectRepository.findById(id);
            if (projectOptional.isEmpty()) {
                return makeBadRequestResponse("Project with given ID does not exist");
            }
            Project project = projectOptional.get();
            ProjectDto projectDto = modelMapper.map(project, ProjectDto.class);
            List<User> users = userRepository.findAllByIdIn(project.getMemberIds());
            projectDto.setMembers(users.stream().map(user -> modelMapper.map(user, UserDto.class)).collect(Collectors.toList()));
            return makeSuccessResponse(projectDto);
        } catch (Exception e) {
            log.error("[GetProject]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

    public BaseResponse<List<ProjectDto>> searchProject(
            List<String> ids,
            String memberId,
            String name, String description,
            Date startTimeFrom, Date startTimeTo,
            Date endTimeFrom, Date endTimeTo,
            Date createTimeFrom, Date createTimeTo,
            Boolean isDelete, Integer page, Integer size,
            String orderBy, Sort.Direction order
    ) {
        BaseResponse<List<Project>> baseResponse = new BaseResponse<>();
        try {
            Pageable pageable = createPageable(
                    page, size, orderBy, order
            );
            Query query = new Query();
            if (!ObjectUtils.isEmpty(ids)) {
                query.addCriteria(Criteria.where("id").in(ids));
            }
            if (StringUtils.isNotBlank(memberId)) {
                query.addCriteria(Criteria.where("memberIds").in(List.of(memberId)));
            }
            if (StringUtils.isNotBlank(name)) {
                query.addCriteria(Criteria.where("name").regex(name, "i"));
            }
            if (StringUtils.isNotBlank(description)) {
                query.addCriteria(Criteria.where("description").regex(description, "i"));
            }
//            if (StringUtils.isNotBlank(createdUserId)) {
//                query.addCriteria(Criteria.where("createdUserId").is(createdUserId));
//            }
            if (startTimeFrom != null && startTimeTo != null) {
                query.addCriteria(new Criteria().andOperator(
                        Criteria.where("startAt").gte(startTimeFrom),
                        Criteria.where("startAt").lte(startTimeTo)
                ));
            } else if (startTimeFrom != null) {
                query.addCriteria(Criteria.where("startAt").gte(startTimeFrom));
            } else if (endTimeFrom != null) {
                query.addCriteria(Criteria.where("startAt").lte(endTimeFrom));
            }

            if (endTimeFrom != null && endTimeTo != null) {
                query.addCriteria(new Criteria().andOperator(
                        Criteria.where("endAt").gte(endTimeFrom),
                        Criteria.where("endAt").lte(endTimeTo)
                ));
            } else if (endTimeFrom != null) {
                query.addCriteria(Criteria.where("endAt").gte(endTimeFrom));
            } else if (endTimeTo != null) {
                query.addCriteria(Criteria.where("endAt").lte(endTimeTo));
            }

            if (createTimeFrom != null && createTimeTo != null) {
                query.addCriteria(new Criteria().andOperator(
                        Criteria.where("createdAt").gte(createTimeFrom),
                        Criteria.where("createdAt").lte(createTimeTo)
                ));
            } else if (createTimeFrom != null) {
                query.addCriteria(Criteria.where("createdAt").gte(createTimeFrom));
            } else if (createTimeTo != null) {
                query.addCriteria(Criteria.where("createdAt").lte(createTimeTo));
            }

            if (isDelete != null) {
                query.addCriteria(Criteria.where("deleted").is(isDelete));
            } else {
                query.addCriteria(Criteria.where("deleted").is(false));
            }

            long total = mongoTemplate.count(query, Project.class);
            query.with(pageable);
            List<Project> projects = mongoTemplate.find(query, Project.class);

            List<String> memberIds = new ArrayList<>();
            for (Project project : projects) {
                memberIds.addAll(project.getMemberIds());
            }
            List<User> users = userRepository.findAllByIdIn(memberIds);
            Map<String, User> userMap = new HashMap<>();
            for (User user : users) {
                userMap.put(user.getId(), user);
            }

            List<ProjectDto> projectDtos = new ArrayList<>();
            List<Enterprise> enterprises = enterpriseRepository.findAllByIdIn(
                    projects.stream().map(Project::getEnterpriseId)
                            .filter(StringUtils::isNotBlank).collect(Collectors.toList())
            );
            Map<String, Enterprise> enterpriseMap = new HashMap<>();
            for (Enterprise enterprise : enterprises) {
                enterpriseMap.put(enterprise.getId(), enterprise);
            }
            for (Project project : projects) {
                ProjectDto projectDto = modelMapper.map(project, ProjectDto.class);
                List<UserDto> members = new ArrayList<>();
                for (String id : project.getMemberIds()) {
                    User user = userMap.get(id);
                    if (user != null) {
                        members.add(modelMapper.map(user, UserDto.class));
                    }
                }
                if (StringUtils.isNotBlank(projectDto.getEnterpriseId())
                    && enterpriseMap.containsKey(projectDto.getEnterpriseId())) {
                    projectDto.setEnterprise(enterpriseMap.get(projectDto.getEnterpriseId()));
                }
                projectDto.setMembers(members);
                projectDtos.add(projectDto);
            }

            return makeSuccessResponse(new PageImpl<>(projectDtos, pageable, total));
        } catch (Exception e) {
            log.error("[SearchProject]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

    public BaseResponse<String> deleteProject(String id) {
        try {
            if (!projectRepository.existsById(id)) {
                return makeBadRequestResponse("Project with given ID does not exist");
            }
            projectRepository.deleteById(id);
            return makeSuccessResponse("Permanently deleted 1 project");
        } catch (Exception e) {
            log.error("[DeleteProject]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

}
