package com.mymemor.mymemor.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "memories")
public class Memory extends Auditable {

    @Getter
    @Setter
    @NotBlank
    private String topic;

    @Getter
    @Setter
    private String content;

    @Getter
    @Setter
    private String location;

    @Getter
    @Setter
    @ElementCollection
    private Set<String> photos = new HashSet<>();

    @Getter
    @Setter
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Getter
    @Setter
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @Getter
    @Setter
    @NotNull
    @ManyToOne
    private User creator;

    @Getter
    @Setter
    @ManyToMany
    @JsonManagedReference
    private Set<User> users = new HashSet<>();

}