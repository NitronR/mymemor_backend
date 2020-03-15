package com.mymemor.mymemor.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormResponse {

    @Getter
    @Setter
    private String status;

    @Getter
    private Map<String, List<String>> errors = new HashMap<>();

    public void setErrors(Map<String, List<String>> errors) {
        setStatus("error");
        this.errors = errors;
    }

    public void addError(String fieldName, String error) {
        if (errors.containsKey(fieldName)) {
            errors.get(fieldName).add(error);
        } else {
            List<String> errorMessages = new ArrayList<>();
            errorMessages.add(error);
            errors.put(fieldName, errorMessages);
        }
        updateStatus();
    }

    private void updateStatus() {
        if (!errors.isEmpty()) setStatus("error");
    }
}