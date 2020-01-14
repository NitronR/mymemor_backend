package com.mymemor.mymemor.controller;

import com.mymemor.mymemor.Constants;
import com.mymemor.mymemor.model.SearchResult;
import com.mymemor.mymemor.model.User;
import com.mymemor.mymemor.repository.AccountRepository;
import com.mymemor.mymemor.repository.UserRepository;
import com.mymemor.mymemor.response.SearchResponse;
import com.mymemor.mymemor.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
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
    @Autowired
    private SessionService sessionService;

    private List<SearchResult> getSearchResultsByIds(List ids) {
        List<SearchResult> searchResults = new ArrayList<>();
        for (Object id : ids) {
            User user = userRepository.findById(((BigInteger) id).longValue()).orElseThrow();
            searchResults.add(new SearchResult(user));
        }
        return searchResults;
    }

    private List<SearchResult> searchByUsername(EntityManager entityManager, String query, int pageNo) {
        // TODO : Optimise query and Improve get suggestion citeria
        Query q = entityManager.createNativeQuery("select user_id from accounts where username LIKE ? LIMIT ?,?");
        q.setParameter(1, "%" + query + "%");
        q.setParameter(2, (pageNo - 1) * Constants.MAX_SEARCH_PAGE_SIZE);
        q.setParameter(3, Constants.MAX_SEARCH_PAGE_SIZE);
        // TODO : Optimise getSearchResultsByIds
        return getSearchResultsByIds(q.getResultList());
    }

    private List<SearchResult> getUserIdsByName(EntityManager entityManager, String query, int pageNo) {
        // TODO : Optimise query and Improve get suggestion citeria
        Query q = entityManager.createNativeQuery("select id from users where name LIKE ? LIMIT ?,?");
        q.setParameter(1, "%" + query + "%");
        q.setParameter(2, (pageNo - 1) * Constants.MAX_SEARCH_PAGE_SIZE);
        q.setParameter(3, Constants.MAX_SEARCH_PAGE_SIZE);
        // TODO : Optimise getSearchResultsByIds
        return getSearchResultsByIds(q.getResultList());
    }

    private List<SearchResult> searchByUsernameForSuggestions(EntityManager entityManager, String query) {
        // TODO : Optimise query and Improve get suggestion citeria
        Query q = entityManager.createNativeQuery("select user_id from accounts where username LIKE ? LIMIT ? ");
        q.setParameter(1, "%" + query + "%");
        q.setParameter(2, Constants.MAX_SUGGESTION_LIST_LENGTH);
        // TODO : Optimise getSearchResultsByIds
        return getSearchResultsByIds(q.getResultList());
    }

    private List<SearchResult> searchByNameForSuggestions(EntityManager entityManager, String query) {
        // TODO : Optimise query and Improve get suggestion citeria
        Query q = entityManager.createNativeQuery("select id sender users where name LIKE ? LIMIT ?");
        q.setParameter(1, "%" + query + "%");
        q.setParameter(2, Constants.MAX_SUGGESTION_LIST_LENGTH);
        // TODO : Optimise getSearchResultsByIds
        return getSearchResultsByIds(q.getResultList());
    }

    @GetMapping({"/search/{q}/{pageNo}"})
    public SearchResponse getSearchResult(HttpServletRequest request,
                                          @PathVariable(value = "q") String query,
                                          @PathVariable(value = "pageNo") int pageNo) {
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setStatus("success");

        if (StringUtils.isEmpty(query)) {
            searchResponse.setStatus("error");
            searchResponse.setError("Query can't be empty");
        } else {
            if (query.startsWith("@")) {
                String username = query.substring(1);
                searchResponse.setSearchResults(searchByUsername(entityManager, username, pageNo));

            } else {
                searchResponse.setSearchResults(getUserIdsByName(entityManager, query, pageNo));
            }
        }
        // TODO include pagination info
        return searchResponse;
    }

    @GetMapping({"/suggestion/{q}", "/suggestion/"})
    public SearchResponse getSearchSuggestions(HttpServletRequest request,
                                               @PathVariable(value = "q", required = false) String query) {
        SearchResponse searchResponse = new SearchResponse();
        List<String> list = new ArrayList<>();
        searchResponse.setStatus("success");

        Long userId = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(Constants.COOKIES_NAME)) {
                userId = Long.parseLong(cookie.getValue());
            }
        }
        if (userId == null) {
            searchResponse.setStatus("error");
            searchResponse.setError("User must be logged in ");
        } else if (StringUtils.isEmpty(query)) {
            searchResponse.setStatus("error");
            searchResponse.setError("Query can't be empty");
        } else {
            if (query.startsWith("@")) {
                String username = query.substring(1);
                searchResponse.setSearchResults(searchByUsernameForSuggestions(entityManager, username));
            } else {
                searchResponse.setSearchResults(searchByNameForSuggestions(entityManager, query));
            }
        }
        return searchResponse;
    }
}
