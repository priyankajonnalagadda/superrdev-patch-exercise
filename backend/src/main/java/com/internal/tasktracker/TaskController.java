package com.internal.tasktracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping("/api/tasks")
    public ResponseEntity<?> searchTasks(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize) {

        // Normalize query input
        String query = q == null ? "" : q.trim();
        String searchTerm = "%" + query.toLowerCase() + "%";

        // Parse status filter
        String normalizedStatus = null;

        if (status != null && !status.isBlank()) {
            try {
                normalizedStatus = TaskStatus.valueOf(
                        status.trim().toUpperCase()
                ).name();
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "error",
                                "Invalid status: " + status
                        ));
            }
        }

        // Validate pagination
        if (page < 1 || pageSize < 1 || pageSize > 100) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error",
                            "page must be >= 1 and pageSize must be between 1 and 100"
                    ));
        }

        // Structured application logging
        log.info(
                "Task search q={} status={} page={} pageSize={}",
                query,
                normalizedStatus,
                page,
                pageSize
        );

        // Search tasks
        List<Task> allResults =
                taskRepository.searchTasks(searchTerm, normalizedStatus);

        // Safe pagination calculation
        long startLong = (long) (page - 1) * pageSize;

        List<Task> pageResults;

        if (startLong >= allResults.size()) {
            pageResults = Collections.emptyList();
        } else {
            int start = (int) startLong;
            int end = Math.min(start + pageSize, allResults.size());

            pageResults = allResults.subList(start, end);
        }

        // Build response
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("items", pageResults);
        response.put("total", allResults.size());
        response.put("page", page);
        response.put("pageSize", pageSize);

        // Prevent stale API responses
        return ResponseEntity.ok()
                .header(
                        "Cache-Control",
                        "no-store, no-cache, must-revalidate"
                )
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .body(response);
    }
}