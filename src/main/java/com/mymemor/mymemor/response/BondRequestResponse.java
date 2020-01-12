package com.mymemor.mymemor.response;

import com.mymemor.mymemor.model.BondRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class BondRequestResponse extends StringResponse {
    @Getter
    @Setter
    private List<BondRequest> bondRequests;
}
