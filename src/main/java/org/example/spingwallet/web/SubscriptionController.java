package org.example.spingwallet.web;

import org.example.spingwallet.subscription.service.SubscriptionService;
import org.example.spingwallet.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final UserService userService;

    @Autowired
    public SubscriptionController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    public String getSubscriptions() {


        return "upgrade";

    };

    @GetMapping("/history")
    public ModelAndView getSubscriptionHistory() {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("subscription-history");
        mav.addObject("users",userService.getById(UUID.fromString("702254ea-a5e5-457d-b0f3-aa4236e49ac5")));


        return mav;
    }
}
