package com.mymemor.mymemor.controller;

import com.mymemor.mymemor.Constants;
import com.mymemor.mymemor.Utils;
import com.mymemor.mymemor.model.*;
import com.mymemor.mymemor.repository.AccountRepository;
import com.mymemor.mymemor.repository.BondRepository;
import com.mymemor.mymemor.repository.UserRepository;
import com.mymemor.mymemor.response.*;
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

@RestController
public class ApiEndpoint {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private BondRepository bondRepository;

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
            list.add("username already exist");
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
    public LoginResponse loginUser(HttpServletResponse response, @RequestParam("username_email") @Valid String username_email,
                                   @RequestParam("password") @Valid String password) {
        LoginResponse loginResponse = new LoginResponse();
        Map<String, List<String>> error = new HashMap<>();
        loginResponse.setStatus("success");

        Account account = null;
        String username = "";

        if(username_email.contains("@")) {
            account = accountRepository.findByEmail(username_email);
            username = account.getUsername();
        }
        else{
            account = accountRepository.findByUsername(username_email);
            username = username_email;
        }

        if (account == null) {
            List<String> list = new ArrayList<>();
            loginResponse.setStatus("error");
            list.add("username not exist");
            error.put("username", list);
            loginResponse.setErrorList(error);
        } else {
            if (!account.getEncPassword().equals(Utils.encryptPassword(password))) {
                List<String> list = new ArrayList<>();
                list.add("paaword not match");
                error.put("password", list);
                loginResponse.setErrorList(error);
            }

            if(error.size() == 0 ){
                loginResponse.setUsername(username);
                Cookie cookie = new Cookie(Constants.COOKIES_NAME, String.valueOf(account.getUser().getId()));
                // set the expiration time
                // 1 hour = 60 seconds x 60 minutes
                cookie.setMaxAge(60 * 60);
                response.addCookie(cookie);
            }
        }
        return loginResponse;
    }

    @GetMapping("/logout")
    public StringResponse logout(HttpServletResponse response){
        StringResponse stringResponse = new StringResponse();
        Map<String, List<String>> error = new HashMap<>();
        List<String> list = new ArrayList<>();
        stringResponse.setStatus("success");

        Cookie cookie = new Cookie(Constants.COOKIES_NAME, "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return stringResponse;
    }

    @GetMapping("/memoline")
    public StringResponse memoline(HttpServletResponse response){
        StringResponse stringResponse = new StringResponse();
        Map<String, List<String>> error = new HashMap<>();
        List<String> list = new ArrayList<>();
        stringResponse.setStatus("success");

        // TODO : memoline backend

        return stringResponse;
    }

    @PostMapping("/add-memory")
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
            list.add("user not logged in");
            error.put("username",list);
            form.setErrorList(error);
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

    @GetMapping("/my-people")
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
            return new HashSet<>();
        }else{
            User user = userRepository.findById(userId).orElseThrow();
            return user.getMyPeople();
        }
    }

    @PostMapping("/profile")
    public ProfileResponse profile(HttpServletRequest request,
                                   @RequestParam("username") @Valid String username)
    {
        ProfileResponse response = new ProfileResponse();
        Map<String, List<String>> error = new HashMap<>();
        List<String> list = new ArrayList<>();
        response.setStatus("success");

        Long userId = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(Constants.COOKIES_NAME)) {
                userId = Long.parseLong(cookie.getValue());
            }
        }
        if(userId == null)
        {
            response.setStatus("Error");
            list.add("user not logged in");
            error.put("username",list);
            response.setErrorList(error);
        }else{
            User loggedUser = userRepository.findById(userId).orElseThrow();
            User userSearched = accountRepository.findByUsername(username).getUser();
            response.setUser(userSearched);

            if(loggedUser.getMyPeople().contains(userSearched)){
                response.setBonded(true);
            }
            else{
                BondRequest bondRequest = new BondRequest();
                bondRequest.setTo(userSearched);
                bondRequest.setFrom(loggedUser);
                if(loggedUser.getSentRequests().contains(bondRequest)){
                    response.setRequested(true);
                }
            }
        }
        return response;
    }


    @PostMapping("/send-bond-request")
    public StringResponse sendrequest (HttpServletRequest request,
                                       @RequestParam("sendRequestToUserName") @Valid String sendRequestToUserName)
    {

        StringResponse response = new StringResponse();
        Map<String, List<String>> error = new HashMap<>();
        List<String> list = new ArrayList<>();
        response.setStatus("success");

        Long userId = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(Constants.COOKIES_NAME)) {
                userId = Long.parseLong(cookie.getValue());
            }
        }
        if(userId == null)
        {
            response.setStatus("Error");
            list.add("user not logged in");
            error.put("username",list);
            response.setErrorList(error);
        }else{
            User userSent = userRepository.findById(userId).orElseThrow();
            User userReceived = accountRepository.findByUsername(sendRequestToUserName).getUser();

            BondRequest bondRequest = new BondRequest();
            bondRequest.setTo(userReceived);
            bondRequest.setFrom(userSent);
            bondRepository.save(bondRequest);
        }
        return response;
    }

    @PostMapping("/bond-requests")
    public BondRequestResponse sendrequest (HttpServletRequest request)
    {

        BondRequestResponse response = new BondRequestResponse();
        Map<String, List<String>> error = new HashMap<>();
        List<String> list = new ArrayList<>();
        response.setStatus("success");

        Long userId = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(Constants.COOKIES_NAME)) {
                userId = Long.parseLong(cookie.getValue());
            }
        }
        if(userId == null)
        {
            response.setStatus("Error");
            list.add("user not logged in");
            error.put("username",list);
            response.setErrorList(error);
        }else{
            User user = userRepository.findById(userId).orElseThrow();
            response.setBondRequests(user.getReceivedRequests());
        }
        return response;
    }

    @PostMapping("/bond-request-action")
    public StringResponse bondRequestAction (HttpServletRequest request,
                                             @RequestParam("bond_request_id") @Valid Long bondRequestId,
                                             @RequestParam("bond_action") @Valid int bondAction)
    {
        StringResponse response = new StringResponse();
        Map<String, List<String>> error = new HashMap<>();
        List<String> list = new ArrayList<>();
        response.setStatus("success");

        Long userId = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(Constants.COOKIES_NAME)) {
                userId = Long.parseLong(cookie.getValue());
            }
        }

        if(userId == null)
        {
            response.setStatus("Error");
            list.add("user not logged in");
            error.put("username",list);
            response.setErrorList(error);
        }else{
            BondRequest bond = bondRepository.findById(bondRequestId).orElseThrow();
            if(bondRequestId==1){
                User user = userRepository.findById(userId).orElseThrow();
                user.getMyPeople().add(bond.getTo());
                userRepository.save(user);
            }
            bond.setBondRequestStatus(BondRequestStatus.fromValue(bondAction));
            bondRepository.save(bond);
        }
        return response;
    }
}