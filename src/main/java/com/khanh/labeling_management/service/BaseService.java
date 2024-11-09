package com.khanh.labeling_management.service;

import com.khanh.labeling_management.model.BaseResponse;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public abstract class BaseService {

    @Autowired
    protected ModelMapper modelMapper;

    public Pageable createPageable(Integer page, Integer size, String sortBy, Sort.Direction order) {
        if (page == null) {
            page = 0;
        }
        if (size == null) {
            size = 20;
        }
        Pageable pageable = PageRequest.of(page, size);
        if (StringUtils.isNotBlank(sortBy)) {
            pageable = PageRequest.of(page, size,
                    Objects.equals(order, Sort.Direction.DESC) ?
                            Sort.by(order, sortBy) : Sort.by(Sort.Direction.ASC, sortBy)
            );
        }
        return pageable;
    }

    public <T> BaseResponse<T> makeNotFoundResponse(String message) {
        BaseResponse<T> baseResponse = new BaseResponse<>();
        baseResponse.notFound(message);
        return baseResponse;
    }

    public <T> BaseResponse<T> makeSuccessResponse(T data) {
        BaseResponse<T> baseResponse = new BaseResponse<>();
        baseResponse.success(data);
        return baseResponse;
    }

    public <T> BaseResponse<List<T>> makeSuccessResponse(Page<T> page) {
        BaseResponse<List<T>> baseResponse = makeSuccessResponse(page.getContent());
        baseResponse.setQuantity(page.getContent().size());
        baseResponse.setOffset(page.getPageable().getPageNumber() * page.getPageable().getPageSize());
        baseResponse.setLimit(page.getPageable().getPageSize());
        baseResponse.setTotal(page.getTotalElements());
        return baseResponse;
    }

    public <T> BaseResponse<T> makeBadRequestResponse(String message) {
        BaseResponse<T> baseResponse = new BaseResponse<>();
        baseResponse.badRequest(message);
        return baseResponse;
    }

    public <T> BaseResponse<T> makeInternalServerErrorResponse(Exception e) {
        BaseResponse<T> baseResponse = new BaseResponse<>();
        baseResponse.internalServerError(e);
        return baseResponse;
    }

    public final Criteria generateCreatedDateCriteria(Date from, Date to) {
        Criteria criteria = new Criteria();
        if (from != null && to != null) {
            criteria.andOperator(
                    Criteria.where("createdAt").gte(from),
                    Criteria.where("createdAt").lt(to)
            );
        } else if (from != null) {
            criteria.and("createdAt").gte(from);
        } else if (to != null) {
            criteria.and("createdAt").lt(to);
        }
        return criteria;
    }

}
