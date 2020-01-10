package com.mymemor.mymemor.controller;

import com.mymemor.mymemor.exceptions.EmptyQuery;
import com.mymemor.mymemor.model.User;
import com.mymemor.mymemor.repository.AccountRepository;
import com.mymemor.mymemor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SearchController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @PersistenceContext
    private EntityManager em;

    private List<User> getUserById(List o){
        List<User> list_user = new ArrayList<>();
        for(Object id: o){
            User user = userRepository.findById(((BigInteger) id).longValue()).orElseThrow();
            list_user.add(user);
        }
        return list_user;
    }

    private List getResultBasedOnUsername(EntityManager em, String query){
        Query q =  em.createNativeQuery("select id from accounts where username LIKE ? ");
        q.setParameter(1, "%"+query+"%");
        return q.getResultList();
    }

    private List getResultBasedOnName(EntityManager em, String query){
        Query q =  em.createNativeQuery("select id from users where name LIKE ? ");
        q.setParameter(1, "%"+query+"%");
        return q.getResultList();
    }

    @GetMapping("/search/{q}")
    public List getSearchResultViaAjax(@PathVariable(value = "q") String query) throws EmptyQuery {
        if(query.length()==0)
            throw new EmptyQuery("Query can't be empty");
        if(query.startsWith("@")){
            return getUserById(getResultBasedOnUsername(em,query.substring(1)));
        }else{
            return getUserById(getResultBasedOnName(em,query));
        }
    }
}
