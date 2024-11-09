package com.khanh.labeling_management.controller.information_management;

import com.khanh.labeling_management.dto.DataDto;
import com.khanh.labeling_management.dto.request.AddLabeledCropImageRequest;
import com.khanh.labeling_management.dto.request.CreateDataRequest;
import com.khanh.labeling_management.dto.request.UpdateDataRequest;
import com.khanh.labeling_management.entity.data_management.labeling_management.Data;
import com.khanh.labeling_management.entity.data_management.labeling_management.LabeledImage;
import com.khanh.labeling_management.model.BaseResponse;
import com.khanh.labeling_management.service.DataService;
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
@RequestMapping("api/data")
@RequiredArgsConstructor
public class DataController extends BaseController {
    private final DataService dataService;

    @PostMapping("import")
    public ResponseEntity<BaseResponse<String>> importFromExcelFile(
            @RequestParam MultipartFile file
    ) throws IOException {
        return createResponseEntity(dataService.importByExcel(file));
    }

    @PostMapping("crop-label")
    public ResponseEntity<BaseResponse<Data>> createLabeledCropImage(
            @RequestBody @Valid AddLabeledCropImageRequest request) {
        return createResponseEntity(dataService.addLabeledCropImage(request));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<DataDto>> createData(
           @RequestParam MultipartFile image,  @ModelAttribute @Valid CreateDataRequest request) {
        return createResponseEntity(dataService.createData(request, image));
    }

    @PutMapping
    public ResponseEntity<BaseResponse<DataDto>> updateDate(@RequestParam(required = false) MultipartFile image, @ModelAttribute @Valid UpdateDataRequest request) {
        return createResponseEntity(dataService.updateData(request, image));
    }

    @GetMapping("{id}")
    public ResponseEntity<BaseResponse<DataDto>> getData(@PathVariable String id) {
        return createResponseEntity(dataService.getData(id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<BaseResponse<String>> deleteData(@PathVariable String id) {
        return createResponseEntity(dataService.deleteData(id));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<DataDto>>> search(
            @RequestParam(required = false) String ids,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String projectId,
//            @RequestParam(required = false) String datasetId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String createdBy,
            @RequestParam(required = false) String updatedBy,
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
        return createResponseEntity(dataService.search(
                idList,
//                name, description, projectId, datasetId, status, createdBy, updatedBy,
                name, description, projectId, status, createdBy, updatedBy,
                startDate, endDate, page, size, sortBy, order));
    }
}
