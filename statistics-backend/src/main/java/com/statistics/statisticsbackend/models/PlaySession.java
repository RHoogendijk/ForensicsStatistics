package com.statistics.statisticsbackend.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class PlaySession {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ElementCollection
    private List<String> fileUrls = new ArrayList<>();

    @ElementCollection
    private List<String> evidence = new ArrayList<>();

    private String basementBackgroundURL;

    private String outsideBackgroundURL;

    private String replayJsonURL;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;

    @PrePersist
    protected void onCreate() {
        createdTime = new Date();
    }

    public void addFileUrl(String fileUrl) {
        fileUrls.add(fileUrl);
    }

    public void addEvidence(String evidence) {this.evidence.add(evidence);}

    public void removeFileUrl(String fileUrl) {
        fileUrls.remove(fileUrl);
    }

    public void removeEvidence(String evidence) {this.evidence.remove(evidence);}
}
