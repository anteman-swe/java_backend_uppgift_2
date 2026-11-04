package com.example.helpdesk.repository;

import com.example.helpdesk.entity.Comment;
import com.example.helpdesk.entity.Ticket;
import com.example.helpdesk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByTicket(Ticket ticket);

    List<Comment> findByUser(User user);

    List<Comment> findByTicketOrderByCreatedAtAsc(Ticket ticket);

    List<Comment> findByUserOrderByCreatedAtDesc(User user);
}
