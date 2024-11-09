package com.khanh.labeling_management.service;

import com.khanh.labeling_management.dto.request.CreateReportRequest;
import com.khanh.labeling_management.dto.request.UpdateReportRequest;
import com.khanh.labeling_management.entity.Report;
import com.khanh.labeling_management.model.BaseResponse;
import com.khanh.labeling_management.repository.ProjectRepository;
import com.khanh.labeling_management.repository.ReportRepository;
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

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService extends BaseService {

    private final ReportRepository reportRepository;
    private final ProjectRepository projectRepository;
    private final MongoTemplate mongoTemplate;

    public BaseResponse<Report> createReport(CreateReportRequest request) {
        try {
            if (StringUtils.isNotBlank(request.getProjectId()) && !projectRepository.existsById(request.getProjectId())) {
                return makeBadRequestResponse("Project with given ID does not exist");
            }
            Report report = modelMapper.map(request, Report.class);
            report.setCreatedAt(new Date());
            report.setUpdatedAt(new Date());
            reportRepository.save(report);
            return makeSuccessResponse(report);
        } catch (Exception e) {
            log.error("[CreateReport]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

    public BaseResponse<Report> updateReport(UpdateReportRequest request) {
        Optional<Report> reportOptional = reportRepository.findById(request.getId());
        if (reportOptional.isEmpty()) {
            return makeBadRequestResponse("Report with given ID does not exist");
        }
        if (StringUtils.isNotBlank(request.getProjectId()) && !projectRepository.existsById(request.getProjectId())) {
            return makeBadRequestResponse("Project with given ID does not exist");
        }
        Report report = reportOptional.get();
        if (StringUtils.isNotBlank(request.getProjectId())) {
            report.setProjectId(request.getProjectId());
        }
        if (StringUtils.isNotBlank(request.getName())) {
            report.setName(request.getName());
        }
        if (StringUtils.isNotBlank(request.getDescription())) {
            report.setDescription(request.getDescription());
        }
        if (StringUtils.isNotBlank(request.getReportType())) {
            report.setReportType(request.getReportType());
        }
        if (StringUtils.isNotBlank(request.getData())) {
            report.setData(request.getData());
        }
        report.setUpdatedAt(new Date());
        reportRepository.save(report);
        return makeSuccessResponse(report);
    }

    public BaseResponse<Report> getReport(String id) {
        Optional<Report> reportOptional = reportRepository.findById(id);
        if (reportOptional.isEmpty()) {
            return makeBadRequestResponse("Report with given ID does not exist");
        }
        Report report = reportOptional.get();
        return makeSuccessResponse(report);
    }

    public BaseResponse<String> deleteReport(String id) {
        if (!reportRepository.existsById(id)) {
            return makeBadRequestResponse("Report with given ID does not exist");
        }
        reportRepository.deleteById(id);
        return makeSuccessResponse("Report deleted successfully");
    }

    public BaseResponse<List<Report>> filter(
            String name, String description, String projectId, String reportType,
            Integer page, Integer size, String sortBy, Sort.Direction order
    ) {
        Pageable pageable = createPageable(page, size, sortBy, order);
        Query query = new Query();
        if (StringUtils.isNotBlank(name)) {
            query.addCriteria(Criteria.where("name").regex(name, "i"));
        }
        if (StringUtils.isNotBlank(description)) {
            query.addCriteria(Criteria.where("description").regex(description, "i"));
        }
        if (StringUtils.isNotBlank(projectId)) {
            query.addCriteria(Criteria.where("projectId").is(projectId));
        }
        if (StringUtils.isNotBlank(reportType)) {
            query.addCriteria(Criteria.where("reportType").is(reportType));
        }
        long total = mongoTemplate.count(query, Report.class);
        query.with(pageable);
        List<Report> reports = mongoTemplate.find(query, Report.class);
        return makeSuccessResponse(
                new PageImpl<>(reports, pageable, total)
        );
    }

}
