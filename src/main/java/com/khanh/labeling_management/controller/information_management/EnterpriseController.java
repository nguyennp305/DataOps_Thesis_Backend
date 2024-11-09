package com.khanh.labeling_management.controller.information_management;

import com.khanh.labeling_management.entity.information_management.Enterprise;
import com.khanh.labeling_management.model.BaseResponse;
import com.khanh.labeling_management.model.input.EnterpriseInput;
import com.khanh.labeling_management.service.EnterpriseService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("api/enterprise")
@RequiredArgsConstructor
public class EnterpriseController extends BaseController {

    public final EnterpriseService enterpriseService;

    @CrossOrigin
    @GetMapping
    @ApiOperation("Get all enterprises")
    public ResponseEntity<BaseResponse<List<Enterprise>>> searchAllEnterprise(
            @RequestParam(required = false) String ids,
            @RequestParam(required = false) String word,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss") Date enbDate,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Sort.Direction order) {
        List<String> idList = null;
        if (StringUtils.isNotBlank(ids)) {
            idList = Arrays.asList(ids.split(","));
        }
        return createResponseEntity(enterpriseService.searchAll(
                idList, word, startDate, enbDate, page, size, sortBy, order
        ));
    }

    @CrossOrigin
    @PostMapping
    @ApiOperation("Create enterprise")
    public ResponseEntity<BaseResponse<Enterprise>> createEnterprise(@RequestBody EnterpriseInput enterpriseInput) {
        return createResponseEntity(enterpriseService.addEnterprise(enterpriseInput));
    }

    @CrossOrigin
    @PutMapping
    @ApiOperation("Update enterprise")
    public ResponseEntity<BaseResponse<Enterprise>> updateEnterprise(@RequestBody EnterpriseInput enterpriseInput) {
        return createResponseEntity(enterpriseService.updateEnterprise(enterpriseInput));
    }

    @CrossOrigin
    @DeleteMapping
    @ApiOperation("Delete enterprise")
    public ResponseEntity<BaseResponse<String>> deleteEnterprise(@RequestParam String id) {
        return createResponseEntity(enterpriseService.removeEnterprise(id));
    }

    @CrossOrigin
    @PostMapping(value = "import", headers = ("content-type=multipart/*"))
    @ApiOperation("Import enterprise from file")
    public ResponseEntity<BaseResponse<String>> importEnterprise(@RequestParam("file") MultipartFile file) {
        return createResponseEntity(enterpriseService.importEntityFromFile(file));
    }

    @CrossOrigin
    @GetMapping(value = "export")
    @ApiOperation("Export enterprise to excel")
    public void exportSentenceTopic(HttpServletResponse response,
                                    @RequestParam(name = "word", required = false) String word,
                                    @RequestParam(name = "startDate", required = false) String startDate,
                                    @RequestParam(name = "enbDate", required = false) String enbDate) throws IOException {
        ByteArrayInputStream byteArrayInputStream = enterpriseService.exportEnterprise(word,startDate,enbDate);
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=Enterprise_"+currentDateTime+".xlsx");
        IOUtils.copy(byteArrayInputStream, response.getOutputStream());
    }

}
