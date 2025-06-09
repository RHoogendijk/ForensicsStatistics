package com.statistics.statisticsbackend.controllers;


import com.statistics.statisticsbackend.models.PlaySession;
import com.statistics.statisticsbackend.models.User;
import com.statistics.statisticsbackend.services.PlaySessionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

    private static final String UPLOAD_DIR = "/app/uploads";
    private static final Logger logger = Logger.getLogger(FileUploadController.class.getName());
    private final PlaySessionService playSessionService;

    //TODO: move logic inside service
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("sessionId") Long sessionId, HttpServletRequest request) {
        logger.info("Received upload request");

        PlaySession currentSession = playSessionService.findById(sessionId);
        if (currentSession == null) {
            return new ResponseEntity<>("Session not found", HttpStatus.NOT_FOUND);
        }
        User user = currentSession.getUser();
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        if (file == null || file.isEmpty()) {
            logger.warning(" Uploaded file is null or empty");
            return ResponseEntity.badRequest().body("No file provided or file is empty.");
        }

        logger.info("File name: " + file.getOriginalFilename());
        logger.info("File size: " + file.getSize() + " bytes");
        logger.info("Content type: " + file.getContentType());

        try {
            // Ensure full absolute path and directory structure
            File baseUploadDir = new File(UPLOAD_DIR);

            if (!baseUploadDir.exists() && !baseUploadDir.mkdirs()) {
                logger.warning("Failed to create base upload directory: " + baseUploadDir.getAbsolutePath());
                return ResponseEntity.status(500).body("Failed to create base upload directory.");
            }

            // Build the directory structure: UPLOAD_DIR/userId/sessionId
            String userIdStr = user.getId().toString();
            String sessionIdStr = currentSession.getId().toString();
            File userSessionDir = new File(baseUploadDir, userIdStr + File.separator + sessionIdStr);

            // Determine if the file is a JPG by checking the lower-case extension
            String originalFileName = file.getOriginalFilename();
            String lowerName = originalFileName.toLowerCase();
            File destinationDir;
            if (lowerName.endsWith(".jpg") && lowerName.startsWith("photo_")) {
                // Append "img" subdirectory for JPG files
                destinationDir = new File(userSessionDir, "img");
            } else if (lowerName.startsWith("replay_")) {
                destinationDir = new File(userSessionDir, "replay");
            } else {
                return ResponseEntity.status(406).body("not a valid file");
            }

            // Ensure that the destination directory exists
            if (!destinationDir.exists() && !destinationDir.mkdirs()) {
                logger.warning("Failed to create destination directory: " + destinationDir.getAbsolutePath());
                return ResponseEntity.status(500).body("Failed to create destination directory.");
            }

            // Create the destination file (using the original file name)
            File dest = new File(destinationDir, originalFileName);
            logger.info("Saving file to: " + dest.getAbsolutePath());

            // Save the file to the destination path
            file.transferTo(dest);

            // Construct the public URL for the uploaded file.
            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            String fileUrl;
            if (lowerName.endsWith(".jpg") && lowerName.startsWith("photo_")) {
                //add photo to current session
                fileUrl = baseUrl + "/api/files/" + userIdStr + "/" + sessionIdStr + "/img/" + originalFileName;
                logger.info("Generated file URL: " + fileUrl);
                currentSession.addFileUrl(fileUrl);
            } else if (lowerName.startsWith("replay_")){
                fileUrl = baseUrl + "/api/files/" + userIdStr + "/" + sessionIdStr + "/replay/" + originalFileName;
                logger.info("Generated file URL: " + fileUrl);
                if (lowerName.contains("outside")) {
                    //add outside background image for replay to current session
                    currentSession.setOutsideBackgroundURL(fileUrl);
                } else if (lowerName.contains("basement")) {
                    //add basement background image for replay to current session
                    currentSession.setBasementBackgroundURL(fileUrl);
                } else if (lowerName.endsWith(".json")) {
                    //add replay json file to current session
                    currentSession.setReplayJsonURL(fileUrl);
                }
            }

            playSessionService.save(currentSession);
            logger.info("File uploaded successfully");
            return ResponseEntity.ok("File uploaded successfully: " + dest.getAbsolutePath());

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Upload failed with IOException", e);
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error during upload", e);
            return ResponseEntity.status(500).body("Unexpected error: " + e.getMessage());
        }
    }
}
