package com.khanh.labeling_management.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.khanh.labeling_management.config.Constants;
import lombok.*;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class BaseResponse<T> {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
    private String message;
    private String code;
    @JsonIgnore
    private HttpStatus status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer offset;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer limit;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer quantity;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long total;

    public void notFound(String message) {
        this.message = message;
        this.code = "404";
        this.status = HttpStatus.NOT_FOUND;
    }

    public void notFound() {
        this.message = Constants.NOT_FOUND;
        this.code = "404";
        this.status = HttpStatus.NOT_FOUND;
    }

    public void success(T data) {
        this.data = data;
        this.message = Constants.SUCCESS;
        this.code = "200";
        this.status = HttpStatus.OK;
    }

    public void badRequest(String message) {
        this.message = message;
        this.code = "400";
        this.status = HttpStatus.BAD_REQUEST;
    }


    public void internalServerError(Exception e) {
        this.message = Constants.INTERNAL_SERVER;
        this.code = "500";
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

}
