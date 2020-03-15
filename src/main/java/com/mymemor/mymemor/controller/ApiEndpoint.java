package com.mymemor.mymemor.controller;

import com.mymemor.mymemor.Utils;
import com.mymemor.mymemor.exceptions.EntityDoesNotExist;
import com.mymemor.mymemor.exceptions.InvalidBondActionException;
import com.mymemor.mymemor.exceptions.NotAuthenticatedException;
import com.mymemor.mymemor.forms.AddMemoryForm;
import com.mymemor.mymemor.forms.LoginForm;
import com.mymemor.mymemor.forms.RegisterForm;
import com.mymemor.mymemor.model.*;
import com.mymemor.mymemor.repository.AccountRepository;
import com.mymemor.mymemor.repository.BondRepository;
import com.mymemor.mymemor.repository.MemoryRepository;
import com.mymemor.mymemor.repository.UserRepository;
import com.mymemor.mymemor.response.*;
import com.mymemor.mymemor.service.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiEndpoint {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private BondRepository bondRepository;
    @Autowired
    private MemoryRepository memoryRepo;
    @Autowired
    private SessionService sessionService;

    Logger logger = LoggerFactory.getLogger(ApiEndpoint.class);

    @PostMapping("/register")
    public FormResponse registeruser(@Valid @RequestBody RegisterForm regForm) {
        FormResponse form = new FormResponse();
        Map<String, List<String>> error = new HashMap<>();
        List<String> list = new ArrayList<>();
        form.setStatus("success");
        Account account = accountRepository.findByUsername(regForm.username);

        // TODO email unique validation
        if (account != null) {
            form.setStatus("error");
            list.add("Username already exists.");
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
            user.setCollegeName(regForm.collegeName);
            user.setCurrentCity(regForm.currentCity);
            user.setProfilePicURL(regForm.profilePicURL);
            user.setHometown(regForm.hometown);
            user.setAccount(account);
            account.setUser(user);
            userRepository.save(user);

            logger.info("New user registered username: {}.", regForm.username);
        }
        return form;
    }

    @PostMapping("/login")
    public LoginResponse loginUser(HttpServletResponse response, @Valid @RequestBody LoginForm loginForm, HttpSession httpSession) {
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

                // add user_id to session
                httpSession.setAttribute("user_id", account.getUser().getId());

                logger.info("User logged in, username/email: {}.", loginForm.username_email);
            } else {
                loginResponse.setStatus("error");
            }
        }
        return loginResponse;
    }

    @GetMapping("/memoline/{sortby}")
    public MemolineResponse memoline(HttpSession session, @PathVariable(value = "sortby") Optional<String> sortby, HttpSession httpSession) throws NotAuthenticatedException, EntityDoesNotExist {
        MemolineResponse memolineResponse = new MemolineResponse();
        memolineResponse.setStatus("success");
        List<Memory> memoline = new ArrayList<>();
        List<Memory> memories = new ArrayList<>();

        String type = "create_time";
        if (sortby.isPresent()) {
            type = sortby.get();
        }

        try {
            if (type.equals("create_time")) {
                memories.addAll(memoryRepo.findAllByOrderByCreatedAtDesc());

            } else {
                memories.addAll(memoryRepo.findAllByOrderByStartDateDesc());
            }
        } catch (Exception e) {
            memolineResponse.setStatus("error");
            memolineResponse.setError("not found memory");
            return memolineResponse;
        }

        User sessionUser = sessionService.getSessionUser(session);

        // TODO improve
        for (Memory memory : memories) {
            Set<User> users = memory.getUsers();
            if (users.contains(sessionUser)) {
                memoline.add(memory);
            }
        }
        memolineResponse.setMemories(memoline);

        logger.info("Memoline served receiver @{}.", sessionUser.getUsername());

        return memolineResponse;
    }

    @GetMapping("/logout")
    public StringResponse logout(HttpSession httpSession) {
        StringResponse stringResponse = new StringResponse();
        stringResponse.setStatus("success");

        httpSession.removeAttribute("user_id");

        return stringResponse;
    }

    @PostMapping("/add-memory")
    public FormResponse addMemory(HttpSession session, @RequestBody @Valid AddMemoryForm addMemoryForm) throws NotAuthenticatedException, EntityDoesNotExist {
        FormResponse form = new FormResponse();
        Map<String, List<String>> error = new HashMap<>();
        List<String> list = new ArrayList<>();
        form.setStatus("success");

        User user = sessionService.getSessionUser(session);

        Memory memory = new Memory();
        memory.setTopic(addMemoryForm.topic);
        memory.setContent(addMemoryForm.content);
        memory.setStartDate(addMemoryForm.date_start);
        memory.setEndDate(addMemoryForm.date_end);
        memory.setLocation(addMemoryForm.location);
        memory.setPhotos(addMemoryForm.photos);
        memory.setCreator(user);
        memory.getUsers().add(user);

        user.getCreatedMemories().add(memory);

        userRepository.save(user);
        return form;
    }

    @GetMapping("/my-people")
    public MyPeopleResponse getMyPeople(HttpSession session) throws NotAuthenticatedException, EntityDoesNotExist {
        MyPeopleResponse myPeopleResponse = new MyPeopleResponse();
        User user = sessionService.getSessionUser(session);

        myPeopleResponse.setPeople(user.getMyPeople());

        return myPeopleResponse;
    }

    @GetMapping("/profile/{username}")
    public ProfileResponse profile(HttpSession session,
                                   @PathVariable("username") @Valid String username) throws NotAuthenticatedException, EntityDoesNotExist {
        ProfileResponse response = new ProfileResponse();
        response.setStatus("success");

        User sessionUser = sessionService.getSessionUser(session);
        User userSearched = accountRepository.findByUsername(username).getUser();
        response.setUser(userSearched);

        if (sessionUser.getMyPeople().contains(userSearched)) {
            response.setBonded(true);
        } else {
            // TODO send info receiver indicate the profile user has sent a request receiver current user
            // is requested is symmetric for now
            response.setRequested(bondRepository.existsBySenderAndReceiver(sessionUser, userSearched) || bondRepository.existsBySenderAndReceiver(userSearched, sessionUser));
        }

        logger.info("Profile of @{} served to @{}.", username, sessionUser.getUsername());

        return response;
    }


    @PostMapping("/send-bond-request")
    public StringResponse sendBondRequest(HttpSession session,
                                          @RequestParam("username") @Valid String username) throws NotAuthenticatedException, EntityDoesNotExist {
        StringResponse response = new StringResponse();
        response.setStatus("success");

        // TODO validate if already requested, already bonded

        User sender = sessionService.getSessionUser(session);
        User receiver = accountRepository.findByUsername(username).getUser();

        BondRequest bondRequest = new BondRequest();
        bondRequest.setSender(sender);
        bondRequest.setReceiver(receiver);
        bondRepository.save(bondRequest);

        sender.getSentRequests().add(bondRequest);
        receiver.getReceivedRequests().add(bondRequest);

        userRepository.save(sender);
        userRepository.save(receiver);

        logger.info("@{} sent bond request to @{}.", sender.getUsername(), receiver.getUsername());

        return response;
    }

    @GetMapping("/bond-requests")
    public BondRequestResponse bondRequests(HttpSession session) throws NotAuthenticatedException, EntityDoesNotExist {
        BondRequestResponse response = new BondRequestResponse();
        List<String> list = new ArrayList<>();
        response.setStatus("success");

        User sessionUser = sessionService.getSessionUser(session);
        response.setBondRequests(sessionUser.getReceivedRequests());

        logger.info("Bond requests served receiver @{}.", sessionUser.getUsername());

        return response;
    }

    @PostMapping("/bond-request-action")
    public StringResponse bondRequestAction(HttpSession session,
                                            @RequestParam("bond_request_id") @Valid Long bondRequestId,
                                            @RequestParam("action") @Valid int bondAction) throws NotAuthenticatedException, InvalidBondActionException, EntityDoesNotExist {
        StringResponse response = new StringResponse();
        response.setStatus("success");

        User sessionUser = sessionService.getSessionUser(session);

        BondRequest bondRequest = bondRepository.findById(bondRequestId).orElseThrow();

        User sender = bondRequest.getSender();
        User receiver = bondRequest.getReceiver();

        if (!receiver.equals(sessionUser)) {
            throw new AccessDeniedException("You are not authorized for this.");
        }

        // if accept then add to my people
        if (BondAction.fromValue(bondAction) == BondAction.ACCEPT) {
            sender.getMyPeople().add(receiver);
            receiver.getMyPeople().add(sender);
            userRepository.save(sender);
            userRepository.save(receiver);

            logger.info("Bond request from @{} accepted by @{}.", sender.getUsername(), receiver.getUsername());
        } else {
            logger.info("Bond request from @{} declined by @{}.", sender.getUsername(), receiver.getUsername());
        }

        // bond request object not required anymore
        bondRepository.delete(bondRequest);

        return response;
    }
}