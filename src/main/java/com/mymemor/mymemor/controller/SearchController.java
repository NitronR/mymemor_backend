package com.mymemor.mymemor.controller;

import com.mymemor.mymemor.Utils;
import com.mymemor.mymemor.exceptions.EmptyQuery;
import com.mymemor.mymemor.exceptions.InvalidName;
import com.mymemor.mymemor.exceptions.InvalidUserName;
import com.mymemor.mymemor.model.User;
import com.mymemor.mymemor.repository.AccountRepository;
import com.mymemor.mymemor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
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
    private EntityManager entityManager;

    private List<User> getUsersByIds(List ids){
        List<User> listUser = new ArrayList<>();
        for(Object id: ids){
            User user = userRepository.findById(((BigInteger) id).longValue()).orElseThrow();
            listUser.add(user);
        }
        return listUser;
    }

    private List getUserIdsByUsername(EntityManager entityManager, String query){
        // TODO : Optimise query
        Query q =  entityManager.createNativeQuery("select id from accounts where username LIKE ? ");
        q.setParameter(1, "%"+query+"%");
        return q.getResultList();
    }

    private List getUserIdsByName(EntityManager entityManager, String query){
        // TODO : Optimise query
        Query q =  entityManager.createNativeQuery("select id from users where name LIKE ? ");
        q.setParameter(1, "%"+query+"%");
        return q.getResultList();
    }

    @GetMapping({"/search/{q}","/search/"})
    public List getSearchResultViaAjax(@PathVariable(value = "q",required = false) String query){
        try {
            if(StringUtils.isEmpty(query))
                throw new EmptyQuery("Query can't be empty");
            else {
                if (query.startsWith("@")) {
                    String username = query.substring(1);
                    if(Utils.validateUsername(username)) {
                        return getUsersByIds(getUserIdsByUsername(entityManager, username));
                    }else
                        throw new InvalidUserName("Enter Valid Username");
                } else {
                    if(Utils.validateName(query)) {
                        return getUsersByIds(getUserIdsByName(entityManager, query));
                    }else
                        throw new InvalidName("Enter Valid Name");
                }
            }
        }catch (EmptyQuery | InvalidUserName | InvalidName message ){
            System.out.println(message);
        }
        return new ArrayList<>();
    }
}
