package com.statistics.statisticsbackend.controllers;


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
    private static final Logger logger = Logger.getLogger(SchoolClassController.class.getName());

    //TODO: receive multiple images at once along with a upload code and create a session for the user
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        logger.info("📩 Received upload request");

        if (file == null || file.isEmpty()) {
            logger.warning("⚠️ Uploaded file is null or empty");
            return ResponseEntity.badRequest().body("No file provided or file is empty.");
        }

        logger.info("📄 File name: " + file.getOriginalFilename());
        logger.info("📏 File size: " + file.getSize() + " bytes");
        logger.info("🧠 Content type: " + file.getContentType());

        try {
            // Ensure full absolute path and directory structure
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                logger.info("📂 Creating upload directory: " + uploadDir.getAbsolutePath());
                if (!uploadDir.mkdirs()) {
                    logger.warning("❌ Failed to create upload directory!");
                    return ResponseEntity.status(500).body("Failed to create upload directory.");
                }
            }

            // Create destination file path
            File dest = new File(uploadDir, file.getOriginalFilename());

            // Just in case, ensure parent directory of dest exists
            File parent = dest.getParentFile();
            if (!parent.exists()) {
                logger.info("📁 Creating parent directories: " + parent.getAbsolutePath());
                parent.mkdirs();
            }

            logger.info("💾 Saving file to: " + dest.getAbsolutePath());

            file.transferTo(dest);

            logger.info("✅ File uploaded successfully");
            return ResponseEntity.ok("File uploaded successfully: " + dest.getAbsolutePath());

        } catch (IOException e) {
            logger.log(Level.SEVERE, "❌ Upload failed with IOException", e);
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Unexpected error during upload", e);
            return ResponseEntity.status(500).body("Unexpected error: " + e.getMessage());
        }
    }

    //TODO: only retrieve images belonging to one session, check for user authentication
    @GetMapping("/images")
    public ResponseEntity<List<String>> listAllImages(HttpServletRequest request) {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists() || !uploadDir.isDirectory()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of());
        }

        File[] files = uploadDir.listFiles((dir, name) -> {
            String lower = name.toLowerCase();
            return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".gif");
        });

        if (files == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }

        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

        List<String> imageUrls = Arrays.stream(files)
                .map(File::getName)
                .map(name -> baseUrl + "/api/images/" + name) // Ensure URL points to /api/images/
                .collect(Collectors.toList());

        return ResponseEntity.ok(imageUrls);
    }
}
