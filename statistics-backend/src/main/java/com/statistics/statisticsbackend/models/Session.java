package com.statistics.statisticsbackend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Session {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User user;

    @ElementCollection
    private List<String> fileUrls = new ArrayList<>();

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;

    @PrePersist
    protected void onCreate() {
        createdTime = new Date();
    }

    public void addFileUrl(String fileUrl) {
        fileUrls.add(fileUrl);
    }

    public void removeFileUrl(String fileUrl) {
        fileUrls.remove(fileUrl);
    }
}
