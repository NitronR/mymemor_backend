package com.mymemor.mymemor.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "accounts")
public class Account extends Auditable {

    @Getter
    @Setter
    @NotBlank
    @Email
    @JsonIgnore
    private String email;

    @Getter
    @Setter
    @NotBlank
    private String username;

    @Getter
    @Setter
    @NotBlank
    @JsonIgnore
    private String encPassword;

    @Getter
    @Setter
    @NotNull
    @OneToOne
    @JsonBackReference
    private User user;

    private Account(Builder builder) {
        this.email = builder.email;
        this.username = builder.username;
        this.encPassword = builder.encPassword;
    }

    public Account(){}

    public static final class Builder {
        private @NotBlank @Email String email;
        private @NotBlank String username;
        private @NotBlank String encPassword;

        public Builder() {
        }

        public Account build() {
            return new Account(this);
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder encPassword(String encPassword) {
            this.encPassword = encPassword;
            return this;
        }
    }
}
