package com.example.helpdesk.controller;

import com.example.helpdesk.entity.Ticket;
import com.example.helpdesk.entity.User;
import com.example.helpdesk.service.UserService;
import org.springframework.ui.Model;

public class BaseController {

    protected final UserService userService;

    public BaseController(UserService userService) {
        this.userService = userService;
    }

    protected void addCommonAttributes(Model model) {
        model.addAttribute("isAgent", userService.isCurrentUserAgent());
        model.addAttribute("isAdmin", userService.isCurrentUserAdmin());
    }

    protected String getPriorityClass(Ticket.Priority priority) {
        if (priority == null) return "secondary";
        return switch (priority) {
            case LOW -> "success";
            case MEDIUM -> "warning";
            case HIGH -> "danger";
            case CRITICAL -> "dark";
        };
    }

    protected String getRoleClass(User.Role role) {
        if (role == null) return "secondary";
        return switch (role) {
            case USER -> "secondary";
            case AGENT -> "info";
            case ADMIN -> "danger";
        };
    }
}
