package com.khanh.labeling_management.dto.request;

import com.khanh.labeling_management.entity.data_management.labeling_management.LabelCropMulti;
import com.khanh.labeling_management.entity.data_management.labeling_management.LabeledImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDataRequest {
    @NotNull(message = "Name is required")
    @NotEmpty(message = "Name is required")
    private String name;

    private String projectId;
//    private String datasetId;
    private String status;
    private String description;
    private List<String> labeledIdClassification = new ArrayList<>();
    private String createdBy;
    private List<LabeledImage> labeledImages = new ArrayList<>();

}
