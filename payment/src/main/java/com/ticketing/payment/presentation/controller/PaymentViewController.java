package com.ticketing.payment.presentation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/payments")
public class PaymentViewController {


    @GetMapping("/view")
    public String payment() {
        return "payment";
    }

}
