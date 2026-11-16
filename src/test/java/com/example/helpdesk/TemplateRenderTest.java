package com.example.helpdesk;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@org.springframework.transaction.annotation.Transactional
class TemplateRenderTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testRenderViewTicket() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@example.com");
        user.setRole(User.Role.USER);
        user = userRepository.save(user);

        Ticket ticket = new Ticket();
        ticket.setTitle("Sample Ticket");
        ticket.setDescription("Sample Description");
        ticket.setStatus(Ticket.Status.NEW);
        ticket.setPriority(Ticket.Priority.MEDIUM);
        ticket.setCreatedBy(user);
        ticket = ticketRepository.save(ticket);

        mockMvc.perform(get("/tickets/" + ticket.getId()))
                .andExpect(status().isOk());
    }
}
