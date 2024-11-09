package com.khanh.labeling_management.controller.information_management;


import com.khanh.labeling_management.dto.UserDto;
import com.khanh.labeling_management.dto.request.CreateUserRequest;
import com.khanh.labeling_management.dto.request.UpdateUserRequest;
import com.khanh.labeling_management.entity.information_management.Role;
import com.khanh.labeling_management.entity.information_management.User;
import com.khanh.labeling_management.model.BaseResponse;
import com.khanh.labeling_management.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

@RequestMapping("api/user")
@RestController
@RequiredArgsConstructor
public class UserController extends BaseController {

    private final UserService userService;

    @GetMapping("roles")
    @ApiOperation("Get all available user roles")
    public ResponseEntity<BaseResponse<List<Role>>> getAllUserRoles() {
        return createResponseEntity(userService.getAllUserRoles());
    }

    @PostMapping
    @ApiOperation("Create new user")
    public ResponseEntity<BaseResponse<UserDto>> createUser(@RequestBody @Valid CreateUserRequest request) {
        return createResponseEntity(userService.createUser(request));
    }

    @PutMapping
    @ApiOperation("Update user")
    public ResponseEntity<BaseResponse<UserDto>> updateUser(@RequestBody @Valid UpdateUserRequest request) {
        return createResponseEntity(userService.updateUser(request));
    }

    @GetMapping("{id}")
    @ApiOperation("Get user detail")
    public ResponseEntity<BaseResponse<UserDto>> getUser(@PathVariable String id) {
        return createResponseEntity(userService.getDetail(id));
    }

    @GetMapping
    @ApiOperation("Search user")
    public ResponseEntity<BaseResponse<List<UserDto>>> searchUser(
            @RequestParam(required = false) String ids,
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer active,
            @RequestParam(required = false) String roleId,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss") Date startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss") Date endTime,
            @RequestParam(required = false) Boolean isDelete,
            @RequestParam(required = false, defaultValue = "0")Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Sort.Direction order) {
        List<String> idList = null;
        if (StringUtils.isNotBlank(ids)) {
            idList = Arrays.asList(ids.split(","));
        }
        return createResponseEntity(userService.getAll(
                idList,
                employeeId, username, status, active, roleId, roleName,
                startTime, endTime, isDelete, page, size, sortBy, order));
    }

    @DeleteMapping("{id}")
    @ApiOperation("Delete user")
    public ResponseEntity<BaseResponse<String>> deleteUser(@PathVariable String id) {
        return createResponseEntity(userService.softDeleteUser(id));
    }

}
