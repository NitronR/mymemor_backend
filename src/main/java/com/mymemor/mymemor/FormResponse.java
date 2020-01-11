package com.mymemor.mymemor;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormResponse {

    @Getter
    @Setter
    private String status;


    public String getStatus() {
        return status;
    }


    @Getter
    @Setter
    private Map<String, List<String>> errorList = new HashMap<>();

    public void setStatus(String status) {
        this.status = status;
    }


    public Map<String, List<String>> getErrorList() {
        return errorList;
    }


    public void setErrorList(Map<String, List<String>> errorList) {
        this.errorList = errorList;
    }
}