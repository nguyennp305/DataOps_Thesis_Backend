package com.khanh.labeling_management.controller.information_management;

import com.khanh.labeling_management.dto.request.CreateLabelSetRequest;
import com.khanh.labeling_management.dto.request.UpdateLabelSetRequest;
import com.khanh.labeling_management.entity.data_management.labeling_management.LabelSet;
import com.khanh.labeling_management.model.BaseResponse;
import com.khanh.labeling_management.service.LabelSetService;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/label-set")
@RequiredArgsConstructor
public class LabelSetController extends BaseController {
    private final LabelSetService labelSetService;

    @PostMapping
    public ResponseEntity<BaseResponse<LabelSet>> createLabelSet(@RequestBody @Valid CreateLabelSetRequest request) {
        System.out.println(request.toString());
        return createResponseEntity(labelSetService.createLabelSet(request));
    }

    @PutMapping
    public ResponseEntity<BaseResponse<LabelSet>> updateLabelSet(@RequestBody @Valid UpdateLabelSetRequest request) {
        return createResponseEntity(labelSetService.updateLabelSet(request));
    }

    @GetMapping("{id}")
    public ResponseEntity<BaseResponse<LabelSet>> getLabelSet(@PathVariable String id) {
        return createResponseEntity(labelSetService.getLabelSet(id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<BaseResponse<String>> deleteLabelSet(@PathVariable String id) {
        return createResponseEntity(labelSetService.deleteLabelSet(id));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<LabelSet>>> search(
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
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Sort.Direction order) {
        List<String> idList = null;
        if (StringUtils.isNotBlank(ids)) {
            idList = Arrays.asList(ids.split(","));
        }
        return createResponseEntity(labelSetService.search(
                idList,
//                name, description, projectId, datasetId, createdBy, updatedBy,
                name, description, projectId, createdBy, updatedBy,
                startDate, endDate, page, size, sortBy, order));
    }

}
