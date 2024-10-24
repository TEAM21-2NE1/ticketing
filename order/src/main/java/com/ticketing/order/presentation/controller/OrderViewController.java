package com.ticketing.order.presentation.controller;

import com.ticketing.order.application.service.OrderRUDService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/api/v1/orders/view")
@RequiredArgsConstructor
public class OrderViewController {

    private final OrderRUDService orderRUDService;

    @GetMapping("/{orderId}")
    public String payment(@PathVariable UUID orderId, Model model) {
        model.addAttribute("orderDto",
                orderRUDService.getOrderRedis(orderId));
        return "payment";
    }


}
