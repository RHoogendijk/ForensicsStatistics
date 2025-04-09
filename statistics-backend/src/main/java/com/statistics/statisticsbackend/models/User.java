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

    @Getter
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<PlaySession> playSessions = new ArrayList<>();

    @Getter
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Getter
    @Setter
    @JsonIgnore
    private byte[] salt;

    @Getter
    @Column(unique = true, nullable = false)
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


    public void addSession(PlaySession playSession){
        this.playSessions.add(playSession);
        playSession.setUser(this);
    }

    public void removeSession(PlaySession playSession){
        this.playSessions.remove(playSession);
        playSession.setUser(null);
    }

    @PrePersist
    private void generateUserCode() {
        if (this.uploadCode == null || this.uploadCode.isEmpty()) {
            this.uploadCode = UUID.randomUUID().toString().substring(0, 8); // Short UUID
        }
    }

}
