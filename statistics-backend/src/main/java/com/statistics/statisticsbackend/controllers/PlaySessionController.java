package com.statistics.statisticsbackend.controllers;

import com.statistics.statisticsbackend.DTOs.PlaySessionCreatedDTO;
import com.statistics.statisticsbackend.DTOs.PlaySessionDTO;
import com.statistics.statisticsbackend.DTOs.SessionContentDTO;
import com.statistics.statisticsbackend.exceptions.UnauthorizedException;
import com.statistics.statisticsbackend.models.PlaySession;
import com.statistics.statisticsbackend.models.Role;
import com.statistics.statisticsbackend.models.User;
import com.statistics.statisticsbackend.security.JWToken;
import com.statistics.statisticsbackend.services.PlaySessionService;
import com.statistics.statisticsbackend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class PlaySessionController {

    Logger logger = Logger.getLogger(PlaySessionController.class.getName());

    private final UserService userService;
    private final PlaySessionService playSessionService;

    //TODO: try catch
    @PostMapping("")
    public ResponseEntity<PlaySessionCreatedDTO> createSession(@RequestBody String uploadCode) {
        User user = userService.findByUploadCode(uploadCode);
        PlaySession session = new PlaySession();
        user.addSession(session);
        playSessionService.save(session);

        PlaySessionCreatedDTO createdDTO = new PlaySessionCreatedDTO(session.getId(), user.getFullName().split(" ")[0]);
        return ResponseEntity.ok(createdDTO);
    }

    @GetMapping("{id}")
    public ResponseEntity<SessionContentDTO> getSession(@PathVariable Long id, @RequestAttribute(name = JWToken.JWT_ATTRIBUTE_NAME) JWToken jwtInfo) {
        if (jwtInfo == null) {
            logger.warning("Unauthorized access attempt: Missing JWT token.");
            throw new UnauthorizedException("JWT token is required.");
        }
        PlaySession session = playSessionService.findById(id);
        if (session == null) {
            return ResponseEntity.notFound().build();
        }
        Long userId = session.getUser().getId();
        if (jwtInfo.getRole() != Role.ADMIN  && jwtInfo.getRole() != Role.TEACHER && !Objects.equals(jwtInfo.getUserId(), userId)) {
            logger.warning("Unauthorized access attempt");
            throw new UnauthorizedException("No access to this user's sessions");
        }

        List<String> fileURLs = session.getFileUrls();
        String outsideBackgroundURL = session.getOutsideBackgroundURL();
        String basementBackgroundURL = session.getBasementBackgroundURL();
        String jsonURL = session.getReplayJsonURL();
        return ResponseEntity.ok(new SessionContentDTO(outsideBackgroundURL, basementBackgroundURL, jsonURL, fileURLs));
    }
}
