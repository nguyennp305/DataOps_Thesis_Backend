package com.khanh.labeling_management.controller.information_management;

import com.khanh.labeling_management.config.ApplicationProperties;
import com.khanh.labeling_management.entity.AppFile;
import com.khanh.labeling_management.model.BaseResponse;
import com.khanh.labeling_management.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("api/file")
@RequiredArgsConstructor
public class FileController extends BaseController {

    private final FileService fileService;
    private final ApplicationProperties applicationProperties;

    @PostMapping
    public ResponseEntity<BaseResponse<AppFile>> uploadFile(MultipartFile file) {
        return createResponseEntity(fileService.createFile(file));
    }

    @GetMapping("{fileName}")
    public ResponseEntity<?> getFile(@PathVariable String fileName) {
        BaseResponse<AppFile> baseResponse = fileService.getFile(fileName);
        if (baseResponse.getStatus() == HttpStatus.OK) {
            MediaType contentType = MediaType.parseMediaType(baseResponse.getData().getMimeType());
            File file = new File(applicationProperties.getFileDir(), baseResponse.getData().getPath());
            try {
                return ResponseEntity.ok()
                        .header("Content-Disposition", "inline; filename=" + baseResponse.getData().getOriginalName())
                        .contentType(contentType)
                        .body(new InputStreamResource(Files.newInputStream(file.toPath())));
            } catch (IOException e) {
                BaseResponse<String> errorResponse = new BaseResponse<>();
                errorResponse.internalServerError(e);
                return createResponseEntity(errorResponse);
            }
        } else {
            return createResponseEntity(baseResponse);
        }
    }

}
