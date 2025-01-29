package org.example.spingwallet.web;


import jakarta.validation.Valid;
import org.example.spingwallet.user.model.User;
import org.example.spingwallet.user.service.UserService;
import org.example.spingwallet.web.dto.LoginRequest;
import org.example.spingwallet.web.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
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
    public ModelAndView getLogin() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("loginRequest", new LoginRequest());

        return modelAndView;
    }

    @PostMapping("login")
    public String login (@Valid LoginRequest loginRequest, BindingResult bindingResult ) {

        if(bindingResult.hasErrors()) {
            return "login";
        }

       userService.login(loginRequest);

        return ("redirect:/home");
    }

    @GetMapping("/register")
    public ModelAndView getRegister() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        modelAndView.addObject("registerRequest", new RegisterRequest());

        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView registerNewUser(@Valid RegisterRequest registerRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("register");
        }

       userService.register(registerRequest);

        return new ModelAndView("redirect:/home");
    }

    @GetMapping("/home")
    public ModelAndView getHome() {

        ModelAndView mav = new ModelAndView();
        User userServiceById = userService.getById(UUID.fromString("702254ea-a5e5-457d-b0f3-aa4236e49ac5"));
        mav.addObject("user", userServiceById);
        mav.setViewName("/home");
        return mav;
    }

}
