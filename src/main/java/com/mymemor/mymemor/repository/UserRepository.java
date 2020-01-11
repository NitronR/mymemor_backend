package com.mymemor.mymemor.repository;

import com.mymemor.mymemor.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepository extends PagingAndSortingRepository<User,Long> {
}

