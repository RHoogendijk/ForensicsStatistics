package com.statistics.statisticsbackend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.statistics.statisticsbackend.security.SecureHasher;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "_user")
public class User {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(unique = true, nullable = false)
    private String fullName;

    @Getter
    @Setter
    @Column(unique = true, nullable = false)
    private String email;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private Role role;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name="school_class_id")
    @JsonIgnore
    private SchoolClass schoolClass;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Session> sessions = new ArrayList<>();

    @Getter
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Getter
    @Setter
    @JsonIgnore
    private byte[] salt;

    @Column(nullable = false)
    private String uploadCode;

    public User(){
        this.role = Role.STUDENT;
    }

    public void setPassword(String password) {
        this.password = hashPassword(password);
    }

    public String hashPassword(String password){
        return SecureHasher.secureHash(password, this.salt);
    }

    public boolean verifyPassword(String password){
        return this.password.equals(hashPassword(password));
    }

    public void addSession(Session session){
        this.sessions.add(session);
        session.setUser(this);
    }

    public void removeSession(Session session){
        this.sessions.remove(session);
        session.setUser(null);
    }

    @PrePersist
    private void generateUserCode() {
        if (this.uploadCode == null || this.uploadCode.isEmpty()) {
            this.uploadCode = UUID.randomUUID().toString().substring(0, 8); // Short UUID
        }
    }

}
