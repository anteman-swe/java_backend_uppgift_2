package com.example.helpdesk.controller;

import com.example.helpdesk.entity.User;
import com.example.helpdesk.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController extends BaseController {

    public AdminController(UserService userService) {
        super(userService);
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        
        addCommonAttributes(model);
        model.addAttribute("users", users);
        model.addAttribute("title", "User Management");
        model.addAttribute("roles", User.Role.values());
        model.addAttribute("getRoleClass", this::getRoleClass);
        
        return "admin/users";
    }

    @GetMapping("/users/new")
    public String createUserForm(Model model) {
        addCommonAttributes(model);
        model.addAttribute("user", new User());
        model.addAttribute("title", "Create New User");
        model.addAttribute("roles", User.Role.values());
        model.addAttribute("isEdit", false);
        
        return "admin/user-form";
    }

    @PostMapping("/users/new")
    public String createUser(@Valid @ModelAttribute("user") User user, 
                             BindingResult bindingResult, 
                             Model model) {
        if (bindingResult.hasErrors()) {
            addCommonAttributes(model);
            model.addAttribute("title", "Create New User");
            model.addAttribute("roles", User.Role.values());
            model.addAttribute("isEdit", false);
            return "admin/user-form";
        }
        
        // Check if username already exists
        try {
            userService.getUserByUsername(user.getUsername());
            bindingResult.rejectValue("username", "error.user", "Username already exists");
            addCommonAttributes(model);
            model.addAttribute("title", "Create New User");
            model.addAttribute("roles", User.Role.values());
            model.addAttribute("isEdit", false);
            return "admin/user-form";
        } catch (RuntimeException e) {
            // Username doesn't exist, continue
        }
        
        // Check if email already exists
        try {
            userService.getUserByUsername(user.getEmail());
            bindingResult.rejectValue("email", "error.user", "Email already exists");
            addCommonAttributes(model);
            model.addAttribute("title", "Create New User");
            model.addAttribute("roles", User.Role.values());
            model.addAttribute("isEdit", false);
            return "admin/user-form";
        } catch (RuntimeException e) {
            // Email doesn't exist, continue
        }
        
        user.setActive(true);
        userService.createUser(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        
        addCommonAttributes(model);
        model.addAttribute("user", user);
        model.addAttribute("title", "Edit User");
        model.addAttribute("roles", User.Role.values());
        model.addAttribute("isEdit", true);
        
        return "admin/user-form";
    }

    @PostMapping("/users/{id}/edit")
    public String updateUser(@PathVariable Long id, 
                             @Valid @ModelAttribute("user") User userDetails, 
                             BindingResult bindingResult, 
                             Model model) {
        if (bindingResult.hasErrors()) {
            addCommonAttributes(model);
            model.addAttribute("title", "Edit User");
            model.addAttribute("roles", User.Role.values());
            model.addAttribute("isEdit", true);
            return "admin/user-form";
        }
        
        userDetails.setId(id);
        userService.updateUser(id, userDetails);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable Long id) {
        User user = userService.getUserById(id);
        userService.changeUserStatus(id, !user.isActive());
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/change-role")
    public String changeUserRole(@PathVariable Long id, 
                                 @RequestParam User.Role role) {
        userService.changeUserRole(id, role);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
}
