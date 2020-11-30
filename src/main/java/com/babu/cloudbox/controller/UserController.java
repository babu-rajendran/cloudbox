package com.babu.cloudbox.controller;

import com.babu.cloudbox.model.Metadata;
import com.babu.cloudbox.model.User;
import com.babu.cloudbox.service.AmazonClient;
import com.babu.cloudbox.service.MetadataService;
import com.babu.cloudbox.service.StorageService;
import com.babu.cloudbox.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MetadataService metadataService;

    private final StorageService storageService;

    private AmazonClient amazonClient;

    @Autowired
    public UserController(StorageService storageService, AmazonClient amazonClient) {
        this.storageService = storageService;
        this.amazonClient = amazonClient;
    }

    @RequestMapping(value= {"/", "/login"}, method= RequestMethod.GET)
    public ModelAndView login() {
        ModelAndView model = new ModelAndView();

        model.setViewName("user/login");
        return model;
    }

    @RequestMapping(value= {"/signup"}, method=RequestMethod.GET)
    public ModelAndView signup() {
        ModelAndView model = new ModelAndView();
        User user = new User();
        model.addObject("user", user);
        model.setViewName("user/signup");

        return model;
    }

    @RequestMapping(value= {"/signup"}, method=RequestMethod.POST)
    public ModelAndView createUser(@Valid User user, BindingResult bindingResult) {
        ModelAndView model = new ModelAndView();
        User userExists = userService.findUserByEmail(user.getEmail());

        if(userExists != null) {
            bindingResult.rejectValue("email", "error.user", "This email already exists!");
        }
        if(bindingResult.hasErrors()) {
            model.setViewName("user/signup");
        } else {
            userService.saveUser(user);
            model.addObject("msg", "User has been registered successfully!");
            model.addObject("user", new User());
            model.setViewName("user/signup");
        }

        return model;
    }

    @RequestMapping(value= {"/home/home"}, method=RequestMethod.GET)
    public ModelAndView home(HttpServletRequest request) {
        ModelAndView model = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Metadata> fileList;
        Map<String, Date> updatedTimeMap = new HashMap<>();
        if (auth != null && auth.getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            fileList = metadataService.findAll();
            Set<String> folderPaths = new HashSet<>();
            for (Metadata metadata : fileList) {
                folderPaths.add(metadata.getFolder());
            }

            for (String folder : folderPaths) {
                updatedTimeMap.putAll(amazonClient.listObjectsFromS3Bucket(folder));
            }

        } else {
            fileList = metadataService.findAllMetadataByUser(auth.getName());

            updatedTimeMap = amazonClient.listObjectsFromS3Bucket(auth.getName());
        }

        for (Metadata metadata : fileList) {
            metadata.setUpdateddate(updatedTimeMap.get(metadata.getFilename()));
        }

        model.addObject("files", fileList);

        User user = userService.findUserByEmail(auth.getName());

        model.addObject("userName", user.getFirstname() + " " + user.getLastname());
        model.setViewName("home/home");
        return model;
    }

    @RequestMapping(value= {"/access_denied"}, method=RequestMethod.GET)
    public ModelAndView accessDenied() {
        ModelAndView model = new ModelAndView();
        model.setViewName("errors/access_denied");
        return model;
    }

}
