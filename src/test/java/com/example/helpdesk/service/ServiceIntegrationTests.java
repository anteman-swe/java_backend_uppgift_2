package com.example.helpdesk.service;

import com.example.helpdesk.entity.Ticket;
import com.example.helpdesk.entity.User;
import com.example.helpdesk.repository.TicketRepository;
import com.example.helpdesk.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class ServiceIntegrationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Test
    void testUserCreationAndRetrieval() {
        User user = User.builder()
                .username("testuser")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .role(User.Role.USER)
                .active(true)
                .build();

        User created = userService.createUser(user);
        assertNotNull(created.getId());
        assertEquals("testuser", created.getUsername());

        User fetched = userService.getUserById(created.getId());
        assertEquals("test@example.com", fetched.getEmail());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testTicketCreationAndListing() {
        User admin = userService.getUserByUsername("admin");
        assertNotNull(admin);

        Ticket ticket = Ticket.builder()
                .title("Network outage")
                .description("Cannot reach the internal server")
                .category("Network")
                .priority(Ticket.Priority.HIGH)
                .build();

        Ticket created = ticketService.createTicket(ticket);
        assertNotNull(created.getId());
        assertEquals(Ticket.Status.NEW, created.getStatus());

        List<Ticket> allTickets = ticketService.getAllTickets();
        assertFalse(allTickets.isEmpty());
    }
}
