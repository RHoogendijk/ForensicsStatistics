package com.statistics.statisticsbackend.repositories;

import com.statistics.statisticsbackend.models.PlaySession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaySessionRepository extends JpaRepository<PlaySession, Long> {
}
