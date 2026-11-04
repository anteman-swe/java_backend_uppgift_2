package com.example.helpdesk.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.NEW;

    @Column(name = "priority", length = 20)
    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.MEDIUM;

    @Column(name = "category", length = 50)
    private String category;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "last_modified")
    private LocalDateTime lastModified;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private Set<Comment> comments = new HashSet<>();

    public enum Status {
        NEW, IN_PROGRESS, WAITING_FOR_USER, RESOLVED, CLOSED
    }

    public enum Priority {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setTicket(this);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setTicket(null);
    }

    @Override
    public String toString() {
        return "Ticket{id=" + id + ", title='" + title + "', status=" + status + "}";
    }
}
