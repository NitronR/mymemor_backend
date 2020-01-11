package com.mymemor.mymemor.controller;

import com.mymemor.mymemor.Constants;
import com.mymemor.mymemor.FormResponse;
import com.mymemor.mymemor.Utils;
import com.mymemor.mymemor.model.Account;
import com.mymemor.mymemor.model.BondRequest;
import com.mymemor.mymemor.model.Memory;
import com.mymemor.mymemor.model.User;
import com.mymemor.mymemor.repository.AccountRepository;
import com.mymemor.mymemor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

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
    public FormResponse loginUser(HttpServletResponse response,@RequestParam("username") @Valid String username,
                                  @RequestParam("password") @Valid String password) {
        FormResponse form = new FormResponse();
        Map<String, List<String>> error = new HashMap<>();
        form.setStatus("success");

        Account account =(Account)accountRepository.findByUsername(username);

        if (account == null) {
            List<String> list = new ArrayList<>();
            form.setStatus("error");
            list.add("username not exist");
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

            if(error.size() == 0 ){
                Cookie cookie = new Cookie(Constants.COOKIES_NAME, String.valueOf(account.getUser().getId()));
                // set the expiration time
                // 1 hour = 60 seconds x 60 minutes
                cookie.setMaxAge(60 * 60);
                response.addCookie(cookie);
            }
        }
        return form;
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response){
        Cookie cookie = new Cookie(Constants.COOKIES_NAME, "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "success";
    }

    @PostMapping("/addmemory")
    public FormResponse addmemory (HttpServletRequest request,
                             @RequestParam("topic") @Valid String topic,
                             @RequestParam("content") @Valid String content,
                             @RequestParam("data_start") @Valid Date date_start,
                             @RequestParam("date_end") @Valid Date date_end,
                             @RequestParam("location") @Valid String location,
                             @RequestParam("photos") @Valid Set photos) {
        FormResponse form = new FormResponse();
        Map<String, List<String>> error = new HashMap<>();
        List<String> list = new ArrayList<>();
        form.setStatus("success");

        Long userId = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(Constants.COOKIES_NAME)) {
                userId = Long.parseLong(cookie.getValue());
            }
        }

        if(userId == null)
        {
            form.setStatus("Error");
            list.add("username not logged in");
            error.put("username",list);
            form.setErrorList(error);
            // TODO : redirect to login url
        }
        else {
            User user = userRepository.findById(userId).orElseThrow();
            Memory memory = new Memory();
            memory.setTopic(topic);
            memory.setContent(content);
            memory.setStartDate(date_start);
            memory.setEndDate(date_end);
            memory.setLocation(location);
            memory.setPhotos(photos);
            user.getMemories().add(memory);
            userRepository.save(user);
        }
        return form;
    }

    @GetMapping("/mypeople")
    public Set<User> getMyPeople(HttpServletRequest request)
    {
        Long userId = null;
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies) {
            if (cookie.getName().equals(Constants.COOKIES_NAME)) {
                userId = Long.parseLong(cookie.getValue());
            }
        }

        if(userId == null)
        {
            // TODO : redirect to login url
        }else{
            User user = userRepository.findById(userId).orElseThrow();
            return user.getMyPeople();
        }
        return new HashSet<>();
    }


    @PostMapping("/sendrequest")
    public void sendrequest (HttpServletRequest request,
                            @RequestParam("sendRequestToUser") @Valid Long sendRequestToUser)
    {
        Long userId = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(Constants.COOKIES_NAME)) {
                userId = Long.parseLong(cookie.getValue());
            }
        }
        if(userId == null)
        {
            // TODO : redirect to login url
        }else{
            User userSent = userRepository.findById(userId).orElseThrow();
            User userReceived = userRepository.findById(sendRequestToUser).orElseThrow();

            BondRequest bondRequest = new BondRequest();
            bondRequest.setTo(sendRequestToUser);
            bondRequest.setFrom(userId);

            userSent.getSentRequests().add(bondRequest);
            userReceived.getReceivedRequests().add(bondRequest);
            userRepository.save(userSent);
            userRepository.save(userReceived);
        }
    }
}