package com.example.helpdesk.controller;

import com.example.helpdesk.entity.Comment;
import com.example.helpdesk.entity.Ticket;
import com.example.helpdesk.entity.User;
import com.example.helpdesk.service.CommentService;
import com.example.helpdesk.service.TicketService;
import com.example.helpdesk.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tickets")
public class TicketController extends BaseController {

    private final TicketService ticketService;
    private final CommentService commentService;

    public TicketController(UserService userService, TicketService ticketService, CommentService commentService) {
        super(userService);
        this.ticketService = ticketService;
        this.commentService = commentService;
    }

    @GetMapping
    public String listTickets(Model model) {
        User currentUser = userService.getCurrentUser();
        List<Ticket> tickets = ticketService.getAllTickets();
        
        addCommonAttributes(model);
        model.addAttribute("tickets", tickets);
        model.addAttribute("title", "My Tickets");
        
        return "tickets/list";
    }

    @GetMapping("/new")
    public String createTicketForm(Model model) {
        if (!userService.getCurrentUser().isActive()) {
            return "redirect:/error?accessDenied";
        }
        
        addCommonAttributes(model);
        model.addAttribute("ticket", new Ticket());
        model.addAttribute("title", "Create New Ticket");
        model.addAttribute("statuses", Ticket.Status.values());
        model.addAttribute("priorities", Ticket.Priority.values());
        
        return "tickets/create";
    }

    @PostMapping("/new")
    public String createTicket(@Valid @ModelAttribute("ticket") Ticket ticket, 
                               BindingResult bindingResult, 
                               Model model) {
        if (bindingResult.hasErrors()) {
            addCommonAttributes(model);
            model.addAttribute("title", "Create New Ticket");
            model.addAttribute("statuses", Ticket.Status.values());
            model.addAttribute("priorities", Ticket.Priority.values());
            return "tickets/create";
        }
        
        ticketService.createTicket(ticket);
        return "redirect:/tickets";
    }

    @GetMapping("/{id}")
    public String viewTicket(@PathVariable Long id, Model model) {
        Ticket ticket = ticketService.getTicketById(id);
        List<Comment> comments = commentService.getCommentsByTicket(id);
        
        User currentUser = userService.getCurrentUser();
        boolean canComment = userService.isCurrentUserAgent() || 
                             ticket.getCreatedBy().getId().equals(currentUser.getId());
        
        addCommonAttributes(model);
        model.addAttribute("ticket", ticket);
        model.addAttribute("comments", comments);
        model.addAttribute("title", "Ticket #" + ticket.getId() + ": " + ticket.getTitle());
        model.addAttribute("canComment", canComment);
        model.addAttribute("isOwner", ticket.getCreatedBy().getId().equals(currentUser.getId()));
        model.addAttribute("newComment", new Comment());
        model.addAttribute("statuses", Ticket.Status.values());
        model.addAttribute("getPriorityClass", this::getPriorityClass);
        
        return "tickets/view";
    }

    @PostMapping("/{id}/add-comment")
    public String addComment(@PathVariable Long id, 
                             @RequestParam String content, 
                             Model model) {
        if (content == null || content.trim().isEmpty()) {
            return "redirect:/tickets/" + id + "?error=emptyComment";
        }
        
        commentService.createComment(id, content);
        return "redirect:/tickets/" + id;
    }

    @PostMapping("/{id}/change-status")
    public String changeStatus(@PathVariable Long id, 
                              @RequestParam Ticket.Status status) {
        ticketService.changeTicketStatus(id, status);
        return "redirect:/tickets/" + id;
    }

    @GetMapping("/{id}/edit")
    public String editTicketForm(@PathVariable Long id, Model model) {
        Ticket ticket = ticketService.getTicketById(id);
        
        addCommonAttributes(model);
        model.addAttribute("ticket", ticket);
        model.addAttribute("title", "Edit Ticket #" + ticket.getId());
        model.addAttribute("statuses", Ticket.Status.values());
        model.addAttribute("priorities", Ticket.Priority.values());
        
        return "tickets/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateTicket(@PathVariable Long id, 
                               @Valid @ModelAttribute("ticket") Ticket ticketDetails, 
                               BindingResult bindingResult, 
                               Model model) {
        if (bindingResult.hasErrors()) {
            addCommonAttributes(model);
            model.addAttribute("title", "Edit Ticket #" + id);
            model.addAttribute("statuses", Ticket.Status.values());
            model.addAttribute("priorities", Ticket.Priority.values());
            return "tickets/edit";
        }
        
        ticketDetails.setId(id);
        ticketService.updateTicket(id, ticketDetails);
        return "redirect:/tickets/" + id;
    }
}
