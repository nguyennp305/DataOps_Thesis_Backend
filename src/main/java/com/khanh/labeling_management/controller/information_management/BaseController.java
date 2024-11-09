package com.khanh.labeling_management.controller.information_management;

import com.khanh.labeling_management.model.BaseResponse;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {

    public <T> ResponseEntity<BaseResponse<T>> createResponseEntity(BaseResponse<T> baseResponse) {
        return new ResponseEntity<>(baseResponse, baseResponse.getStatus());
    }

}
