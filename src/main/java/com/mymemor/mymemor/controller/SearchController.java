package com.mymemor.mymemor.controller;

import com.mymemor.mymemor.exceptions.EmptyQuery;
import com.mymemor.mymemor.model.Account;
import com.mymemor.mymemor.model.User;
import com.mymemor.mymemor.repository.AccountRepository;
import com.mymemor.mymemor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SearchController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/search/{q}")
    public List getSearchResultViaAjax(@PathVariable(value = "q") String query) throws EmptyQuery {
        List<User> list_user = new ArrayList<>();
        List<User> all_user = new ArrayList<>();
        List<Account> all_account = new ArrayList<>();

        List<String> username = new ArrayList<>();

        if(query.length()==0)
            throw new EmptyQuery("Query can't be empty");

        if(query.startsWith("@")){

            all_account = accountRepository.findAll();
            for(Account ac: all_account) {
                list_user.add(ac.getUser());
            }

//            Account account = accountRepository.findById((long) 1).orElseThrow();
//            list_user.add(account.getUser());
        }else{
            User user = userRepository.findById((long) 1).orElseThrow();
            list_user.add(user);
        }
        return list_user;
    }
}
