package com.mymemor.mymemor.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="bond_requests")
public class BondRequest extends Auditable {

    @Getter
    @Setter
    @NotNull
    private BondRequestStatus bondRequestStatus = BondRequestStatus.PENDING;

    @Getter
    @Setter
    @ManyToOne
    @NotNull
    private User from;

    @Getter
    @Setter
    @ManyToOne
    @NotNull
    private User to;

}