package com.mymemor.mymemor.repository;

import com.mymemor.mymemor.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account,Long> {
}
