package org.example.spingwallet.web;


import org.example.spingwallet.security.AuthenticationDetails;
import org.example.spingwallet.transaction.model.Transaction;
import org.example.spingwallet.user.model.User;
import org.example.spingwallet.user.service.UserService;
import org.example.spingwallet.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;
    private final UserService userService;

    @Autowired
    public WalletController(WalletService walletService, UserService userService) {
        this.walletService = walletService;
        this.userService = userService;
    }


    @GetMapping
    public ModelAndView walletsPage(@AuthenticationPrincipal AuthenticationDetails details) {

        User user = userService.getById(details.getId());

        Map<UUID, List<Transaction>> fourTransaction = walletService.getLastFourTransaction(user.getWallets());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("wallets");
        modelAndView.addObject("user", user);
        modelAndView.addObject("fourTransaction", fourTransaction);
        return modelAndView;
    }

    @PostMapping
    public String createNewWallet(@AuthenticationPrincipal AuthenticationDetails details){

        User user = userService.getById(details.getId());
        walletService.unlockMoreWallets(user);
        return "redirect:/wallets";
    }

    @PutMapping("/{id}/status")
    public String updateWalletStatus(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationDetails details){

        walletService.switchStatus(id, details.getId());
        return "redirect:/wallets";
    }

    @PutMapping ("/{id}/balance/up")
    public String updateWalletBalance(@PathVariable UUID id){


        Transaction transaction = walletService.topUp(id, new BigDecimal("20.00"));
        return "redirect:/transactions/" + transaction.getId();
    }
}
