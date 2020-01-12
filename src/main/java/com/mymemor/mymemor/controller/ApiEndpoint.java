package com.mymemor.mymemor.controller;

import com.mymemor.mymemor.Constants;
import com.mymemor.mymemor.Utils;
import com.mymemor.mymemor.forms.LoginForm;
import com.mymemor.mymemor.forms.RegisterForm;
import com.mymemor.mymemor.model.Account;
import com.mymemor.mymemor.model.BondRequest;
import com.mymemor.mymemor.model.Memory;
import com.mymemor.mymemor.model.User;
import com.mymemor.mymemor.repository.AccountRepository;
import com.mymemor.mymemor.repository.BondRepository;
import com.mymemor.mymemor.repository.MemoryRepository;
import com.mymemor.mymemor.repository.UserRepository;
import com.mymemor.mymemor.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping
public class ApiEndpoint {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private BondRepository bondRepository;
    @Autowired
    private MemoryRepository memoryRepo;

    @PostMapping("/register")
    public FormResponse registeruser(@Valid @RequestBody RegisterForm regForm) {

        FormResponse form = new FormResponse();
        Map<String, List<String>> error = new HashMap<>();
        List<String> list = new ArrayList<>();
        form.setStatus("success");
        Account account = accountRepository.findByUsername(regForm.username);

        if (account != null) {

            form.setStatus("Error");
            list.add("username already exist");
            error.put("username", list);
            form.setErrors(error);
        } else {
            User user = new User();
            account = new Account();
            account.setUsername(regForm.username);
            account.setEncPassword(Utils.encryptPassword(regForm.password));
            account.setEmail(regForm.email);

            user.setName(regForm.name);
            user.setSchoolName(regForm.schoolName);
            user.setCurrentCity(regForm.currentCity);
            user.setProfilePicURL(regForm.profilePicURL);
            user.setHometown(regForm.homeTown);
            user.setAccount(account);
            account.setUser(user);
            userRepository.save(user);
        }
        return form;
    }

    @PostMapping("/login")
    public LoginResponse loginUser(HttpServletResponse response, @Valid @RequestBody LoginForm loginForm) {
        LoginResponse loginResponse = new LoginResponse();
        Map<String, List<String>> error = new HashMap<>();
        loginResponse.setStatus("success");

        Account account = null;
        String username = "";

        if (loginForm.username_email.contains("@")) {
            account = accountRepository.findByEmail(loginForm.username_email);
            username = account.getUsername();
        } else {
            account = accountRepository.findByUsername(loginForm.username_email);
            username = loginForm.username_email;
        }

        if (account == null) {
            List<String> list = new ArrayList<>();
            loginResponse.setStatus("error");
            list.add("Invalid credentials.");
            error.put("non_field", list);
            loginResponse.setErrors(error);
        } else {
            if (!account.getEncPassword().equals(Utils.encryptPassword(loginForm.password))) {
                List<String> list = new ArrayList<>();
                list.add("Invalid credentials.");
                error.put("non_field", list);
                loginResponse.setErrors(error);
            }

            if (error.size() == 0) {
                loginResponse.setUsername(username);
                Cookie cookie = new Cookie(Constants.COOKIES_NAME, String.valueOf(account.getUser().getId()));
                // set the expiration time
                // 1 hour = 60 seconds x 60 minutes
                cookie.setMaxAge(60 * 60);
                response.addCookie(cookie);
            } else {
                loginResponse.setStatus("error");
            }
        }
        return loginResponse;
    }

    @GetMapping("/memoline/{sortby}")
    public MymemoResponse memoline(HttpServletRequest request, @PathVariable(value = "sortby") Optional<String> sortby) {
        MymemoResponse myMemoResponse = new MymemoResponse();
        myMemoResponse.setStatus("success");
        List<Memory> memoline = new ArrayList<>();
        List<Memory> memories = new ArrayList<>();

        String type = "create_time";
        if (sortby.isPresent()) {
            type = sortby.get();
        }

        try {
            if (type.equals("create_time")) {

                memories.addAll(memoryRepo.findAllByOrderByCreatedAtAsc());

            } else {
                memories.addAll(memoryRepo.findAllByOrderByStartDateAsc());
            }
        } catch (Exception e) {
            myMemoResponse.setStatus("error");
            myMemoResponse.setError("not found memory");
            return myMemoResponse;
        }

        // session user
        Long userId = getUserId(request);
        User sessionUser = userRepository.findById(userId).orElseThrow();

        // TODO improve
        for (Memory memory : memories) {
            Set<User> users = memory.getUsers();
            if (users.contains(sessionUser)) {
                memoline.add(memory);
            }
        }
        myMemoResponse.setMemories(memoline);
        return myMemoResponse;
    }

    @GetMapping("/logout")
    public StringResponse logout(HttpServletResponse response) {
        StringResponse stringResponse = new StringResponse();
        Map<String, List<String>> error = new HashMap<>();
        List<String> list = new ArrayList<>();
        stringResponse.setStatus("success");

        Cookie cookie = new Cookie(Constants.COOKIES_NAME, "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return stringResponse;
    }

    private Long getUserId(HttpServletRequest request) {
        Long userId = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(Constants.COOKIES_NAME)) {
                userId = Long.parseLong(cookie.getValue());
            }
        }
        return userId;
    }

    @PostMapping("/add-memory")
    public FormResponse addmemory(HttpServletRequest request,
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

        Long userId = getUserId(request);

        if (userId == null) {
            form.setStatus("Error");
            list.add("user not logged in");
            error.put("username", list);
            form.setErrors(error);
        } else {
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
    public Set<User> getMyPeople(HttpServletRequest request) {
        Long userId = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(Constants.COOKIES_NAME)) {
                userId = Long.parseLong(cookie.getValue());
            }
        }

        if (userId == null) {
            return new HashSet<>();
        } else {
            User user = userRepository.findById(userId).orElseThrow();
            return user.getMyPeople();
        }
    }

    @PostMapping("/profile/{username}")
    public void profile(HttpServletRequest request,
                        @RequestParam("username") @Valid String username) {

        // TODO : profile backend
    }


    @PostMapping("/send-bond-request")
    public StringResponse sendrequest(HttpServletRequest request,
                                      @RequestParam("sendRequestToUserName") @Valid String sendRequestToUserName) {

        StringResponse response = new StringResponse();
        response.setStatus("success");

        Long userId = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(Constants.COOKIES_NAME)) {
                userId = Long.parseLong(cookie.getValue());
            }
        }
        if (userId == null) {
            response.setStatus("Error");
            response.setError("user not logged in");
        } else {
            User userSent = userRepository.findById(userId).orElseThrow();
            User userReceived = accountRepository.findByUsername(sendRequestToUserName).getUser();

            BondRequest bondRequest = new BondRequest();
            bondRequest.setTo(userSent);
            bondRequest.setFrom(userReceived);
            bondRepository.save(bondRequest);
        }
        return response;
    }

    @PostMapping("/bond-requests")
    public BondRequestResponse sendrequest(HttpServletRequest request) {

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
        if (userId == null) {
            response.setStatus("Error");
            error.put("username", list);
            response.setError("user not logged in");
        } else {
            User user = userRepository.findById(userId).orElseThrow();
            response.setBondRequests(user.getReceivedRequests());
        }
        return response;
    }

//    @PostMapping("/bond-request-action")
//    public StringResponse sendrequest (HttpServletRequest request)
//    {
//
//        BondRequestResponse response = new BondRequestResponse();
//        Map<String, List<String>> error = new HashMap<>();
//        List<String> list = new ArrayList<>();
//        response.setStatus("success");
//
//        Long userId = null;
//        Cookie[] cookies = request.getCookies();
//        for (Cookie cookie : cookies) {
//            if (cookie.getName().equals(Constants.COOKIES_NAME)) {
//                userId = Long.parseLong(cookie.getValue());
//            }
//        }
//        if(userId == null)
//        {
//            response.setStatus("Error");
//            list.add("user not logged in");
//            error.put("username",list);
//            response.setErrorList(error);
//        }else{
//            User user = userRepository.findById(userId).orElseThrow();
//            response.setBondRequests(user.getReceivedRequests());
//        }
//        return response;
//    }
}