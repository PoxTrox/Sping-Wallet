package org.example.spingwallet.web;


import jakarta.servlet.http.HttpSession;
import org.example.spingwallet.transaction.model.Transaction;
import org.example.spingwallet.transaction.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public TransactionController( TransactionService transactionService) {


        this.transactionService = transactionService;
    }

    @GetMapping
    public ModelAndView showTransactions(HttpSession session) {

        Object userId = session.getAttribute("user_id");
        List<Transaction> allTransaction = transactionService.getAllTransaction(UUID.fromString(userId.toString()));

        ModelAndView mav = new ModelAndView();
        mav.setViewName("transactions");
        mav.addObject("transactions", allTransaction);

        return  mav;
    }

    @GetMapping("/{id}")
    public ModelAndView showTransactionById(@PathVariable UUID id) {

        Transaction transaction = transactionService.getTransactionById(id);
        ModelAndView mav = new ModelAndView();
        mav.setViewName("transaction-result");
        mav.addObject("transaction", transaction);

        return mav;
    }
}
