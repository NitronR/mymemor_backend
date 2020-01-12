package com.mymemor.mymemor.forms;

import javax.validation.constraints.NotBlank;

public class LoginForm {
    @NotBlank
    public String username_email;
    @NotBlank
    public String password;
}
