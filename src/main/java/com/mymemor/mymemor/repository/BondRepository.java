package com.mymemor.mymemor.repository;

import com.mymemor.mymemor.model.BondRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BondRepository extends JpaRepository<BondRequest,Long> {
}
