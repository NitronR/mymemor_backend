package com.mymemor.mymemor.controller;

import com.mymemor.mymemor.Utils;
import com.mymemor.mymemor.exceptions.RegistrationError;
import com.mymemor.mymemor.model.Account;
import com.mymemor.mymemor.model.User;
import com.mymemor.mymemor.repository.AccountRepository;
import com.mymemor.mymemor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dev")
public class PopulateDB {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/add-user/{name}/{usrName}/{email}/{paswrd}/{url}/{scholName}/{currenCity}/{homeTown}")
    public void addUser(@PathVariable(value = "name") String name,
                        @PathVariable(value = "usrName") String userName,
                        @PathVariable(value = "email") String email,
                        @PathVariable(value = "paswrd") String password,
                        @PathVariable(value = "url") String url,
                        @PathVariable(value = "scholName") String scholName,
                        @PathVariable(value = "currenCity") String currentCity,
                        @PathVariable(value = "homeTown") String hometown){

        // TODO : Server side verification of register form data
        // TODO : Make optional field required = false
        try {
            if(Utils.validRegistration()) {
                User user = new User.Builder()
                        .name(name)
                        .profilePicURL(url)
                        .schoolName(scholName)
                        .currentCity(currentCity)
                        .hometown(hometown)
                        .build();

                Account account = new Account.Builder()
                        .email(email)
                        .username(userName)
                        .encPassword(Utils.encryptPassword(password))
                        .build();

                user.setAccount(account);
                account.setUser(user);
                userRepository.save(user);
            }else{
                throw new RegistrationError("Registration error");
            }
        }catch(RegistrationError message){
            System.out.println(message);
        }
    }
}

