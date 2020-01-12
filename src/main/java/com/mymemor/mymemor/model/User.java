package com.mymemor.mymemor.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Account account;

    @Getter
    @Setter
    @NotBlank
    private String name;

    @Getter
    @Setter
    @URL
    @NotBlank
    private String profilePicURL;

    @Getter
    @Setter
    private String schoolName;

    @Getter
    @Setter
    private String currentCity;

    @Getter
    @Setter
    private String hometown;

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
    @OneToMany(mappedBy = "from", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<BondRequest> sentRequests =  new HashSet<>();

    @Getter
    @Setter
    @OneToMany(mappedBy = "to", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<BondRequest> receivedRequests = new HashSet<>();

    @Getter
    @Setter
    @ManyToMany
    @JsonIgnore
    private Set<User> myPeople = new HashSet<>();

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
}