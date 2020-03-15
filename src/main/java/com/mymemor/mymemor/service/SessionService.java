package com.mymemor.mymemor.service;

import com.mymemor.mymemor.exceptions.EntityDoesNotExist;
import com.mymemor.mymemor.exceptions.NotAuthenticatedException;
import com.mymemor.mymemor.model.User;
import com.mymemor.mymemor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Service
public class SessionService {
    @Autowired
    private UserRepository userRepository;

    public User getSessionUser(HttpSession session) throws NotAuthenticatedException, EntityDoesNotExist {
        Long userId = (Long) session.getAttribute("user_id");

        if (userId == null) {
            throw new NotAuthenticatedException("You are not authenticated, please login first.");
        }

        Optional<User> sessionUser = userRepository.findById(userId);

        if (sessionUser.isPresent()) {
            return sessionUser.get();
        }

        throw new EntityDoesNotExist("No user found.");
    }
}
