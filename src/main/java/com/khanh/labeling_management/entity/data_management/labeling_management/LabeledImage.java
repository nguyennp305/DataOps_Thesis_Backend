package com.khanh.labeling_management.entity.data_management.labeling_management;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.khanh.labeling_management.entity.data_management.labeling_management.LabelCropMulti;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabeledImage {
    private CropData cropData;
    private CropBoxData cropBoxData;
    private String cropImg;
//    private String labelGroupId;
//    private List<String> labelId = new ArrayList<>();
    private List<LabelCropMulti> labelCropMulti = new ArrayList<>();
    private String description;
}