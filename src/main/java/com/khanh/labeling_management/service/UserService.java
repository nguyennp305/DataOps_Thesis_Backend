package com.khanh.labeling_management.service;

import com.khanh.labeling_management.dto.UserDto;
import com.khanh.labeling_management.dto.request.CreateUserRequest;
import com.khanh.labeling_management.dto.request.UpdateUserRequest;
import com.khanh.labeling_management.entity.information_management.Role;
import com.khanh.labeling_management.entity.information_management.User;
import com.khanh.labeling_management.model.BaseResponse;
import com.khanh.labeling_management.model.Message;
import com.khanh.labeling_management.repository.RoleRepository;
import com.khanh.labeling_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.apache.tomcat.jni.Local;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService extends BaseService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MongoTemplate mongoTemplate;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    public BaseResponse<UserDto> createUser(CreateUserRequest request) {
        try {
            if (userRepository.existsByUsername(request.getUsername())) {
                return makeBadRequestResponse("User with given username already exists");
            }
            Optional<Role> roleOptional = roleRepository
                    .findById(request.getRoleId());
            if (roleOptional.isEmpty()) {
                return makeBadRequestResponse("Role with given ID does not exist.");
            }

            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setEmail(request.getEmail());
            user.setStatus(User.Status.Active);
            user.setRoleId(request.getRoleId());
            user.setRoleName(roleOptional.get().getName());
            user.setCreatedAt(new Date());
            user.setUpdatedAt(new Date());
            userRepository.save(user);
            return makeSuccessResponse(modelMapper.map(user, UserDto.class));
        } catch (Exception e) {
            log.error("[CreateUser]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

    public BaseResponse<UserDto> updateUser(UpdateUserRequest request) {
        try {
            Optional<User> userOptional = userRepository.findById(request.getId());
            if (userOptional.isEmpty()) {
                return makeBadRequestResponse("User with given ID does not exist");
            }
            User user = userOptional.get();
            if (StringUtils.isNotBlank(request.getUsername())) {
                user.setUsername(request.getUsername());
            }
            if (StringUtils.isNotBlank(request.getPassword())) {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
            }
            if (StringUtils.isNotBlank(request.getRoleId())) {
                Optional<Role> roleOptional = roleRepository.findById(request.getRoleId());
                if (roleOptional.isEmpty()) {
                    return makeBadRequestResponse("Role with given ID does not exist");
                }
                user.setRoleId(roleOptional.get().getId());
                user.setRoleId(roleOptional.get().getName());
            }
            if (StringUtils.isNotBlank(request.getEmail())) {
                user.setEmail(request.getEmail());
            }
            if (request.getStatus() != null) {
                user.setStatus(request.getStatus());
            }
            user.setUpdatedAt(new Date());
            userRepository.save(user);
            return makeSuccessResponse(modelMapper.map(user, UserDto.class));
        } catch (Exception e) {
            log.error("[UpdateUser]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

    public BaseResponse<String> softDeleteUser(String id) {
        try {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isEmpty()) {
                return makeBadRequestResponse("User with given ID does not exist");
            }
            User user = userOptional.get();
            user.setDeleted(true);
            user.setDeletedAt(new Date());
            user.setUpdatedAt(new Date());
            userRepository.save(user);
            return makeSuccessResponse("Soft delete user successfully");
        } catch (Exception e) {
            log.error("[SoftDeleteUser]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

    public BaseResponse<UserDto> getDetail(String id) {
        try {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isPresent()) {
                return makeSuccessResponse(modelMapper.map(userOptional.get(), UserDto.class));
            } else {
                return makeBadRequestResponse("User with given ID does not exist");
            }
        } catch (Exception e) {
            log.error("[GetDetail]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

    public BaseResponse<List<UserDto>> getAll(
            List<String> ids,
            String employeeId, String username,
            String status, Integer active, String roleId,
            String roleName, Date startTime, Date endTime, Boolean isDelete,
            Integer page, Integer size, String sortBy, Sort.Direction order
    ) {
        try {
            Pageable pageable = createPageable(page, size, sortBy, order);

            Criteria criteria = new Criteria();
            if (!ObjectUtils.isEmpty(ids)) {
                criteria.and("id").in(ids);
            }
            if (StringUtils.isNotBlank(employeeId)) {
                criteria.and("employeeId").is(employeeId);
            }
            if (StringUtils.isNotBlank(username)) {
                criteria.and("username").regex(username, "i");
            }
            if (StringUtils.isNotBlank(status)) {
                criteria.and("status").is(status);
            }
            if (active != null) {
                criteria.and("active").is(active);
            }
            if (StringUtils.isNotBlank(roleId)) {
                criteria.and("roleId").is(roleId);
            }
            if (StringUtils.isNotBlank(roleName)) {
                criteria.and("roleName").is(roleName);
            }
            if (isDelete != null) {
                criteria.and("deleted").is(isDelete);
            } else {
                criteria.and("deleted").is(false);
            }

            if (startTime != null && endTime != null) {
                criteria.andOperator(
                        new Criteria().and("createdAt").gte(startTime),
                        new Criteria().and("createdAt").lte(endTime)
                );
            }
            else if (startTime != null) {
                criteria.and("createdAt").gte(startTime);
            }
            else if (endTime != null) {
                criteria.and("createdAt").lte(endTime);
            }

            Query filterQuery = new Query(criteria);
            long total = mongoTemplate.count(filterQuery, User.class);

            filterQuery.with(pageable);
            List<User> users = mongoTemplate.find(filterQuery, User.class);

            return makeSuccessResponse(new PageImpl<>(
                    users.stream().map(user -> modelMapper.map(user, UserDto.class)).collect(Collectors.toList()),
                    pageable, total
            ));
        } catch (Exception e) {
            log.error("[GetAll]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

    public BaseResponse<List<Role>> getAllUserRoles() {
        BaseResponse<List<Role>> baseResponse = new BaseResponse<>();
        try {
            baseResponse.success(roleRepository.findAll());
        } catch (Exception e) {
            baseResponse.internalServerError(e);
        }
        return baseResponse;
    }


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository.findByUsername(s).orElseGet(null);
    }

    public User loadUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }
}
