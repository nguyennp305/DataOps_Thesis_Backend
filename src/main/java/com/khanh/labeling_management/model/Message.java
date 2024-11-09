package com.khanh.labeling_management.model;

import com.khanh.labeling_management.config.Constants;
import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
public class Message {
    private int status;
    private String message;

    public static Message successMessage() {
        return new Message(Constants.SUCCESS_STATUS, Constants.SUCCESS);
    }

    public static Message internalErrorMessage() {
        return new Message(Constants.INTERNAL_SERVER_CODE, Constants.INTERNAL_SERVER);
    }

    public static Message notFoundErrorMessage() {
        return new Message(Constants.NOT_FOUND_CODE, Constants.NOT_FOUND);
    }

    public static Message checkId(String id) {
        if (id == null) {
            return new Message(Constants.NOT_FOUND_CODE, Constants.NULL_ID);
        }
        return null;
    }

}