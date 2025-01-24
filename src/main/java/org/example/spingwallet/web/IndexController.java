package org.example.spingwallet.web;


import jakarta.validation.Valid;
import org.example.spingwallet.user.model.User;
import org.example.spingwallet.user.service.UserService;
import org.example.spingwallet.web.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/")
public class IndexController {

    private final UserService userService;

    @Autowired
    public IndexController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping()
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String getLogin() {

        return "login";
    }

    @GetMapping("/register")
    public ModelAndView getRegister() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        modelAndView.addObject("registerRequest", new RegisterRequest());

        return modelAndView;
    }

    @PostMapping ("/register")
    public ModelAndView registerNewUser (@Valid  RegisterRequest registerRequest) {

        System.out.println();
//        ModelAndView mav = new ModelAndView();
//        mav.setViewName("register");
//        mav.addObject("registerRequest", registerRequest);

        return null;
    }

    @GetMapping("/home")
    public ModelAndView getHome() {

        ModelAndView mav = new ModelAndView("home");
        User userServiceById = userService.getById(UUID.fromString("702254ea-a5e5-457d-b0f3-aa4236e49ac5"));
        mav.addObject("user", userServiceById);
        mav.setViewName("home");
        return mav;
    }

}
