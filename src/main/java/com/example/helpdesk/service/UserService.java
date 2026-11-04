package com.example.helpdesk.service;

import com.example.helpdesk.entity.User;
import com.example.helpdesk.repository.UserRepository;
import com.example.helpdesk.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());
        user.setActive(userDetails.isActive());
        
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersByRole(User.Role role) {
        return userRepository.findByRole(role);
    }

    public List<User> getActiveUsers() {
        return userRepository.findByActive(true);
    }

    public List<User> getInactiveUsers() {
        return userRepository.findByActive(false);
    }

    public void changeUserStatus(Long id, boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setActive(active);
        userRepository.save(user);
    }

    public void changeUserRole(Long id, User.Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setRole(role);
        userRepository.save(user);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUser();
        }
        
        return null;
    }

    public boolean isCurrentUserAdmin() {
        User user = getCurrentUser();
        return user != null && user.getRole() == User.Role.ADMIN;
    }

    public boolean isCurrentUserAgent() {
        User user = getCurrentUser();
        return user != null && (user.getRole() == User.Role.AGENT || user.getRole() == User.Role.ADMIN);
    }

    public boolean isCurrentUser(User user) {
        User currentUser = getCurrentUser();
        return currentUser != null && currentUser.getId().equals(user.getId());
    }

    public boolean canUserAccessTicket(User user, Long ticketId) {
        // Implementation will be in TicketService
        return true;
    }
}
