package com.mymemor.mymemor.forms;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

public class RegisterForm {
    @NotBlank(message = "Name must not be blank.")
    public String name;

    @NotBlank
    public String username;

    @NotBlank
    public String email;

    @NotBlank
    public String password;

    @JsonProperty("hometown")
    public String homeTown;

    @JsonProperty("college")
    public String collegeName;

    @JsonProperty("school")
    public String schoolName;

    @JsonProperty("current_city")
    public String currentCity;

    @NotBlank
    @JsonProperty("profile_pic_url")
    public String profilePicURL;
}
