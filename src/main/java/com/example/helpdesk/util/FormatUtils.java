package com.example.helpdesk.util;

import com.example.helpdesk.entity.Ticket;
import com.example.helpdesk.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component("formatUtils")
public class FormatUtils {

    // Prettier texts for roles
    public String formatRole(User.Role role) {
        if (role == null) return "Unknown";
        return switch (role) {
            case ADMIN -> "Administrator";
            case AGENT -> "Supportagent";
            case USER -> "User";
        };
    }

    // Connect the right Tailwind classes to the role
    public String getRoleBadgeClass(User.Role role) {
        if (role == null) return "bg-gray-100 text-gray-800";
        return switch (role) {
            case ADMIN -> "bg-red-100 text-red-800";
            case AGENT -> "bg-cyan-100 text-cyan-800";
            default -> "bg-gray-100 text-gray-800";
        };
    }

    public String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) return "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return dateTime.format(formatter);
    }

    // Connect the right Tailwind classes to a ticket status pill
    public String getTicketStatusClass(Ticket.Status status) {
        if (status == null) return "bg-gray-100 text-gray-800";
        return switch (status) {
            case  NEW-> "bg-blue-100 text-blue-800";
            case IN_PROGRESS -> "bg-yellow-100 text-yellow-800";
            case WAITING_FOR_USER -> "bg-cyan-100 text-cyan-800";
            case RESOLVED -> "bg-green-100 text-green-800";
            case CLOSED -> "bg-gray-100 text-gray-800";
            default -> "bg-gray-100 text-gray-700";
        };
    }

    // Connect the right Tailwind classes to a ticket status pill
    public String getTicketPriorityClass(Ticket.Priority priority) {
        if (priority == null) return "bg-gray-100 text-gray-800";
        return switch (priority) {
            case LOW -> "bg-green-100 text-green-800";
            case MEDIUM -> "bg-yellow-100 text-yellow-800";
            case HIGH -> "bg-red-100 text-red-800";
            case CRITICAL -> "bg-red-300 text-red-800";
            default -> "bg-gray-100 text-gray-800";
        };
    }
}
