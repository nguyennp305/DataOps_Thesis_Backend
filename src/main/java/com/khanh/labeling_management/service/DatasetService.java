package com.khanh.labeling_management.service;

import com.khanh.labeling_management.dto.request.CreateDatasetRequest;
import com.khanh.labeling_management.dto.request.UpdateDatasetRequest;
import com.khanh.labeling_management.entity.data_management.labeling_management.Dataset;
import com.khanh.labeling_management.model.BaseResponse;
import com.khanh.labeling_management.repository.DatasetRepository;
import com.khanh.labeling_management.repository.ProjectRepository;
import com.khanh.labeling_management.repository.UserRepository;
import lombok.Data;
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
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatasetService extends BaseService {

    private final DatasetRepository datasetRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    public BaseResponse<Dataset> createDataset(CreateDatasetRequest request) {
        try {
            if (StringUtils.isNotBlank(request.getProjectId())
                && !projectRepository.existsById(request.getProjectId())) {
                return makeBadRequestResponse("Project with given ID does not exist");
            }
            if (StringUtils.isNotBlank(request.getCreatedBy())
                && !userRepository.existsById(request.getCreatedBy())) {
                return makeBadRequestResponse("User with given ID does not exist");
            }

            Dataset dataset = new Dataset();
            dataset.setName(request.getName());
            dataset.setDescription(request.getDescription());
            dataset.setProjectId(request.getProjectId());
            dataset.setLabelType(request.getLabelType());
            dataset.setLabelGroupIds(request.getLabelGroupIds());
            dataset.setLabeledImageIds(request.getLabeledImageIds());
//            dataset.setLabelIds(request.getLabelIds());
            dataset.setCreatedAt(new Date());
            dataset.setUpdatedAt(new Date());
            dataset.setCreatedBy(request.getCreatedBy());
            dataset.setUpdatedBy(request.getCreatedBy());
            datasetRepository.save(dataset);
            return makeSuccessResponse(dataset);
        } catch (Exception e) {
            log.error("[CreateDataset]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

    public BaseResponse<Dataset> updateDataset(UpdateDatasetRequest request) {
        try {
            if (StringUtils.isNotBlank(request.getUpdatedBy())
                    && !userRepository.existsById(request.getUpdatedBy())) {
                return makeBadRequestResponse("User with given ID does not exist");
            }
            if (StringUtils.isNotBlank(request.getProjectId())
                    && !projectRepository.existsById(request.getProjectId())) {
                return makeBadRequestResponse("Project with given ID does not exist");
            }
            Optional<Dataset> datasetOptional = datasetRepository.findById(request.getId());
            if (datasetOptional.isEmpty()) {
                return makeBadRequestResponse("Dataset with given ID does no exist");
            }

            Dataset dataset = datasetOptional.get();
            if (StringUtils.isNotBlank(request.getName())) {
                dataset.setName(request.getName());
            }
            if (StringUtils.isNotBlank(request.getDescription())) {
                dataset.setDescription(request.getDescription());
            }
            if (StringUtils.isNotBlank(request.getProjectId())) {
                dataset.setProjectId(request.getProjectId());
            }
            if (StringUtils.isNotBlank(request.getLabelType())) {
                dataset.setLabelType(request.getLabelType());
            }
            if (request.getLabelGroupIds() != null) {
                dataset.setLabelGroupIds(request.getLabelGroupIds());
            }
//            if (request.getLabelIds() != null) {
//                dataset.setLabelIds(request.getLabelIds());
//            }
            if (request.getLabeledImageIds() != null) {
                dataset.setLabeledImageIds(request.getLabeledImageIds());
            }
            dataset.setUpdatedAt(new Date());
            dataset.setUpdatedBy(request.getUpdatedBy());
            datasetRepository.save(dataset);
            return makeSuccessResponse(dataset);
        } catch (Exception e) {
            log.error("[UpdateDataset]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

    public BaseResponse<Dataset> getDataset(String id) {
        try {
            Optional<Dataset> datasetOptional = datasetRepository.findById(id);
            if (datasetOptional.isEmpty()) {
                return makeBadRequestResponse("Dataset with given ID does not exist");
            }
            return makeSuccessResponse(datasetOptional.get());
        } catch (Exception e) {
            log.error("[GetDataset]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

    public BaseResponse<String> deleteDataset(String id) {
        try {
            Optional<Dataset> datasetOptional = datasetRepository.findById(id);
            if (datasetOptional.isEmpty()) {
                return makeBadRequestResponse("Dataset with given ID does not exist");
            }
            datasetRepository.deleteById(id);
            return makeSuccessResponse("Dataset deleted successfully");
        } catch (Exception e) {
            log.error("[DeleteDataset]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

    public BaseResponse<List<Dataset>> search(
            List<String> ids,
            String name, String description,
            String projectId, String labelType, Date startDate, Date endDate,
            Integer page, Integer size, String sortBy, Sort.Direction order) {
        try {
            Pageable pageable = createPageable(page, size, sortBy, order);
            Query query = new Query();
            if (!ObjectUtils.isEmpty(ids)) {
                query.addCriteria(Criteria.where("id").in(ids));
            }
            if (StringUtils.isNotBlank(name)) {
                query.addCriteria(Criteria.where("name").regex(name, "i"));
            }
            if (StringUtils.isNotBlank(description)) {
                query.addCriteria(Criteria.where("description").regex(description, "i"));
            }
            if (StringUtils.isNotBlank(projectId)) {
                query.addCriteria(Criteria.where("projectId").is(projectId));
            }
            if (StringUtils.isNotBlank(labelType)) {
                query.addCriteria(Criteria.where("labelType").is(labelType));
            }
            if (startDate != null && endDate != null) {
                query.addCriteria(Criteria.where("createdAt")
                        .gte(startDate).lte(endDate));
            } else if (startDate != null) {
                query.addCriteria(Criteria.where("createdAt").gte(startDate));
            } else if (endDate != null) {
                query.addCriteria(Criteria.where("createdAt").lte(endDate));
            }

            long total = mongoTemplate.count(query, Dataset.class);
            query.with(pageable);
            List<Dataset> datasets = mongoTemplate.find(query, Dataset.class);
            return makeSuccessResponse(new PageImpl<>(datasets, pageable, total));
        } catch (Exception e) {
            log.error("[SearchDataset]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

}
