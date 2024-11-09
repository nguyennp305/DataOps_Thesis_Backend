package com.khanh.labeling_management.controller.information_management;

import com.khanh.labeling_management.dto.request.LoginRequest;
import com.khanh.labeling_management.dto.response.LoginResponse;
import com.khanh.labeling_management.entity.information_management.User;
import com.khanh.labeling_management.model.BaseResponse;
import com.khanh.labeling_management.security.JsonWebTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping(path = "api")
@RequiredArgsConstructor
public class AuthenticationController extends BaseController {
    private final AuthenticationManager authenticationManager;
    private final JsonWebTokenProvider tokenProvider;

    @PostMapping("login")
    public ResponseEntity<BaseResponse<LoginResponse>> authenticateUser(@RequestBody @Valid LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Trả về jwt cho người dùng.
        String jwt = tokenProvider.generateToken((User) authentication.getPrincipal());
        BaseResponse<LoginResponse> baseResponse = new BaseResponse<>();
        baseResponse.success(new LoginResponse(
                jwt,
                "Bearer",
                (User) authentication.getPrincipal()
        ));
        return createResponseEntity(baseResponse);
    }
}
