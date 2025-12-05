package com.intellidb.app.controller;

import com.intellidb.app.dto.ConnectionDetails;
import com.intellidb.app.model.QueryHistory;
import com.intellidb.app.model.User;
import com.intellidb.app.repository.QueryHistoryRepository;
import com.intellidb.app.repository.UserRepository;
import com.intellidb.app.service.DatabaseService;
import com.intellidb.app.service.GeminiAiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
public class QueryController {

    @Autowired private DatabaseService databaseService;
    @Autowired private GeminiAiService geminiAiService;
    @Autowired private QueryHistoryRepository queryHistoryRepository;
    @Autowired private UserRepository userRepository;

    @PostMapping("/api/query")
    public ResponseEntity<Map<String, Object>> handleQuery(@RequestBody Map<String, String> payload, HttpSession session, Authentication authentication) {
        String question = payload.get("question");
        ConnectionDetails details = (ConnectionDetails) session.getAttribute("connectionDetails");
        if (details == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No active database connection."));
        }

        String schema = databaseService.getSchemaDefinition(details);
        if (schema.isEmpty()) {
            return ResponseEntity.status(500).body(Map.of("error", "Could not retrieve database schema."));
        }

        String generatedSql = geminiAiService.generateSql(schema, question);

        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        QueryHistory historyItem = new QueryHistory();
        historyItem.setNaturalQuery(question);
        historyItem.setGeneratedSql(generatedSql);
        historyItem.setExecutionTimestamp(LocalDateTime.now());
        historyItem.setUser(user);
        QueryHistory savedHistoryItem = queryHistoryRepository.save(historyItem);

        Map<String, Object> response = new HashMap<>();
        response.put("generatedSql", generatedSql);

        Map<String, String> newHistoryItemMap = Map.of(
                "naturalQuery", savedHistoryItem.getNaturalQuery(),
                "executionTimestamp", savedHistoryItem.getExecutionTimestamp().format(DateTimeFormatter.ofPattern("MMM-dd HH:mm"))
        );
        response.put("newHistoryItem", newHistoryItemMap);

        if (generatedSql.trim().toUpperCase().startsWith("SELECT")) {
            Map<String, Object> results = databaseService.executeQuery(details, generatedSql);
            response.putAll(results);
            return ResponseEntity.ok(response);
        } else {
            response.put("requiresConfirmation", true);
            response.put("warning", "This query is not a 'SELECT' statement and may modify or delete data. Please review it carefully before executing.");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/api/execute")
    public ResponseEntity<Map<String, Object>> executeConfirmedQuery(@RequestBody Map<String, String> payload, HttpSession session) {
        String sql = payload.get("sql");
        ConnectionDetails details = (ConnectionDetails) session.getAttribute("connectionDetails");
        if (details == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No active database connection."));
        }
        Map<String, Object> result = databaseService.executeModificationQuery(details, sql);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/api/table-preview")
    public ResponseEntity<Map<String, Object>> getTablePreview(@RequestBody Map<String, String> payload, HttpSession session) {
        String tableName = payload.get("tableName");
        ConnectionDetails details = (ConnectionDetails) session.getAttribute("connectionDetails");
        if (details == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No active database connection."));
        }
        Map<String, Object> result = databaseService.getTablePreview(details, tableName);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/api/test-connection")
    public ResponseEntity<Map<String, String>> testConnection(@RequestBody ConnectionDetails details) {
        boolean isConnected = databaseService.testConnection(details);
        if (isConnected) {
            return ResponseEntity.ok(Map.of("status", "success", "message", "Connection successful!"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Connection failed. Please check credentials."));
        }
    }
}