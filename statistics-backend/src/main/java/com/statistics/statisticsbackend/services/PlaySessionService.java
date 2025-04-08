package com.statistics.statisticsbackend.services;

import com.statistics.statisticsbackend.models.PlaySession;
import com.statistics.statisticsbackend.models.User;
import com.statistics.statisticsbackend.repositories.PlaySessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PlaySessionService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final PlaySessionRepository playSessionRepository;
    private final UserService userService;

    public PlaySession findById(Long id) {
        return playSessionRepository.findById(id).orElse(null);
    }

    public List<PlaySession> findAllByUserId(Long userId) {
        User user = userService.findById(userId);
        if (user == null) return null;

        return user.getPlaySessions();
    }

    public void save(PlaySession playSession) {
        logger.info("Saving play session: " + playSession);
        playSessionRepository.save(playSession);
        logger.info("Saved play session: " + playSession);
    }
}
