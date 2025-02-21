package org.example.spingwallet.web;

import jakarta.servlet.http.HttpSession;
import org.example.spingwallet.security.AuthenticationDetails;
import org.example.spingwallet.subscription.model.SubscriptionType;
import org.example.spingwallet.subscription.service.SubscriptionService;
import org.example.spingwallet.transaction.model.Transaction;
import org.example.spingwallet.user.model.User;
import org.example.spingwallet.user.service.UserService;
import org.example.spingwallet.web.dto.UpgradeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final UserService userService;
    private final SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(UserService userService, SubscriptionService subscriptionService) {
        this.userService = userService;
        this.subscriptionService = subscriptionService;
    }


    @GetMapping()
    public ModelAndView getSubscriptions(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {


        User user = userService.getById(authenticationDetails.getId());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user", user);
        modelAndView.addObject("upgradeRequest", UpgradeRequest.builder().build());
        modelAndView.setViewName("upgrade");


        return modelAndView;

    }

    @PostMapping()
    public String postUpgradeSubscription(@RequestParam("subscription-type") SubscriptionType subscriptionType, UpgradeRequest upgradeRequest
            , @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {


        User user = userService.getById(authenticationDetails.getId());

        Transaction upgrade = subscriptionService.upgrade(user, subscriptionType, upgradeRequest);

        return "redirect:/transactions/" + upgrade.getId();
    }

    @GetMapping("/history")
    public ModelAndView getSubscriptionHistory(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {


        ModelAndView mav = new ModelAndView();
        mav.setViewName("subscription-history");
        mav.addObject("users", userService.getById(authenticationDetails.getId()));


        return mav;
    }
}
