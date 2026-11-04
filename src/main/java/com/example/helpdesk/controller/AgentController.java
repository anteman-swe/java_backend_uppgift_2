package com.example.helpdesk.controller;

import com.example.helpdesk.entity.Ticket;
import com.example.helpdesk.service.TicketService;
import com.example.helpdesk.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/agent")
public class AgentController extends BaseController {

    private final TicketService ticketService;

    public AgentController(UserService userService, TicketService ticketService) {
        super(userService);
        this.ticketService = ticketService;
    }

    @GetMapping("/tickets")
    public String listAllTickets(Model model) {
        List<Ticket> tickets = ticketService.getAllTickets();
        
        addCommonAttributes(model);
        model.addAttribute("tickets", tickets);
        model.addAttribute("title", "All Tickets");
        
        // Statistics
        model.addAttribute("newCount", ticketService.countTicketsByStatus(Ticket.Status.NEW));
        model.addAttribute("inProgressCount", ticketService.countTicketsByStatus(Ticket.Status.IN_PROGRESS));
        model.addAttribute("waitingCount", ticketService.countTicketsByStatus(Ticket.Status.WAITING_FOR_USER));
        model.addAttribute("resolvedCount", ticketService.countTicketsByStatus(Ticket.Status.RESOLVED));
        model.addAttribute("closedCount", ticketService.countTicketsByStatus(Ticket.Status.CLOSED));
        model.addAttribute("assignedCount", ticketService.countAssignedTickets());
        model.addAttribute("getPriorityClass", this::getPriorityClass);
        
        return "agent/tickets";
    }

    @GetMapping("/tickets/new")
    public String listNewTickets(Model model) {
        List<Ticket> tickets = ticketService.getTicketsByStatus(Ticket.Status.NEW);
        
        addCommonAttributes(model);
        model.addAttribute("tickets", tickets);
        model.addAttribute("title", "New Tickets");
        model.addAttribute("status", "NEW");
        model.addAttribute("getPriorityClass", this::getPriorityClass);
        
        return "agent/tickets-by-status";
    }

    @GetMapping("/tickets/in-progress")
    public String listInProgressTickets(Model model) {
        List<Ticket> tickets = ticketService.getTicketsByStatus(Ticket.Status.IN_PROGRESS);
        
        addCommonAttributes(model);
        model.addAttribute("tickets", tickets);
        model.addAttribute("title", "In Progress Tickets");
        model.addAttribute("status", "IN_PROGRESS");
        model.addAttribute("getPriorityClass", this::getPriorityClass);
        
        return "agent/tickets-by-status";
    }

    @GetMapping("/tickets/assigned")
    public String listAssignedTickets(Model model) {
        List<Ticket> tickets = ticketService.getAssignedTickets();
        
        addCommonAttributes(model);
        model.addAttribute("tickets", tickets);
        model.addAttribute("title", "My Assigned Tickets");
        model.addAttribute("getPriorityClass", this::getPriorityClass);
        
        return "agent/assigned-tickets";
    }

    @GetMapping("/tickets/unassigned")
    public String listUnassignedTickets(Model model) {
        List<Ticket> tickets = ticketService.getUnassignedTickets();
        
        addCommonAttributes(model);
        model.addAttribute("tickets", tickets);
        model.addAttribute("title", "Unassigned Tickets");
        model.addAttribute("getPriorityClass", this::getPriorityClass);
        
        return "agent/unassigned-tickets";
    }

    @PostMapping("/tickets/{id}/assign")
    public String assignTicket(@PathVariable Long id) {
        ticketService.assignTicketToSelf(id);
        return "redirect:/agent/tickets";
    }

    @PostMapping("/tickets/{id}/unassign")
    public String unassignTicket(@PathVariable Long id) {
        ticketService.unassignTicket(id);
        return "redirect:/agent/tickets";
    }
}
