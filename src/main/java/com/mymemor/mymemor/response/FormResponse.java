package com.mymemor.mymemor.response;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormResponse {

    @Getter
    @Setter
    private String status;

    @Getter
    @Setter
    private Map<String, List<String>> errorList = new HashMap<>();
}