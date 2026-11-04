package com.example.helpdesk.controller;

import com.example.helpdesk.service.TicketService;
import com.example.helpdesk.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController extends BaseController {

    private final TicketService ticketService;

    public HomeController(UserService userService, TicketService ticketService) {
        super(userService);
        this.ticketService = ticketService;
    }

    @GetMapping("/")
    public String home(Model model) {
        if (userService.getCurrentUser() != null) {
            return "redirect:/tickets";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Model model) {
        if (userService.getCurrentUser() != null) {
            return "redirect:/tickets";
        }
        model.addAttribute("title", "Login");
        return "login";
    }

    @GetMapping("/error")
    public String error(Model model) {
        model.addAttribute("title", "Error");
        return "error";
    }
}
