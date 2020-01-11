package com.mymemor.mymemor.controller;

import com.mymemor.mymemor.Constants;
import com.mymemor.mymemor.exceptions.EmptyQuery;
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

    private List<User> getUserIdsByUsername(EntityManager entityManager, String query,int pageNo){
        // TODO : Optimise query and Improve get suggestion citeria
        Query q =  entityManager.createNativeQuery("select user_id from accounts where username LIKE ? LIMIT ?,?");
        q.setParameter(1, "%"+query+"%");
        q.setParameter(2, (pageNo-1) * Constants.MAX_SEARCH_PAGE_SIZE);
        q.setParameter(3, Constants.MAX_SEARCH_PAGE_SIZE);
        // TODO : Optimise getUsersByIds
        return getUsersByIds(q.getResultList());
    }

    private List<User> getUserIdsByName(EntityManager entityManager, String query,int pageNo){
        // TODO : Optimise query and Improve get suggestion citeria
        Query q =  entityManager.createNativeQuery("select id from users where name LIKE ? LIMIT ?,?");
        q.setParameter(1, "%"+query+"%");
        q.setParameter(2, (pageNo-1) * Constants.MAX_SEARCH_PAGE_SIZE);
        q.setParameter(3, Constants.MAX_SEARCH_PAGE_SIZE);
        // TODO : Optimise getUsersByIds
        return getUsersByIds(q.getResultList());
    }

    private List<User> getUserIdsByUsernameForSuggestions(EntityManager entityManager, String query){
        // TODO : Optimise query and Improve get suggestion citeria
        Query q =  entityManager.createNativeQuery("select user_id from accounts where username LIKE ? LIMIT ? ");
        q.setParameter(1, "%"+query+"%");
        q.setParameter(2, Constants.MAX_SUGGESTION_LIST_LENGTH);
        // TODO : Optimise getUsersByIds
        return getUsersByIds(q.getResultList());
    }

    private List<User> getUserIdsByNameForSuggestions(EntityManager entityManager, String query){
        // TODO : Optimise query and Improve get suggestion citeria
        Query q =  entityManager.createNativeQuery("select id from users where name LIKE ? LIMIT ?");
        q.setParameter(1, "%"+query+"%");
        q.setParameter(2, Constants.MAX_SUGGESTION_LIST_LENGTH);
        // TODO : Optimise getUsersByIds
        return getUsersByIds(q.getResultList());
    }

    @GetMapping({"/search/{q}/{pageNo}","/search/{pageNo}"})
    public List<User> getSearchResult(@PathVariable(value = "q",required = false) String query,
                                      @PathVariable(value = "pageNo") int pageNo){
        try {
            if(StringUtils.isEmpty(query))
                throw new EmptyQuery("Query can't be empty");
            else {
                if (query.startsWith("@")) {
                    String username = query.substring(1);
                    return getUserIdsByUsername(entityManager, username,pageNo);

                }
                else {
                    return getUserIdsByName(entityManager, query,pageNo);
                }
            }
        }catch (EmptyQuery message ){
            System.out.println(message);
        }
        return new ArrayList<>();
    }

    @GetMapping({"/suggestion/{q}","/suggestion/"})
    public List getSearchSuggestions(@PathVariable(value = "q",required = false) String query){
        try {
            if(StringUtils.isEmpty(query))
                throw new EmptyQuery("Query can't be empty");
            else {
                if (query.startsWith("@")) {
                    String username = query.substring(1);
                    return getUserIdsByUsernameForSuggestions(entityManager, username);
                } else {
                    return getUserIdsByNameForSuggestions(entityManager, query);
                }
            }
        }catch (EmptyQuery message ){
            System.out.println(message);
        }
//        Cookie cookie = new Cookie("username", "Jovan");
//        String value = cookie.getValue(cookie);
        return new ArrayList<>();
    }
}
