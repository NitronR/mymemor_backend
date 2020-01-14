package com.mymemor.mymemor.repository;

import com.mymemor.mymemor.model.BondRequest;
import com.mymemor.mymemor.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BondRepository extends JpaRepository<BondRequest, Long> {
    boolean existsBySenderAndReceiver(User sender, User receiver);
}
