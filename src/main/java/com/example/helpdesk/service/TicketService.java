package com.example.helpdesk.service;

import com.example.helpdesk.entity.Ticket;
import com.example.helpdesk.entity.User;
import com.example.helpdesk.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserService userService;

    public TicketService(TicketRepository ticketRepository, UserService userService) {
        this.ticketRepository = ticketRepository;
        this.userService = userService;
    }

    public Ticket createTicket(Ticket ticket) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("User must be logged in to create a ticket");
        }
        ticket.setCreatedBy(currentUser);
        ticket.setStatus(Ticket.Status.NEW);
        return ticketRepository.save(ticket);
    }

    public Ticket updateTicket(Long id, Ticket ticketDetails) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));
        
        User currentUser = userService.getCurrentUser();
        
        // Only the creator or an agent/admin can update the ticket
        if (!isUserAuthorizedForTicket(currentUser, ticket)) {
            throw new RuntimeException("Not authorized to update this ticket");
        }
        
        ticket.setTitle(ticketDetails.getTitle());
        ticket.setDescription(ticketDetails.getDescription());
        ticket.setCategory(ticketDetails.getCategory());
        ticket.setPriority(ticketDetails.getPriority());
        
        return ticketRepository.save(ticket);
    }

    public void deleteTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));
        
        User currentUser = userService.getCurrentUser();
        
        // Only the creator or an agent/admin can delete the ticket
        if (!isUserAuthorizedForTicket(currentUser, ticket)) {
            throw new RuntimeException("Not authorized to delete this ticket");
        }
        
        ticketRepository.delete(ticket);
    }

    public Ticket getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));
        
        User currentUser = userService.getCurrentUser();
        
        // Check if user is authorized to view this ticket
        if (!isUserAuthorizedForTicket(currentUser, ticket)) {
            throw new RuntimeException("Not authorized to view this ticket");
        }
        
        return ticket;
    }

    public List<Ticket> getAllTickets() {
        User currentUser = userService.getCurrentUser();
        
        if (userService.isCurrentUserAgent()) {
            // Agents and admins can see all tickets
            return ticketRepository.findAllByOrderByCreatedAtDesc();
        } else if (currentUser != null) {
            // Regular users can only see their own tickets
            return ticketRepository.findByCreatedByOrderByCreatedAtDesc(currentUser);
        }
        
        throw new RuntimeException("User must be logged in to view tickets");
    }

    public List<Ticket> getTicketsByUser() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("User must be logged in");
        }
        return ticketRepository.findByCreatedByOrderByCreatedAtDesc(currentUser);
    }

    public List<Ticket> getTicketsByStatus(Ticket.Status status) {
        User currentUser = userService.getCurrentUser();
        
        if (userService.isCurrentUserAgent()) {
            return ticketRepository.findByStatusOrderByCreatedAtDesc(status);
        } else if (currentUser != null) {
            return ticketRepository.findByCreatedByAndStatus(currentUser, status);
        }
        
        throw new RuntimeException("User must be logged in");
    }

    public List<Ticket> getAssignedTickets() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("User must be logged in");
        }
        return ticketRepository.findByAssignedToOrderByCreatedAtDesc(currentUser);
    }

    public Ticket assignTicketToSelf(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + ticketId));
        
        User currentUser = userService.getCurrentUser();
        
        if (!userService.isCurrentUserAgent()) {
            throw new RuntimeException("Only agents can assign tickets");
        }
        
        ticket.setAssignedTo(currentUser);
        ticket.setStatus(Ticket.Status.IN_PROGRESS);
        return ticketRepository.save(ticket);
    }

    public Ticket unassignTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + ticketId));
        
        User currentUser = userService.getCurrentUser();
        
        if (!userService.isCurrentUserAgent()) {
            throw new RuntimeException("Only agents can unassign tickets");
        }
        
        ticket.setAssignedTo(null);
        ticket.setStatus(Ticket.Status.NEW);
        return ticketRepository.save(ticket);
    }

    public Ticket changeTicketStatus(Long ticketId, Ticket.Status status) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + ticketId));
        
        User currentUser = userService.getCurrentUser();
        
        if (!isUserAuthorizedForTicket(currentUser, ticket)) {
            throw new RuntimeException("Not authorized to change status of this ticket");
        }
        
        ticket.setStatus(status);
        return ticketRepository.save(ticket);
    }

    public List<Ticket> getUnassignedTickets() {
        if (!userService.isCurrentUserAgent()) {
            throw new RuntimeException("Only agents can view unassigned tickets");
        }
        return ticketRepository.findByAssignedToIsNull();
    }

    public long countTicketsByStatus(Ticket.Status status) {
        if (userService.isCurrentUserAgent()) {
            return ticketRepository.countByStatus(status);
        }
        User currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            return ticketRepository.countByCreatedByAndStatus(currentUser, status);
        }
        return 0;
    }

    public long countMyTickets() {
        User currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            return ticketRepository.countByCreatedBy(currentUser);
        }
        return 0;
    }

    public long countAssignedTickets() {
        User currentUser = userService.getCurrentUser();
        if (currentUser != null && userService.isCurrentUserAgent()) {
            return ticketRepository.countByAssignedTo(currentUser);
        }
        return 0;
    }

    private boolean isUserAuthorizedForTicket(User user, Ticket ticket) {
        if (user == null) {
            return false;
        }
        
        // Admins can access all tickets
        if (user.getRole() == User.Role.ADMIN) {
            return true;
        }
        
        // Agents can access all tickets
        if (user.getRole() == User.Role.AGENT) {
            return true;
        }
        
        // Users can only access their own tickets
        return ticket.getCreatedBy().getId().equals(user.getId());
    }
}
