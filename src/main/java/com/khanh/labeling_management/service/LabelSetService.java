package com.khanh.labeling_management.service;

import com.khanh.labeling_management.dto.request.CreateLabelSetRequest;
import com.khanh.labeling_management.dto.request.UpdateLabelSetRequest;
import com.khanh.labeling_management.entity.data_management.labeling_management.LabelSet;
import com.khanh.labeling_management.model.BaseResponse;
import com.khanh.labeling_management.repository.DatasetRepository;
import com.khanh.labeling_management.repository.LabelSetRepository;
import com.khanh.labeling_management.repository.ProjectRepository;
import com.khanh.labeling_management.repository.UserRepository;
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
public class LabelSetService extends BaseService {
    private final LabelSetRepository labelSetRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final DatasetRepository datasetRepository;
    private final MongoTemplate mongoTemplate;

    public BaseResponse<LabelSet> createLabelSet(CreateLabelSetRequest request) {
        try {
            if (StringUtils.isNotBlank(request.getProjectId()) && !projectRepository.existsById(request.getProjectId())) {
                return makeBadRequestResponse("Project with given ID does not exist");
            }
//            if (StringUtils.isNotBlank(request.getDatasetId()) && !datasetRepository.existsById(request.getDatasetId())) {
//                return makeBadRequestResponse("Dataset with given ID does not exist");
//            }
            if (StringUtils.isNotBlank(request.getCreatedBy()) && !userRepository.existsById(request.getCreatedBy())) {
                return makeBadRequestResponse("User with given ID does not exist");
            }
            LabelSet labelSet = new LabelSet();
            labelSet.setName(request.getName());
            labelSet.setDescription(request.getDescription());
            labelSet.setProjectId(request.getProjectId());
//            labelSet.setDatasetId(request.getDatasetId());
            labelSet.setLabelIds(request.getLabelIds());
            labelSet.setCreatedBy(request.getCreatedBy());
            labelSet.setUpdatedBy(request.getCreatedBy());
            labelSet.setCreatedAt(new Date());
            labelSet.setUpdatedAt(new Date());
            labelSetRepository.save(labelSet);
            return makeSuccessResponse(labelSet);
        } catch (Exception e) {
            log.error("[CreateLabelSet]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

    public BaseResponse<LabelSet> updateLabelSet(UpdateLabelSetRequest request) {
        try {
            Optional<LabelSet> labelSetOptional = labelSetRepository.findById(request.getId());
            if (labelSetOptional.isEmpty()) {
                return makeBadRequestResponse("LabelSet with given ID does not exist");
            }
            if (StringUtils.isNotBlank(request.getProjectId()) && !projectRepository.existsById(request.getProjectId())) {
                return makeBadRequestResponse("Project with given ID does not exist");
            }
//            if (StringUtils.isNotBlank(request.getDatasetId()) && !datasetRepository.existsById(request.getDatasetId())) {
//                return makeBadRequestResponse("Dataset with given ID does not exist");
//            }
            if (StringUtils.isNotBlank(request.getUpdatedBy()) && !userRepository.existsById(request.getUpdatedBy())) {
                return makeBadRequestResponse("User with given ID does not exist");
            }
            LabelSet labelSet = labelSetOptional.get();
            if (StringUtils.isNotBlank(request.getName())) {
                labelSet.setName(request.getName());
            }
            if (StringUtils.isNotBlank(request.getDescription())) {
                labelSet.setDescription(request.getDescription());
            }
            if (StringUtils.isNotBlank(request.getProjectId())) {
                labelSet.setProjectId(request.getProjectId());
            }
//            if (StringUtils.isNotBlank(request.getDatasetId())) {
//                labelSet.setDatasetId(request.getDatasetId());
//            }
            if (request.getLabelIds() != null) {
                labelSet.setLabelIds(request.getLabelIds());
            }
            labelSet.setUpdatedBy(request.getUpdatedBy());
            labelSet.setUpdatedAt(new Date());
            labelSetRepository.save(labelSet);
            return makeSuccessResponse(labelSet);
        } catch (Exception e) {
            log.error("[UpdateLabelSet]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

    public BaseResponse<LabelSet> getLabelSet(String id) {
        try {
            Optional<LabelSet> labelSetOptional = labelSetRepository.findById(id);
            if (labelSetOptional.isEmpty()) {
                return makeBadRequestResponse("LabelSet with given ID does not exist");
            }
            return makeSuccessResponse(labelSetOptional.get());
        } catch (Exception e) {
            log.error("[GetLabelSet]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

    public BaseResponse<String> deleteLabelSet(String id) {
        try {
            Optional<LabelSet> labelSetOptional = labelSetRepository.findById(id);
            if (labelSetOptional.isEmpty()) {
                return makeBadRequestResponse("LabelSet with given ID does not exist");
            }
            labelSetRepository.deleteById(id);
            return makeSuccessResponse("Delete LabelSet successfully");
        } catch (Exception e) {
            log.error("[DeleteLabelSet]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

    public BaseResponse<List<LabelSet>> search(
            List<String> ids,
//            String name, String description, String projectId, String datasetId,
            String name, String description, String projectId,
            String createdBy, String updatedBy, Date startDate, Date endDate,
            Integer page, Integer size, String sortBy, Sort.Direction order) {
        try {
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
//            if (StringUtils.isNotBlank(datasetId)) {
//                query.addCriteria(Criteria.where("datasetId").is(datasetId));
//            }
            if (StringUtils.isNotBlank(createdBy)) {
                query.addCriteria(Criteria.where("createdBy").is(createdBy));
            }
            if (StringUtils.isNotBlank(updatedBy)) {
                query.addCriteria(Criteria.where("updatedBy").is(updatedBy));
            }
            query.addCriteria(generateCreatedDateCriteria(startDate, endDate));
            Pageable pageable = createPageable(page, size, sortBy, order);
            long total = mongoTemplate.count(query, LabelSet.class);
            query.with(pageable);
            List<LabelSet> labelSets = mongoTemplate.find(query, LabelSet.class);
            return makeSuccessResponse(new PageImpl<>(labelSets, pageable, total));
        } catch (Exception e) {
            log.error("[SearchLabelSet]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

}
