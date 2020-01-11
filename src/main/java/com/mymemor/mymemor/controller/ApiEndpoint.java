package com.mymemor.mymemor.controller;

import com.mymemor.mymemor.FormResponse;
import com.mymemor.mymemor.Utils;
import com.mymemor.mymemor.model.Account;
import com.mymemor.mymemor.model.User;
import com.mymemor.mymemor.repository.AccountRepository;
import com.mymemor.mymemor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO : Validation , collegeName

@RestController
public class ApiEndpoint {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;

    @PostMapping("/register")
    public FormResponse registeruser(@RequestParam("name") @Valid String name,
                                     @RequestParam("username") @Valid String username,
                                     @RequestParam("email") @Valid String email,
                                     @RequestParam("password") @Valid String password,
                                     @RequestParam("hometown") @Valid String homeTown,
                                     @RequestParam("college_name") @Valid String collegeName,
                                     @RequestParam("school_name") @Valid String schoolName,
                                     @RequestParam("current_City") @Valid String currentCity,
                                     @RequestParam("profile_pic_url") @Valid String profilePicURL) {

        FormResponse form = new FormResponse();
        Map<String, List<String>> error = new HashMap<>();
        List<String> list = new ArrayList<>();
        form.setStatus("success");
        Account account = accountRepository.findByUsername(username);

        if(account!=null)
        {

            form.setStatus("Error");
            list.add("username already exiest");
            error.put("username",list);
            form.setErrorList(error);
        }
        else {
            User user = new User();
            account = new Account();
            account.setUsername(username);
            account.setEncPassword(Utils.encryptPassword(password));
            account.setEmail(email);

            user.setName(name);
            user.setSchoolName(schoolName);
            user.setCurrentCity(currentCity);
            user.setProfilePicURL(profilePicURL);
            user.setHometown(homeTown);
            user.setAccount(account);
            account.setUser(user);
            userRepository.save(user);
        }
        return form;
    }

    @PostMapping("/login")
    public FormResponse loginUser(@RequestParam("username") @Valid String username,
                                  @RequestParam("password") @Valid String password) {
        FormResponse form = new FormResponse();
        Map<String, List<String>> error = new HashMap<>();
        form.setStatus("success");

        Account account =(Account)accountRepository.findByUsername(username);

        if (account == null) {
            List<String> list = new ArrayList<>();
            form.setStatus("error");
            list.add("username not exiest");
            error.put("username", list);
            form.setErrorList(error);
        } else {
            account = accountRepository.findByUsername(username);
            if (!account.getEncPassword().equals(Utils.encryptPassword(password))) {
                List<String> list = new ArrayList<>();
                list.add("paaword not match");
                error.put("password", list);
                form.setErrorList(error);
            }
        }
        return form;
    }
}