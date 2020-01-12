package com.mymemor.mymemor.response;

import com.mymemor.mymemor.model.User;
import lombok.Getter;
import lombok.Setter;

public class ProfileResponse extends FormResponse {

    @Getter
    @Setter
    User user;

    @Getter
    @Setter
    boolean isBonded = false;

    @Getter
    @Setter
    boolean isRequested = false;
}