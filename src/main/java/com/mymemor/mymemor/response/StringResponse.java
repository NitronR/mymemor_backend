package com.mymemor.mymemor.response;

import lombok.Getter;
import lombok.Setter;

public class StringResponse {
    @Getter
    @Setter
    private String status;

    @Getter
    private String error;

    public void setError(String error) {
        this.error = error;
        this.status = "success";
    }
}
