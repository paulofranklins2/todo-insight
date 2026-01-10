package org.duckdns.todosummarized.controller;

import org.duckdns.todosummarized.domains.enums.TaskPriority;
import org.duckdns.todosummarized.domains.enums.TaskStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for metadata endpoints.
 * Provides enum values and labels for UI dropdowns.
 */
@RestController
@RequestMapping("/api/meta")
public class MetaController {

    /**
     * Get all task statuses with display labels.
     */
    @GetMapping("/statuses")
    public ResponseEntity<List<Map<String, String>>> getStatuses() {
        List<Map<String, String>> statuses = Arrays.stream(TaskStatus.values())
                .map(s -> Map.of(
                        "value", s.name(),
                        "label", formatEnumLabel(s.name())
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(statuses);
    }

    /**
     * Get all task priorities with display labels.
     */
    @GetMapping("/priorities")
    public ResponseEntity<List<Map<String, String>>> getPriorities() {
        List<Map<String, String>> priorities = Arrays.stream(TaskPriority.values())
                .map(p -> Map.of(
                        "value", p.name(),
                        "label", formatEnumLabel(p.name())
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(priorities);
    }

    /**
     * Converts ENUM_NAME to "Enum Name" format.
     */
    private String formatEnumLabel(String enumName) {
        return Arrays.stream(enumName.split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}

