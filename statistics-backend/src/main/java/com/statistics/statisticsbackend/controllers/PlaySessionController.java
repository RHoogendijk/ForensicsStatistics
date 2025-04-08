package com.statistics.statisticsbackend.controllers;

import com.statistics.statisticsbackend.DTOs.PlaySessionCreatedDTO;
import com.statistics.statisticsbackend.models.PlaySession;
import com.statistics.statisticsbackend.models.User;
import com.statistics.statisticsbackend.services.PlaySessionService;
import com.statistics.statisticsbackend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class PlaySessionController {

    private final UserService userService;
    private final PlaySessionService playSessionService;

    @PostMapping("")
    public ResponseEntity<PlaySessionCreatedDTO> createSession(@RequestBody String uploadCode) {
        User user = userService.findByUploadCode(uploadCode);
        PlaySession session = new PlaySession();
        user.addSession(session);
        playSessionService.save(session);

        PlaySessionCreatedDTO createdDTO = new PlaySessionCreatedDTO(session.getId(), user.getFullName().split(" ")[0]);
        return ResponseEntity.ok(createdDTO);
    }
}
