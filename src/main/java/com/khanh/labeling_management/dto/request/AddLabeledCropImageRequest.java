package com.khanh.labeling_management.dto.request;

import com.khanh.labeling_management.entity.data_management.labeling_management.CropBoxData;
import com.khanh.labeling_management.entity.data_management.labeling_management.CropData;
import com.khanh.labeling_management.entity.data_management.labeling_management.LabelCropMulti;
import com.khanh.labeling_management.entity.data_management.labeling_management.LabeledImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddLabeledCropImageRequest {
    @NotNull(message = "DataId is required")
    @NotEmpty(message = "DataId is required")
    private String dataId;

    private List<LabeledImage> labeledImages = new ArrayList<>();
    private List<String> labeledIdClassification = new ArrayList<>();
    private String status;
}
