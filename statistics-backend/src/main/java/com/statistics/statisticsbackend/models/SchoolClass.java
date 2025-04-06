package com.statistics.statisticsbackend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolClass {
    @Id
    @Getter
    @Column(unique = true)
    private String id;

    @OneToMany(mappedBy = "schoolClass")
    private List<User> users = new ArrayList<>();

    public void addUser(User user) {
        users.add(user);
        user.setSchoolClass(this);
    }

    public void removeUser(User user) {
        users.remove(user);
        user.setSchoolClass(null);
    }
}
