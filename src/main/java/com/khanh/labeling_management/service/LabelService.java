package com.khanh.labeling_management.service;

import com.khanh.labeling_management.dto.request.CreateLabelRequest;
import com.khanh.labeling_management.dto.request.UpdateLabelRequest;
import com.khanh.labeling_management.entity.data_management.labeling_management.Label;
import com.khanh.labeling_management.model.BaseResponse;
import com.khanh.labeling_management.repository.DatasetRepository;
import com.khanh.labeling_management.repository.LabelRepository;
import com.khanh.labeling_management.repository.ProjectRepository;
import com.khanh.labeling_management.repository.UserRepository;
import com.khanh.labeling_management.utils.ExcelUtils;
import com.khanh.labeling_management.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LabelService extends BaseService {
    private final LabelRepository labelRepository;
    private final DatasetRepository datasetRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    public BaseResponse<String> importByExcel(MultipartFile file) throws IOException {
        if (!ObjectUtils.inOptions(FilenameUtils.getExtension(file.getOriginalFilename()), "xls", "xlsx")) {
            return makeBadRequestResponse("Unsupported file format");
        }
        File temp = new File(FileUtils.getTempDirectory(),
                UUID.randomUUID().toString() + "/" + file.getOriginalFilename());
        temp.getParentFile().mkdirs();
        file.transferTo(temp);
        List<Label> labels = readExcel(temp);

        List<Label> validLabels = new ArrayList<>();
        for (Label label : labels) {
            // Tìm item có name trùng
            Label existingLabel = labelRepository.findByName(label.getName());
            if (existingLabel != null) {
                // Nếu item có cùng name và projectId, bỏ qua việc tạo mới
                if (existingLabel.getProjectId().equals(label.getProjectId())) {
                    continue; // Bỏ qua label này vì đã tồn tại
                }
            }
            label.setCreatedAt(new Date());
            label.setUpdatedAt(new Date());
            validLabels.add(label);
        }

        if (validLabels.isEmpty()) {
            return makeBadRequestResponse("No valid labels to create. All labels already exist.");
        }
//        for (Label label : labels) {
//            label.setCreatedAt(new Date());
//            label.setUpdatedAt(new Date());
//        }
//        labelRepository.saveAll(labels);
//        return makeSuccessResponse(String.format("Created %d labels", labels.size()));
//    }

        labelRepository.saveAll(validLabels);
        return makeSuccessResponse(String.format("Created %d labels", validLabels.size()));
    }

    public List<Label> readExcel(File file) throws IOException {
        List<Label> labels = new ArrayList<>();

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
            Label label = new Label();
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
                        label.setProjectId(cellValue.toString());
                        break;
                    case 1:
                        label.setName(cellValue.toString());
                        break;
                    case 2:
                        label.setDescription(cellValue.toString());
                        break;
                    case 3:
                        label.setCreatedBy(cellValue.toString());
                        break;
                    default:
                        break;
                }

            }
            labels.add(label);
        }

        workbook.close();
        inputStream.close();

        return labels;
    }

    public BaseResponse<Label> createLabel(@NotNull CreateLabelRequest request) {
        try {
            if (StringUtils.isNotBlank(request.getProjectId()) && !projectRepository.existsById(request.getProjectId())) {
                return makeBadRequestResponse("Project with given ID does not exist");
            }
//            if (StringUtils.isNotBlank(request.getDatasetId()) && !datasetRepository.existsById(request.getDatasetId())) {
//                return makeBadRequestResponse("Dataset with given ID does not exist");
//            }
            if (StringUtils.isNotBlank(request.getCreatedBy()) && !userRepository.existsById(request.getCreatedBy())) {
                return makeBadRequestResponse("User with given ID does not exist");
            }
//            if (labelRepository.existsByName(request.getName())) {
//                return makeBadRequestResponse("Label with given name already exists");
//            }

            // Tìm item có name trùng
            Label existingLabel = labelRepository.findByName(request.getName());
            if (labelRepository.existsByName(request.getName())) {
                if (existingLabel.getProjectId().equals(request.getProjectId())) {
                    return makeBadRequestResponse("Label with given name and project ID already exists");
                }
            }

            Label label = new Label();
            label.setName(request.getName());
            label.setDescription(request.getDescription());
            label.setProjectId(request.getProjectId());
//            label.setDatasetId(request.getDatasetId());
            label.setCreatedBy(request.getCreatedBy());
            label.setUpdatedBy(request.getCreatedBy());
            label.setCreatedAt(new Date());
            label.setUpdatedAt(new Date());
            labelRepository.save(label);
            return makeSuccessResponse(label);
        } catch (Exception e) {
            log.error("[CreateLabel]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

    public BaseResponse<Label> updateLabel(@NotNull UpdateLabelRequest request) {
        try {
            Optional<Label> labelOptional = labelRepository.findById(request.getId());
            if (labelOptional.isEmpty()) {
                return makeBadRequestResponse("Label with given ID does not exist");
            }
            if (StringUtils.isNotBlank(request.getProjectId()) && !projectRepository.existsById(request.getProjectId())) {
                return makeBadRequestResponse("Project with given ID does not exist");
            }
//            if (StringUtils.isNotBlank(request.getDatasetId()) && !datasetRepository.existsById(request.getDatasetId())) {
//                return makeBadRequestResponse("Dataset with given ID does not exist");
//            }
            if (StringUtils.isNotBlank(request.getUpdatedBy()) && !userRepository.existsById(request.getUpdatedBy())) {
                return makeBadRequestResponse("User with given ID does not exist");
            }
            Label label = labelOptional.get();
            if (StringUtils.isNotBlank(request.getName())
                    && !label.getName().equals(request.getName())
                    && labelRepository.existsByName(request.getName())) {
                return makeBadRequestResponse("Label with given name already exists");
            }
            if (StringUtils.isNotBlank(request.getName())) {
                label.setName(request.getName());
            }
            if (StringUtils.isNotBlank(request.getDescription())) {
                label.setDescription(request.getDescription());
            }
            if (StringUtils.isNotBlank(request.getProjectId())) {
                label.setProjectId(request.getProjectId());
            }
//            if (StringUtils.isNotBlank(request.getDatasetId())) {
//                label.setDatasetId(request.getDatasetId());
//            }
            if (StringUtils.isNotBlank(request.getUpdatedBy())) {
                label.setUpdatedBy(request.getUpdatedBy());
            }
            label.setUpdatedAt(new Date());
            labelRepository.save(label);
            return makeSuccessResponse(label);
        } catch (Exception e) {
            log.error("[UpdateLabel]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

    public BaseResponse<Label> getLabel(@NotNull String id) {
        try {
            Optional<Label> labelOptional = labelRepository.findById(id);
            if (labelOptional.isEmpty()) {
                return makeBadRequestResponse("Label with given ID does not exist");
            }
            return makeSuccessResponse(labelOptional.get());
        } catch (Exception e) {
            log.error("[GetLabel]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

    public BaseResponse<String> deleteLabel(@NotNull String id) {
        try {
            if (!labelRepository.existsById(id)) {
                return makeBadRequestResponse("Label with given ID does not exist");
            }
            labelRepository.deleteById(id);
            return makeSuccessResponse("Label deleted successfully");
        } catch (Exception e) {
            log.error("[DeleteLabel]", e);
            return makeInternalServerErrorResponse(e);
        }
    }

    public BaseResponse<List<Label>> search(
            List<String> ids,
            String name, String description, String projectId,
            String createdBy, String updatedBy,
//            String datasetId, String createdBy, String updatedBy,
            Date startDate, Date endDate,
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
            if (StringUtils.isNotBlank(createdBy)) {
                query.addCriteria(Criteria.where("createdBy").is(createdBy));
            }
            if (StringUtils.isNotBlank(updatedBy)) {
                query.addCriteria(Criteria.where("updatedBy").is(updatedBy));
            }
            query.addCriteria(generateCreatedDateCriteria(startDate, endDate));
            long total = mongoTemplate.count(query, Label.class);
            Pageable pageable = createPageable(page, size, sortBy, order);
            query.with(pageable);
            List<Label> labels = mongoTemplate.find(query, Label.class);
            return makeSuccessResponse(new PageImpl<>(labels, pageable, total));
        } catch (Exception e) {
            log.error("[SearchLabel]", e);
            return makeInternalServerErrorResponse(e);
        }
    }


}
