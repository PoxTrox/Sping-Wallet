package org.example.spingwallet.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.example.spingwallet.security.AuthenticationDetails;
import org.example.spingwallet.transaction.model.Transaction;
import org.example.spingwallet.user.model.User;
import org.example.spingwallet.user.service.UserService;
import org.example.spingwallet.wallet.service.WalletService;
import org.example.spingwallet.web.dto.TransferRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/transfers")
public class TransferController {

    private final WalletService walletService;
    private final UserService userService;


    @Autowired
    public TransferController(WalletService walletService, UserService userService) {
        this.walletService = walletService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView getTransfers(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {


        User user = userService.getById(authenticationDetails.getId());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user", user);
        modelAndView.addObject("transferRequest", TransferRequest.builder().build());
        modelAndView.setViewName("transfer");


        return modelAndView;
    }

    @PostMapping
    public ModelAndView initiateTransfer(@Valid TransferRequest transferRequest, BindingResult bindingResult,
                                         @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {


        User user = userService.getById(authenticationDetails.getId());
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("transfer");
            modelAndView.addObject("transferRequest", transferRequest);
            modelAndView.addObject("user", user);
            return modelAndView;
        }
        Transaction transaction = walletService.transfersFunds(user, transferRequest);

        return new ModelAndView("redirect:/transactions/" + transaction.getId());
    }
}
