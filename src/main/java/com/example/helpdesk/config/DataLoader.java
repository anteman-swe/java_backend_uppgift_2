package com.example.helpdesk.config;

import com.example.helpdesk.entity.Ticket;
import com.example.helpdesk.entity.User;
import com.example.helpdesk.repository.TicketRepository;
import com.example.helpdesk.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, 
                      TicketRepository ticketRepository, 
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Only load data if database is empty
        if (userRepository.count() == 0) {
            // Create ADMIN user
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .firstName("Admin")
                    .lastName("User")
                    .email("admin@helpdesk.com")
                    .role(User.Role.ADMIN)
                    .active(true)
                    .build();
            userRepository.save(admin);

            // Create AGENT user
            User agent = User.builder()
                    .username("agent")
                    .password(passwordEncoder.encode("agent123"))
                    .firstName("Support")
                    .lastName("Agent")
                    .email("agent@helpdesk.com")
                    .role(User.Role.AGENT)
                    .active(true)
                    .build();
            userRepository.save(agent);

            // Create USER
            User user = User.builder()
                    .username("user")
                    .password(passwordEncoder.encode("user123"))
                    .firstName("Regular")
                    .lastName("User")
                    .email("user@helpdesk.com")
                    .role(User.Role.USER)
                    .active(true)
                    .build();
            userRepository.save(user);

            // Create another USER
            User user2 = User.builder()
                    .username("user2")
                    .password(passwordEncoder.encode("user123"))
                    .firstName("Test")
                    .lastName("User")
                    .email("user2@helpdesk.com")
                    .role(User.Role.USER)
                    .active(true)
                    .build();
            userRepository.save(user2);

            // Create some sample tickets
            Ticket ticket1 = Ticket.builder()
                    .title("Cannot login to my account")
                    .description("I'm having trouble logging in to my account. The system keeps saying invalid credentials even though I'm sure I'm entering the correct password.")
                    .category("Authentication")
                    .priority(Ticket.Priority.HIGH)
                    .status(Ticket.Status.NEW)
                    .createdBy(user)
                    .createdAt(LocalDateTime.now().minusDays(1))
                    .build();
            ticketRepository.save(ticket1);

            Ticket ticket2 = Ticket.builder()
                    .title("Feature request: Dark mode")
                    .description("It would be great to have a dark mode option for the application. This would help reduce eye strain when working late at night.")
                    .category("Feature Request")
                    .priority(Ticket.Priority.LOW)
                    .status(Ticket.Status.NEW)
                    .createdBy(user2)
                    .createdAt(LocalDateTime.now().minusDays(2))
                    .build();
            ticketRepository.save(ticket2);

            Ticket ticket3 = Ticket.builder()
                    .title("Bug: Dashboard not loading")
                    .description("The dashboard page is not loading properly. I see a blank screen when I try to access it. This started happening after the last update.")
                    .category("Bug")
                    .priority(Ticket.Priority.CRITICAL)
                    .status(Ticket.Status.IN_PROGRESS)
                    .createdBy(user)
                    .assignedTo(agent)
                    .createdAt(LocalDateTime.now().minusHours(5))
                    .build();
            ticketRepository.save(ticket3);

            System.out.println("Sample data loaded successfully!");
            System.out.println("Admin user: admin/admin123");
            System.out.println("Agent user: agent/agent123");
            System.out.println("User 1: user/user123");
            System.out.println("User 2: user2/user123");
        }
    }
}
