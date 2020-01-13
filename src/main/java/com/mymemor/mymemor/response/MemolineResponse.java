package com.mymemor.mymemor.response;

import com.mymemor.mymemor.model.Memory;
import lombok.Getter;

import java.util.List;

public class MemolineResponse extends StringResponse {
    @Getter
    List<Memory> memories;

    public void setMemories(List<Memory> memories) {
        this.memories = memories;
        setStatus("success");
    }
}