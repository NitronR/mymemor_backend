package com.mymemor.mymemor.response;

import com.mymemor.mymemor.model.User;
import lombok.Getter;
import lombok.Setter;

public class ProfileResponse extends StringResponse {

    @Getter
    @Setter
    User user;

    @Getter
    @Setter
    private boolean isBonded = false;

    @Getter
    @Setter
    private boolean isRequested = false;

    @Getter
    @Setter
    private boolean some = true;
}