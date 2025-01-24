package org.example.spingwallet.web;

import jakarta.persistence.ManyToOne;
import org.example.spingwallet.transaction.model.Transaction;
import org.example.spingwallet.transaction.service.TransactionService;
import org.example.spingwallet.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ModelAndView showTransactions() {

        List<Transaction> allTransaction = transactionService.getAllTransaction(UUID.fromString("702254ea-a5e5-457d-b0f3-aa4236e49ac5"));

        ModelAndView mav = new ModelAndView();
        mav.setViewName("transactions");
        mav.addObject("transactions", allTransaction);

        return  mav;
    }
}
