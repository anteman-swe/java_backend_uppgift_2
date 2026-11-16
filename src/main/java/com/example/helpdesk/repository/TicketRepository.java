package com.example.helpdesk.repository;

import com.example.helpdesk.entity.Ticket;
import com.example.helpdesk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByCreatedBy(User createdBy);

    List<Ticket> findByAssignedTo(User assignedTo);

    List<Ticket> findByStatus(Ticket.Status status);

    List<Ticket> findByCreatedByAndStatus(User createdBy, Ticket.Status status);

    List<Ticket> findByAssignedToAndStatus(User assignedTo, Ticket.Status status);

    List<Ticket> findByStatusOrderByCreatedAtDesc(Ticket.Status status);

    List<Ticket> findAllByOrderByCreatedAtDesc();

    List<Ticket> findByCreatedByOrderByCreatedAtDesc(User createdBy);

    List<Ticket> findByAssignedToOrderByCreatedAtDesc(User assignedTo);

    List<Ticket> findByAssignedToIsNull();

    long countByStatus(Ticket.Status status);

    long countByCreatedBy(User createdBy);

    long countByAssignedTo(User assignedTo);

    long countByCreatedByAndStatus(User createdBy, Ticket.Status status);
}
