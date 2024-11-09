package com.khanh.labeling_management.entity.data_management.labeling_management;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CropData {
    private Double x;
    private Double y;
    private Double width;
    private Double height;
    private Double rotate;
    private Double scaleX;
    private Double scaleY;
}
