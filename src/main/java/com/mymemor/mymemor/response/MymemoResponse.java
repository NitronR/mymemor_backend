package com.mymemor.mymemor.response;

import com.mymemor.mymemor.model.Memory;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class MymemoResponse extends StringResponse {
    @Getter
    @Setter
    private List<Memory> memories;
}