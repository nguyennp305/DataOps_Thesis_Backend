package com.khanh.labeling_management.service;

import com.khanh.labeling_management.config.ApplicationProperties;
import com.khanh.labeling_management.entity.AppFile;
import com.khanh.labeling_management.model.BaseResponse;
import com.khanh.labeling_management.repository.AppFileRepository;
import com.khanh.labeling_management.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService extends BaseService {
    private final AppFileRepository fileRepository;
    private final ApplicationProperties applicationProperties;

    public BaseResponse<AppFile> createFile(@NotNull MultipartFile multipartFile) {
        try {
            Path path = Paths.get(applicationProperties.getFileDir(), DateUtils.format(new Date(), "dd-MM-yyyy"));
            String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
            File diskFile = new File(path.toString(), UUID.randomUUID() + "." + extension);
            diskFile.getParentFile().mkdirs();
            multipartFile.transferTo(diskFile);

            AppFile file = new AppFile();
            file.setName(diskFile.getName());
            file.setOriginalName(multipartFile.getOriginalFilename());
            file.setMimeType(multipartFile.getContentType());
            file.setSize(multipartFile.getSize());
            file.setExtension(FilenameUtils.getExtension(multipartFile.getOriginalFilename()));

            String relativePath = Paths.get(applicationProperties.getFileDir()).relativize(diskFile.toPath()).toString();
            file.setPath(relativePath);

            String mimeType = file.getMimeType();
            if (mimeType != null) {
                switch (mimeType) {
                    case "image/jpeg":
                    case "image/png":
                    case "image/gif":
                    case "image/svg+xml":
                    case "image/tiff":
                    case "image/webp":
                        file.setType("image");
                        break;
                    case "application/msword":
                    case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                    case "application/vnd.oasis.opendocument.text":
                        file.setType("docx");
                        break;
                    case "application/pdf":
                        file.setType("pdf");
                        break;
                }
            }
            file.setCreatedAt(new Date());
            file.setUpdatedAt(new Date());
            fileRepository.save(file);
            return makeSuccessResponse(file);
        } catch (Exception e) {
            log.error("[CreateFile]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

    public BaseResponse<AppFile> createFile(@NotNull File file) {
        try {
            Path path = Paths.get(applicationProperties.getFileDir(), DateUtils.format(new Date(), "dd-MM-yyyy"));
            String extension = FilenameUtils.getExtension(file.getName());
            File diskFile = new File(path.toString(), UUID.randomUUID() + "." + extension);
            diskFile.getParentFile().mkdirs();
            FileUtils.copyFile(file, diskFile);

            AppFile appFile = new AppFile();
            appFile.setName(diskFile.getName());
            appFile.setOriginalName(file.getName());
            appFile.setMimeType(
                    Files.probeContentType(file.toPath())
            );
            appFile.setSize(FileUtils.sizeOf(file));
            appFile.setExtension(FilenameUtils.getExtension(file.getName()));

            String relativePath = Paths.get(applicationProperties.getFileDir()).relativize(diskFile.toPath()).toString();
            appFile.setPath(relativePath);

            String mimeType = appFile.getMimeType();
            if (mimeType != null) {
                switch (mimeType) {
                    case "image/jpeg":
                    case "image/png":
                    case "image/gif":
                    case "image/svg+xml":
                    case "image/tiff":
                    case "image/webp":
                        appFile.setType("image");
                        break;
                    case "application/msword":
                    case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                    case "application/vnd.oasis.opendocument.text":
                        appFile.setType("docx");
                        break;
                    case "application/pdf":
                        appFile.setType("pdf");
                        break;
                }
            }
            appFile.setCreatedAt(new Date());
            appFile.setUpdatedAt(new Date());
            fileRepository.save(appFile);
            return makeSuccessResponse(appFile);
        } catch (Exception e) {
            log.error("[CreateFile]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

    public BaseResponse<AppFile> getFile(@NotNull String fileName) {
        try {
            Optional<AppFile> fileOptional = fileRepository.findByName(fileName);
            if (fileOptional.isEmpty()) {
                return makeNotFoundResponse("File not found");
            }
            return makeSuccessResponse(fileOptional.get());
        } catch (Exception e) {
            log.error("[GetFile]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

    public String generateFileUrl(AppFile appFile) {
        return applicationProperties.getDomain() + "/api/file/" + appFile.getName();
    }

    public String generateFileUrl(String fileId) {
        Optional<AppFile> fileOptional = fileRepository.findById(fileId);
        if (fileOptional.isEmpty()) {
            return null;
        }
        AppFile appFile = fileOptional.get();
        return applicationProperties.getDomain() + "/api/file/" + appFile.getName();
    }

}
