package com.khanh.labeling_management.utils;

import java.util.Objects;

public class ObjectUtils extends org.springframework.util.ObjectUtils {

    public static boolean inOptions(Object target, Object... options) {
        for (Object option : options) {
            if (Objects.equals(target, option)) {
                return true;
            }
        }
        return false;
    }

}
