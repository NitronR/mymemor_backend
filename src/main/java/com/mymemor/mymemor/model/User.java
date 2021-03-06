package com.mymemor.mymemor.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
public class User extends Auditable {

    @Getter
    @Setter
    @NotNull
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Account account;

    @Getter
    @Setter
    @NotBlank
    private String name;

    @Getter
    @Setter
    @URL
    @NotBlank
    @JsonProperty("profile_pic_url")
    private String profilePicURL;

    @Getter
    @Setter
    @JsonProperty("school")
    private String schoolName;

    @Getter
    @Setter
    @JsonProperty("current_city")
    private String currentCity;

    @Getter
    @Setter
    private String hometown;

    @Getter
    @Setter
    @JsonProperty("college")
    private String collegeName;

    @Getter
    @Setter
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Memory> createdMemories = new ArrayList<>();

    @Getter
    @Setter
    @ManyToMany(mappedBy = "users")
    @JsonIgnore
    private List<Memory> memories = new ArrayList<>();

    @Getter
    @Setter
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<BondRequest> sentRequests = new HashSet<>();

    @Getter
    @Setter
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<BondRequest> receivedRequests = new HashSet<>();

    @Getter
    @Setter
    @ManyToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<User> myPeople = new HashSet<>();

    public String getUsername() {
        return getAccount().getUsername();
    }

    public User(){}

    private User(Builder builder) {
        this.name = builder.name;
        this.profilePicURL = builder.profilePicURL;
        this.schoolName = builder.schoolName;
        this.currentCity = builder.currentCity;
        this.hometown = builder.hometown;
    }

    public static final class Builder {
        private @NotBlank String name;
        private @URL @NotBlank String profilePicURL;
        private String schoolName;
        private String currentCity;
        private String hometown;

        public Builder() {
        }

        public User build() {
            return new User(this);
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder profilePicURL(String profilePicURL) {
            this.profilePicURL = profilePicURL;
            return this;
        }

        public Builder schoolName(String schoolName) {
            this.schoolName = schoolName;
            return this;
        }

        public Builder currentCity(String currentCity) {
            this.currentCity = currentCity;
            return this;
        }

        public Builder hometown(String hometown) {
            this.hometown = hometown;
            return this;
        }
    }

    // Checks if given user is in my people set
    public boolean isMyPeople(User user) {
        return myPeople.contains(user);
    }
}