package com.khanh.labeling_management.controller.information_management;

import com.khanh.labeling_management.dto.request.CreateReportRequest;
import com.khanh.labeling_management.dto.request.UpdateReportRequest;
import com.khanh.labeling_management.entity.Report;
import com.khanh.labeling_management.model.BaseResponse;
import com.khanh.labeling_management.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/report")
@RequiredArgsConstructor
public class ReportController extends BaseController {
    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<BaseResponse<Report>> createReport(
            @RequestBody @Valid CreateReportRequest createReportRequest) {
        return createResponseEntity(reportService.createReport(createReportRequest));
    }

    @PutMapping
    public ResponseEntity<BaseResponse<Report>> updateReport(
            @RequestBody @Valid UpdateReportRequest updateReportRequest) {
        return createResponseEntity(reportService.updateReport(updateReportRequest));
    }

    @GetMapping("{id}")
    public ResponseEntity<BaseResponse<Report>> getReport(@PathVariable String id) {
        return createResponseEntity(reportService.getReport(id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<BaseResponse<String>> deleteReport(@PathVariable String id) {
        return createResponseEntity(reportService.deleteReport(id));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<Report>>> filter(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String projectId,
            @RequestParam(required = false) String reportType,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Sort.Direction order) {
        return createResponseEntity(reportService.filter(
                name, description, projectId, reportType, page, size, sortBy, order));
    }

}
