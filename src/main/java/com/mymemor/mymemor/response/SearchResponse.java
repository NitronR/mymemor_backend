package com.mymemor.mymemor.response;

import com.mymemor.mymemor.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class SearchResponse extends FormResponse{
    @Getter
    @Setter
    private List<User> user;
}