package org.example.spingwallet.web;


import jakarta.servlet.http.HttpSession;
import org.example.spingwallet.security.AuthenticationDetails;
import org.example.spingwallet.transaction.model.Transaction;
import org.example.spingwallet.transaction.service.TransactionService;
import org.example.spingwallet.user.model.User;
import org.example.spingwallet.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/transactions")
public class TransactionController {


    private final TransactionService transactionService;
    private final UserService userService;

    @Autowired
    public TransactionController(TransactionService transactionService, UserService userService) {


        this.transactionService = transactionService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView showTransactions(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {


        List<Transaction> allTransaction = transactionService.getAllTransaction(authenticationDetails.getId());

        ModelAndView mav = new ModelAndView();
        mav.setViewName("transactions");
        mav.addObject("transactions", allTransaction);

        return  mav;
    }

    @GetMapping("/{id}")
    public ModelAndView showTransactionById(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {

        Transaction transaction = transactionService.getTransactionById(id);
        User user = userService.getById(authenticationDetails.getId());
        ModelAndView mav = new ModelAndView();
        mav.setViewName("transaction-result");
        mav.addObject("transaction", transaction);
        mav.addObject("user", user);

        return mav;
    }
}
