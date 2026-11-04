package com.example.helpdesk.service;

import com.example.helpdesk.entity.Comment;
import com.example.helpdesk.entity.Ticket;
import com.example.helpdesk.entity.User;
import com.example.helpdesk.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketService ticketService;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository, TicketService ticketService, UserService userService) {
        this.commentRepository = commentRepository;
        this.ticketService = ticketService;
        this.userService = userService;
    }

    public Comment createComment(Long ticketId, String content) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("User must be logged in to add a comment");
        }
        
        Ticket ticket = ticketService.getTicketById(ticketId);
        
        // Check if user is authorized to comment on this ticket
        if (!isUserAuthorizedForTicket(currentUser, ticket)) {
            throw new RuntimeException("Not authorized to comment on this ticket");
        }
        
        Comment comment = Comment.builder()
                .content(content)
                .user(currentUser)
                .ticket(ticket)
                .build();
        
        return commentRepository.save(comment);
    }

    public Comment updateComment(Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));
        
        User currentUser = userService.getCurrentUser();
        
        // Only the comment author can update it
        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Not authorized to update this comment");
        }
        
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));
        
        User currentUser = userService.getCurrentUser();
        
        // Only the comment author or an admin can delete it
        if (!comment.getUser().getId().equals(currentUser.getId()) && 
            currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Not authorized to delete this comment");
        }
        
        commentRepository.delete(comment);
    }

    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));
    }

    public List<Comment> getCommentsByTicket(Long ticketId) {
        Ticket ticket = ticketService.getTicketById(ticketId);
        return commentRepository.findByTicketOrderByCreatedAtAsc(ticket);
    }

    public List<Comment> getCommentsByUser() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("User must be logged in");
        }
        return commentRepository.findByUserOrderByCreatedAtDesc(currentUser);
    }

    private boolean isUserAuthorizedForTicket(User user, Ticket ticket) {
        // Admins can comment on all tickets
        if (user.getRole() == User.Role.ADMIN) {
            return true;
        }
        
        // Agents can comment on all tickets
        if (user.getRole() == User.Role.AGENT) {
            return true;
        }
        
        // Users can only comment on their own tickets
        return ticket.getCreatedBy().getId().equals(user.getId());
    }
}
