package com.mymemor.mymemor.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

public class SearchResult {
    public SearchResult(User user) {
        this.name = user.getName();
        this.username = user.getAccount().getUsername();
        this.profilePicURL = user.getProfilePicURL();
    }

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    @JsonProperty("profile_pic_url")
    private String profilePicURL;

}