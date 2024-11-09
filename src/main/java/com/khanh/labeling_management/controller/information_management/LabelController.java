package com.khanh.labeling_management.controller.information_management;

import com.khanh.labeling_management.dto.request.CreateLabelRequest;
import com.khanh.labeling_management.dto.request.UpdateLabelRequest;
import com.khanh.labeling_management.entity.data_management.labeling_management.Label;
import com.khanh.labeling_management.model.BaseResponse;
import com.khanh.labeling_management.service.LabelService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/label")
@RequiredArgsConstructor
public class LabelController extends BaseController {
    private final LabelService labelService;

    @PostMapping("import")
    public ResponseEntity<BaseResponse<String>> importFromExcelFile(
            @RequestParam MultipartFile file
    ) throws IOException {
        return createResponseEntity(labelService.importByExcel(file));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<Label>> createLabel(@RequestBody @Valid CreateLabelRequest request) {
        return createResponseEntity(labelService.createLabel(request));
    }

    @PutMapping
    public ResponseEntity<BaseResponse<Label>> updateLabel(@RequestBody @Valid UpdateLabelRequest request) {
        System.out.println(request);
        return createResponseEntity(labelService.updateLabel(request));
    }

    @GetMapping("{id}")
    public ResponseEntity<BaseResponse<Label>> getLabel(@PathVariable String id) {
        return createResponseEntity(labelService.getLabel(id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<BaseResponse<String>> deleteLabel(@PathVariable String id) {
        return createResponseEntity(labelService.deleteLabel(id));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<Label>>> search(
            @RequestParam(required = false) String ids,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String projectId,
//            @RequestParam(required = false) String datasetId,
            @RequestParam(required = false) String createdBy,
            @RequestParam(required = false) String updatedBy,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss") Date endDate,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20")Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Sort.Direction order
    ) {
        List<String> idList = null;
        if (StringUtils.isNotBlank(ids)) {
            idList = Arrays.asList(ids.split(","));
        }
        return createResponseEntity(labelService.search(
                idList,
//                name, description, projectId, datasetId,
                name, description, projectId,
                createdBy, updatedBy, startDate, endDate,
                page, size, sortBy, order
        ));
    }

}
