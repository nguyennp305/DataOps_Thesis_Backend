package com.khanh.labeling_management.service;

import com.khanh.labeling_management.dto.DataDto;
import com.khanh.labeling_management.dto.request.AddLabeledCropImageRequest;
import com.khanh.labeling_management.dto.request.CreateDataRequest;
import com.khanh.labeling_management.dto.request.UpdateDataRequest;
import com.khanh.labeling_management.entity.AppFile;
import com.khanh.labeling_management.entity.data_management.labeling_management.Data;
import com.khanh.labeling_management.entity.data_management.labeling_management.Label;
import com.khanh.labeling_management.entity.data_management.labeling_management.LabeledImage;
import com.khanh.labeling_management.model.BaseResponse;
import com.khanh.labeling_management.repository.*;
import com.khanh.labeling_management.utils.ExcelUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataService extends BaseService {
    private final DataRepository dataRepository;
    private final ProjectRepository projectRepository;
    private final DatasetRepository datasetRepository;
    private final LabelRepository labelRepository;
    private final UserRepository userRepository;
    private final FileService fileService;
    private final MongoTemplate mongoTemplate;
    private final ModelMapper modelMapper;
    private final OkHttpClient httpClient = new OkHttpClient.Builder().build();

    public BaseResponse<String> importByExcel(MultipartFile file) throws IOException {
        if (!com.khanh.labeling_management.utils.ObjectUtils.inOptions(FilenameUtils.getExtension(file.getOriginalFilename()), "xls", "xlsx")) {
            return makeBadRequestResponse("Unsupported file format");
        }
        File temp = new File(FileUtils.getTempDirectory(),
                UUID.randomUUID().toString() + "/" + file.getOriginalFilename());
        temp.getParentFile().mkdirs();
        file.transferTo(temp);
        List<Data> dataList = readExcel(temp);
        for (Data data : dataList) {
            data.setCreatedAt(new Date());
            data.setUpdatedAt(new Date());
        }
        dataRepository.saveAll(dataList);
        return makeSuccessResponse(String.format("Created %d labels", dataList.size()));
    }

    public List<Data> readExcel(File file) throws IOException {
        List<Data> dataList = new ArrayList<>();

        // Get file
        InputStream inputStream = Files.newInputStream(file.toPath());

        // Get workbook
        Workbook workbook = ExcelUtils.getWorkbook(inputStream, file.getAbsolutePath());

        // Get sheet
        Sheet sheet = workbook.getSheetAt(0);

        // Get all rows
        Iterator<Row> iterator = sheet.iterator();
        while (iterator.hasNext()) {
            Row nextRow = iterator.next();
            if (nextRow.getRowNum() == 0) {
                // Ignore header
                continue;
            }

            // Get all cells
            Iterator<Cell> cellIterator = nextRow.cellIterator();

            // Read cells and set value for book object
            Data data = new Data();
            while (cellIterator.hasNext()) {
                //Read cell
                Cell cell = cellIterator.next();
                Object cellValue = ExcelUtils.getCellValue(cell);
                if (cellValue == null || cellValue.toString().isEmpty()) {
                    continue;
                }
                // Set value for book object
                int columnIndex = cell.getColumnIndex();
                switch (columnIndex) {
                    case 0:
                        data.setName(cellValue.toString());
                        break;
                    case 1:
                        data.setDescription(cellValue.toString());
                        break;
                    case 2:
                        data.setProjectId(cellValue.toString());
                        break;
                    case 3:
                        String imageUrl = cellValue.toString();
                        Request request = new Request.Builder()
                                .url(imageUrl)
                                .get().build();
                        try (Response response = httpClient.newCall(request).execute()) {
                            if (!response.isSuccessful()) {
                                throw new IllegalArgumentException("Cannot retrieve image file from provided url");
                            } else {
                                String requestUrl = response.request().url().toString();
                                String fileName = requestUrl.split("/")
                                        [requestUrl.split("/").length - 1];
                                String contentDisposition = response.header("Content-Disposition");
                                if (contentDisposition != null && contentDisposition.contains("filename=")) {
                                    fileName = contentDisposition.substring(
                                            contentDisposition.indexOf("filename=")
                                            + "filename=".length()
                                    );
                                    fileName = fileName.replace("\"", "");
                                }
                                File tempFile = new File(FileUtils.getTempDirectory(), UUID.randomUUID() + "/"
                                    + fileName);
                                FileUtils.writeByteArrayToFile(
                                        tempFile, response.body().bytes()
                                );
                                BaseResponse<AppFile> createFileResponse = fileService.createFile(tempFile);
                                if (createFileResponse.getStatus() != HttpStatus.OK) {
                                    throw new IllegalArgumentException("Cannot retrieve image file from provided url");
                                }
                                data.setImageFileId(createFileResponse.getData().getId());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new IllegalArgumentException("Cannot retrieve image file from provided url");
                        }
//                        data.setImageFileId(cellValue.toString());
                        break;
                    case 4:
                        data.setStatus(cellValue.toString());
                        break;
                    case 5:
                        data.setCreatedBy(cellValue.toString());
                        break;
                    default:
                        break;
                }

            }
            dataList.add(data);
        }

        workbook.close();
        inputStream.close();

        return dataList;
    }

    public BaseResponse<DataDto> createData(CreateDataRequest request, MultipartFile image) {
        try {
            if (StringUtils.isNotBlank(request.getProjectId())
                && !projectRepository.existsById(request.getProjectId())) {
                return makeBadRequestResponse("Project with given ID does not exist");
            }
//            if (StringUtils.isNotBlank(request.getDatasetId())
//                && !datasetRepository.existsById(request.getDatasetId())) {
//                return makeBadRequestResponse("Dataset with given ID does not exist");
//            }
            if (StringUtils.isNotBlank(request.getCreatedBy())
                && !userRepository.existsById(request.getCreatedBy())) {
                return makeBadRequestResponse("User with given ID does not exist");
            }
            BaseResponse<AppFile> createFileResponse = fileService.createFile(image);
            if (createFileResponse.getStatus() != HttpStatus.OK) {
                return makeBadRequestResponse("Failed to create image file");
            }

            Data data = new Data();
            data.setName(request.getName());
            data.setImageFileId(createFileResponse.getData().getId());
            data.setProjectId(request.getProjectId());
//            data.setDatasetId(request.getDatasetId());
            data.setStatus(request.getStatus());
            data.setDescription(request.getDescription());
            data.setLabeledIdClassification(request.getLabeledIdClassification());
            data.setCreatedBy(request.getCreatedBy());
            data.setLabeledImages(request.getLabeledImages());
            data.setCreatedAt(new Date());
            data.setUpdatedAt(new Date());
            data.setCreatedBy(request.getCreatedBy());
            data.setUpdatedBy(request.getCreatedBy());
            dataRepository.save(data);
            DataDto dataDto = modelMapper.map(data, DataDto.class);
            dataDto.setImageUrl(fileService.generateFileUrl(data.getImageFileId()));
            return makeSuccessResponse(dataDto);
        } catch (Exception e) {
            log.error("[CreateData]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

    public BaseResponse<DataDto> updateData(UpdateDataRequest request, MultipartFile image) {
        try {
            Optional<Data> dataOptional = dataRepository.findById(request.getId());
            if (dataOptional.isEmpty()) {
                return makeBadRequestResponse("Data with given ID does not exist");
            }
            if (StringUtils.isNotBlank(request.getProjectId())
                && !projectRepository.existsById(request.getProjectId())) {
                return makeBadRequestResponse("Project with given ID does not exist");
            }
//            if (StringUtils.isNotBlank(request.getDatasetId())
//                && !datasetRepository.existsById(request.getDatasetId())) {
//                return makeBadRequestResponse("Dataset with given ID does not exist");
//            }
            if (StringUtils.isNotBlank(request.getUpdatedBy())
                && !userRepository.existsById(request.getUpdatedBy())) {
                return makeBadRequestResponse("User with given ID does not exist");
            }
            Data data = dataOptional.get();
            if (image != null) {
                BaseResponse<AppFile> createFileResponse = fileService.createFile(image);
                if (createFileResponse.getStatus() != HttpStatus.OK) {
                    return makeBadRequestResponse("Failed to create image file");
                } else {
                    data.setImageFileId(createFileResponse.getData().getId());
                }
            }
            if (StringUtils.isNotBlank(request.getName())) {
                data.setName(request.getName());
            }
            if (StringUtils.isNotBlank(request.getProjectId())) {
                data.setProjectId(request.getProjectId());
            }
//            if (StringUtils.isNotBlank(request.getDatasetId())) {
//                data.setDatasetId(request.getDatasetId());
//            }
            if (StringUtils.isNotBlank(request.getStatus())) {
                data.setStatus(request.getStatus());
            }
            if (StringUtils.isNotBlank(request.getDescription())) {
                data.setDescription(request.getDescription());
            }
            if (StringUtils.isNotBlank(request.getUpdatedBy())) {
                data.setUpdatedBy(request.getUpdatedBy());
            }
            data.setUpdatedAt(new Date());
            dataRepository.save(data);
            DataDto dataDto = modelMapper.map(data, DataDto.class);
            dataDto.setImageUrl(fileService.generateFileUrl(data.getImageFileId()));
            return makeSuccessResponse(dataDto);
        } catch (Exception e) {
            log.error("[UpdateData]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

    public BaseResponse<DataDto> getData(String id) {
        try {
            Optional<Data> dataOptional = dataRepository.findById(id);
            if (dataOptional.isEmpty()) {
                return makeBadRequestResponse("Data with given ID does not exist");
            }
            DataDto dataDto = modelMapper.map(dataOptional.get(), DataDto.class);
            dataDto.setImageUrl(fileService.generateFileUrl(dataOptional.get().getImageFileId()));
            return makeSuccessResponse(dataDto);
        } catch (Exception e) {
            log.error("[GetData]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

    public BaseResponse<String> deleteData(String id) {
        try {
            Optional<Data> dataOptional = dataRepository.findById(id);
            if (dataOptional.isEmpty()) {
                return makeBadRequestResponse("Data with given ID does not exist");
            }
            dataRepository.deleteById(id);
            return makeSuccessResponse("Data deleted successfully");
        } catch (Exception e) {
            log.error("[DeleteData]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

    public BaseResponse<Data> addLabeledCropImage(AddLabeledCropImageRequest request) {
        try {
            Optional<Data> dataOptional = dataRepository.findById(request.getDataId());
            if (dataOptional.isEmpty()) {
                return makeBadRequestResponse("Data with given ID does not exist");
            }
//            if (!labelRepository.existsById(request.getLabelId())) {
//                return makeBadRequestResponse("Label with given ID does not exist");
//            }
            Data data = dataOptional.get();
            data.setLabeledImages(request.getLabeledImages());
//            data.getLabeledImages().addAll(request.getLabeledImages());
            data.setStatus(request.getStatus());
            data.setLabeledIdClassification(request.getLabeledIdClassification());
            dataRepository.save(data);
            return makeSuccessResponse(data);
        } catch (Exception e) {
            log.error("[AddLabeledCropImage]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

    public BaseResponse<List<DataDto>> search(
            List<String> ids,
//            String name, String description, String projectId, String datasetId, String status,
            String name, String description, String projectId, String status,
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
            if (StringUtils.isNotBlank(status)) {
//                query.addCriteria(Criteria.where("status").is(status));
                query.addCriteria(Criteria.where("status").regex(status, "i"));
            }
            if (StringUtils.isNotBlank(createdBy)) {
                query.addCriteria(Criteria.where("createdBy").is(createdBy));
            }
            if (StringUtils.isNotBlank(updatedBy)) {
                query.addCriteria(Criteria.where("updatedBy").is(updatedBy));
            }
            query.addCriteria(generateCreatedDateCriteria(startDate, endDate));

            Pageable pageable = createPageable(page, size, sortBy, order);
            long total = mongoTemplate.count(query, Data.class);
            query.with(pageable);
            List<Data> dataList = mongoTemplate.find(query, Data.class);
            List<DataDto> dataDtoList = dataList.stream()
                    .map(data -> {
                        DataDto dataDto = modelMapper.map(data, DataDto.class);
                        dataDto.setImageUrl(fileService.generateFileUrl(data.getImageFileId()));
                        return dataDto;
                    })
                    .collect(Collectors.toList());
            return makeSuccessResponse(new PageImpl<>(dataDtoList, pageable, total));
        } catch (Exception e) {
            log.error("[SearchData]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

}
