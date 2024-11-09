package com.khanh.labeling_management.entity.data_management.labeling_management;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabelCropMulti {
    private String labelId;
    private Double exactRatio;
}
