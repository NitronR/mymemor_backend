package com.mymemor.mymemor.controller;

import com.mymemor.mymemor.Utils;
import com.mymemor.mymemor.model.Account;
import com.mymemor.mymemor.model.User;
import com.mymemor.mymemor.repository.AccountRepository;
import com.mymemor.mymemor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dev")
public class PopulateDB {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;

    // TODO accept parameters and add user
    @GetMapping("/add-user")
    public void addDummyUser() {
        accountRepository.deleteAll();
        for (int i = 0; i < 3; i++) {
            User luffy = new User();

            luffy.setName("Monkey D Luffy"+i);
            luffy.setProfilePicURL("https://i.pinimg.com/originals/90/a7/f6/90a7f67864acea71fb5ffed6aa6298cb.jpg");
            luffy.setSchoolName("xyxzz"+i);
            luffy.setCurrentCity("Kolkata"+i);
            luffy.setHometown("syzff"+i);

            Account dummy = new Account();
            dummy.setEmail("xyz@gmail.com"+i);
            dummy.setUsername("dummy"+i);
            dummy.setEncPassword(Utils.encryptPassword("dummypass" + i));


            luffy.setAccount(dummy);
            dummy.setUser(luffy);

            userRepository.save(luffy);
        }
    }

    // TODO populate sender JSON file
}

