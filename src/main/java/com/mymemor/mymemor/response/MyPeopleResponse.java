package com.mymemor.mymemor.response;

import com.mymemor.mymemor.model.User;
import lombok.Getter;

import java.util.Set;

public class MyPeopleResponse extends StringResponse {
    @Getter
    private Set<User> people;

    public void setPeople(Set<User> people) {
        this.people = people;
        setStatus("success");
    }
}
