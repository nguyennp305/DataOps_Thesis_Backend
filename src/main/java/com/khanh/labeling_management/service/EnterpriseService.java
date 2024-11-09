package com.khanh.labeling_management.service;

import com.google.gson.Gson;
import com.khanh.labeling_management.config.Constants;
import com.khanh.labeling_management.entity.information_management.Enterprise;
import com.khanh.labeling_management.model.BaseResponse;
import com.khanh.labeling_management.model.Message;
import com.khanh.labeling_management.model.input.EnterpriseInput;
import com.khanh.labeling_management.repository.EnterpriseRepository;
import com.khanh.labeling_management.utils.FileUtils;
import com.khanh.labeling_management.utils.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class EnterpriseService extends BaseService {

    private final EnterpriseRepository enterpriseRepository;
    private final MongoTemplate mongoTemplate;
    private final Gson gson;

    /*
        * Create Enterprise from input
     */
    private Enterprise createEnterprise(EnterpriseInput input,Enterprise enterprise, boolean isEdit) {
        if (ObjectUtils.isEmpty(enterprise)) enterprise = new Enterprise();
        enterprise.setName(input.getName());
        enterprise.setEmail(input.getEmail());
        enterprise.setDescription(input.getDescription());
        if (isEdit) enterprise.setUpdatedAt(new Date());
        else {
            enterprise.setCreatedAt(new Date());
            enterprise.setUpdatedAt(enterprise.getCreatedAt());
        }
        return enterprise;
    }

    /*
        * Add Enterprise
     */
    public BaseResponse<Enterprise> addEnterprise(EnterpriseInput input) {
        BaseResponse<Enterprise> baseResponse = new BaseResponse<>();
        try {
            if (StringUtils.isBlank(input.getName())) {
                return makeBadRequestResponse(Constants.NULL_NAME);
            }

            if (enterpriseRepository.existsByName(input.getName())) {
                return makeBadRequestResponse(Constants.EXITS_ENTERPRISE);
            }

            Enterprise enterprise = createEnterprise(input, null, false);
            enterpriseRepository.save(enterprise);
            baseResponse.success(enterprise);
        } catch (Exception exception) {
            log.error("Error: ", exception);
            baseResponse.internalServerError(exception);
        }
        return baseResponse;
    }

    /*
        * Update Enterprise
     */
    public BaseResponse<Enterprise> updateEnterprise(EnterpriseInput input) {
        BaseResponse<Enterprise> baseResponse = new BaseResponse<>();
        try {
            Enterprise enterprise = null;

            if (StringUtils.isBlank(input.getId())) {
                return makeBadRequestResponse(Constants.NULL_ID);
            }

            Optional<Enterprise> enterpriseOptional = enterpriseRepository.findById(input.getId());
            if (enterpriseOptional.isPresent()) {
                enterprise = enterpriseOptional.get();
            } else {
                return makeBadRequestResponse(Constants.NULL_ENTERPRISE_ID);
            }

            String oldName = enterprise.getName().trim();
            if (!oldName.equals(input.getName().trim()) && enterpriseRepository.existsByName(input.getName())) {
                return makeBadRequestResponse(Constants.EXITS_ENTERPRISE);
            }

            enterprise = createEnterprise(input, enterprise, true);
            enterpriseRepository.save(enterprise);
            baseResponse.success(enterprise);
        } catch (Exception exception) {
            log.error("Error: ", exception);
            baseResponse.internalServerError(exception);
        }
        return baseResponse;
    }

    /*
     * Remove Enterprise
     */
    public BaseResponse<String> removeEnterprise(String id) {
        BaseResponse<String> baseResponse = new BaseResponse<>();

        try {
            Optional<Enterprise> enterpriseOptional = enterpriseRepository.findById(id);
            if (enterpriseOptional.isEmpty()) {
                return makeBadRequestResponse(Constants.INVALID_ID);
            }


            Enterprise enterprise = enterpriseOptional.get();
            enterprise.setDeleted(true);
            enterpriseRepository.save(enterprise);
            baseResponse.success(Constants.SUCCESS);
        } catch (Exception ex) {
            baseResponse.internalServerError(ex);
            log.error("removeComment", ex);
        }
        return baseResponse;
    }

    /*
        * Search Enterprise
        * Create query search all enterprise
     */
    public BaseResponse<List<Enterprise>> searchAll(
            List<String> ids,
            String word, Date startDate, Date endDate,
            Integer page, Integer size, String sortBy, Sort.Direction order) {
        log.info("Search All Enterprise: {}", word);
        BaseResponse<List<Enterprise>> baseResponse = new BaseResponse<>();
        try {
            Pageable pageable = createPageable(page, size, sortBy, order);

            Query query = new Query();
            if (ids != null) {
                query.addCriteria(Criteria.where("id").in(ids));
            }
            if (StringUtils.isNotBlank(word)) {
                query.addCriteria(Criteria.where("name").regex(word, "i"));
            }
            if (startDate != null && endDate != null) {
                query.addCriteria(Criteria.where("createdAt")
                        .gte(startDate)
                        .lte(endDate)
                );
            } else if (startDate != null) {
                query.addCriteria(Criteria.where("createdAt").gte(startDate));
            } else if (endDate != null) {
                query.addCriteria(Criteria.where("createdAt").lte(endDate));
            }
            query.addCriteria(Criteria.where("deleted").is(false));
            query.with(Sort.by(Sort.Direction.DESC, "createdAt"));

            long total = mongoTemplate.count(query, Enterprise.class);
            query.with(pageable);
            List<Enterprise> enterprisePage = mongoTemplate.find(query, Enterprise.class);

            baseResponse.success(enterprisePage);
            baseResponse.setQuantity(enterprisePage.size());
            baseResponse.setOffset(pageable.getPageNumber());
            baseResponse.setLimit(pageable.getPageSize() * pageable.getPageNumber());
            baseResponse.setTotal(total);
        } catch (Exception ex) {
            log.error("Search All Enterprise", ex);
            baseResponse.internalServerError(ex);
        }
        return baseResponse;
    }

    public BaseResponse<List<Enterprise>> searchAllWithoutPage(String word,String startDate,String endDate) {
        BaseResponse<List<Enterprise>> baseResponse = new BaseResponse<>();
        try {
            if (!Function.checkValidText(word)) word = null;

            Query query = createQuerySearchAll(word, startDate, endDate);

            List<Enterprise> enterprisePage = mongoTemplate.find(query, Enterprise.class);

            baseResponse.success(enterprisePage);
        } catch (Exception ex) {
            log.error("Search All Enterprise", ex);
            baseResponse.internalServerError(ex);
        }
        return baseResponse;
    }

    public Query createQuerySearchAll(String word, String startDate, String endDate) {
        Query query = new Query();
        if (Function.checkValidText(word)) {
            query.addCriteria(Criteria.where("name").regex(word, "i"));
        }
        if (Function.checkValidText(startDate) || Function.checkValidText(endDate)) {
            query.addCriteria(Criteria.where("createdAt")
                    .gte(Objects.requireNonNull(Function.parseDate(startDate)))
                    .lte(Objects.requireNonNull(Function.parseDate(endDate))));
        }
        query.addCriteria(Criteria.where("deleted").is(false));
        query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
        return query;
    }

    /*
        * Import Enterprise
     */
    public BaseResponse<String> importEntityFromFile(MultipartFile file) {
        Workbook workbook;
        BaseResponse<String> baseResponse = new BaseResponse<>();
        try {
            log.info("import Enterprise {}", file.getOriginalFilename());
            InputStream inputStream = file.getInputStream();
            workbook = FileUtils.getWorkbook(inputStream, Objects.requireNonNull(file.getOriginalFilename()));
            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowIterator  = sheet.rowIterator();
            List<EnterpriseInput> enterpriseInputs = new LinkedList<>();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row.getRowNum() == 0) {
                    continue;
                }

                EnterpriseInput enterpriseInput = new EnterpriseInput();

                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    switch (cell.getColumnIndex()) {
                        case 1:
                            enterpriseInput.setName(cell.getStringCellValue().trim());
                            break;
                        case 2:
                            enterpriseInput.setEmail(cell.getStringCellValue().trim());
                            break;
                        case 3:
                            enterpriseInput.setDescription(cell.getStringCellValue().trim());
                            break;
                        default:
                            break;
                    }
                }
                if (enterpriseInput.getName() != null && enterpriseInput.getDescription() != null) {
                    enterpriseInputs.add(enterpriseInput);
                }
            }

            for (EnterpriseInput entityTypeInput : enterpriseInputs) {
                addEnterprise(entityTypeInput);
            }

            baseResponse.success("Import Enterprise Success !");
            workbook.close();
        } catch (Exception exp) {
            exp.printStackTrace();
            baseResponse.setData("Import Enterprise Fail !");
            baseResponse.internalServerError(exp);
        }
        return baseResponse;
    }

    /*
        * Export Enterprise
     */
    public ByteArrayInputStream exportEnterprise(String word, String startDate, String endDate) {
        try {

            if (!Function.checkValidText(word)) word = null;

            BaseResponse<List<Enterprise>> enterpriseBaseResponse = searchAllWithoutPage(word, startDate, endDate);
            List<Enterprise> enterprises = enterpriseBaseResponse.getData();
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Enterprise");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("STT");
            header.createCell(1).setCellValue("Name");
            header.createCell(2).setCellValue("Email");
            header.createCell(3).setCellValue("Description");

            int rowNum = 1;
            for (Enterprise enterprise : enterprises) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rowNum - 1);
                row.createCell(1).setCellValue(enterprise.getName());
                row.createCell(2).setCellValue(enterprise.getEmail());
                row.createCell(3).setCellValue(enterprise.getDescription());
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
            log.info("Export Sentence Topic Done");
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (Exception exp) {
            exp.printStackTrace();
            return null;
        }
    }
}
