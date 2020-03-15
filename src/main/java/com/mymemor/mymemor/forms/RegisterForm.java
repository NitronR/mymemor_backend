package com.mymemor.mymemor.forms;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mymemor.mymemor.ValidationConstants;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class RegisterForm {
    @NotBlank(message = "Name must not be blank.")
    @Length(max = ValidationConstants.MAX_LEN_NAME)
    public String name;

    // TODO validate username using regex
    @NotBlank
    public String username;

    @NotBlank
    @Email
    public String email;

    // TODO validate password
    @NotBlank
    public String password;

    @Length(max = ValidationConstants.MAX_LEN_HOMETOWN)
    @JsonProperty("hometown")
    public String hometown;

    @JsonProperty("college")
    @Length(max = ValidationConstants.MAX_LEN_COLLEGE)
    public String collegeName;

    @JsonProperty("school")
    @Length(max = ValidationConstants.MAX_LEN_SCHOOL)
    public String schoolName;

    @JsonProperty("current_city")
    @Length(max = ValidationConstants.MAX_LEN_CURRENT_CITY)
    public String currentCity;

    @NotBlank
    @URL(message = "Must be a valid URL.")
    @JsonProperty("profile_pic_url")
    public String profilePicURL;
}
