package com.khanh.labeling_management.controller.information_management;

import com.khanh.labeling_management.dto.request.CreateDatasetRequest;
import com.khanh.labeling_management.dto.request.UpdateDatasetRequest;
import com.khanh.labeling_management.entity.data_management.labeling_management.Dataset;
import com.khanh.labeling_management.model.BaseResponse;
import com.khanh.labeling_management.service.DatasetService;
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
@RequestMapping("api/dataset")
@RequiredArgsConstructor
public class DatasetController extends BaseController {
    private final DatasetService datasetService;

    @PostMapping
    public ResponseEntity<BaseResponse<Dataset>> createDataset(@RequestBody @Valid CreateDatasetRequest request) {
        return createResponseEntity(datasetService.createDataset(request));
    }

    @PutMapping
    public ResponseEntity<BaseResponse<Dataset>> updateDataset(@RequestBody @Valid UpdateDatasetRequest request) {
        return createResponseEntity(datasetService.updateDataset(request));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<BaseResponse<String>> deleteDataset(@PathVariable String id) {
        return createResponseEntity(datasetService.deleteDataset(id));
    }

    @GetMapping("{id}")
    public ResponseEntity<BaseResponse<Dataset>> getDataset(@PathVariable String id) {
        return createResponseEntity(datasetService.getDataset(id));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<Dataset>>> search(
            @RequestParam(required = false) String ids,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String projectId,
            @RequestParam(required = false) String labelType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss") Date endDate,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Sort.Direction order
            ) {
        List<String> idList = null;
        if (StringUtils.isNotBlank(ids)) {
            idList = Arrays.asList(ids.split(","));
        }
        return createResponseEntity(datasetService.search(
                idList,
                name, description, projectId, labelType, startDate, endDate, page, size, sortBy, order));
    }

}
