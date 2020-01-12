package com.mymemor.mymemor.response;

import com.mymemor.mymemor.model.BondRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

public class BondRequestResponse extends StringResponse {
    @Getter
    @Setter
    private Set<BondRequest> bondRequests;
}
