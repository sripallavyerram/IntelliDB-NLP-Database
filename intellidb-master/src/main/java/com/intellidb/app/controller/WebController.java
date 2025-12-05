package com.intellidb.app.controller;

import com.intellidb.app.dto.ConnectionDetails;
import com.intellidb.app.dto.SchemaDetails;
import com.intellidb.app.model.SavedConnection;
import com.intellidb.app.model.User;
import com.intellidb.app.repository.QueryHistoryRepository; // Import the new repository
import com.intellidb.app.repository.SavedConnectionRepository;
import com.intellidb.app.repository.UserRepository;
import com.intellidb.app.service.DatabaseService;
import com.intellidb.app.service.EncryptionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class WebController {

    // Use @Autowired to let Spring provide working instances
    @Autowired private DatabaseService databaseService;
    @Autowired private SavedConnectionRepository savedConnectionRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private EncryptionService encryptionService;
    @Autowired private QueryHistoryRepository queryHistoryRepository; // CORRECT: Injected here

    // In WebController.java

    @GetMapping("/dashboard")
    public String connectionsPage(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        List<SavedConnection> connections = savedConnectionRepository.findByUser(user);
        model.addAttribute("connections", connections);

        // --- NEW: Add data for the stat card ---
        model.addAttribute("totalConnections", connections.size());

        return "connections";
    }

    @GetMapping("/connections/add")
    public String addConnectionForm(Model model) {
        model.addAttribute("connectionDetails", new ConnectionDetails());
        return "add-connection";
    }

    @PostMapping("/connections/add")
    public String saveConnection(@ModelAttribute ConnectionDetails connectionDetails, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        SavedConnection newConnection = new SavedConnection();
        newConnection.setConnectionName(connectionDetails.getConnectionName());
        newConnection.setHost(connectionDetails.getHost());
        newConnection.setPort(connectionDetails.getPort());
        newConnection.setDatabaseName(connectionDetails.getDatabase());
        newConnection.setDbUsername(connectionDetails.getUsername());
        newConnection.setDbPassword(encryptionService.encrypt(connectionDetails.getPassword()));
        newConnection.setUser(user);

        savedConnectionRepository.save(newConnection);
        return "redirect:/";
    }

    @PostMapping("/connect/{id}")
    public String connectToSavedDatabase(@PathVariable Long id, HttpSession session, Model model, Authentication authentication) {
        SavedConnection savedConn = savedConnectionRepository.findById(id).orElseThrow(() -> new RuntimeException("Connection not found"));

        ConnectionDetails details = new ConnectionDetails();
        details.setConnectionName(savedConn.getConnectionName());
        details.setHost(savedConn.getHost());
        details.setPort(savedConn.getPort());
        details.setDatabase(savedConn.getDatabaseName());
        details.setUsername(savedConn.getDbUsername());
        details.setPassword(encryptionService.decrypt(savedConn.getDbPassword()));

        boolean isConnected = databaseService.testConnection(details);
        if (isConnected) {
            session.setAttribute("connectionDetails", details);
            List<String> tableNames = databaseService.getTables(details);
            SchemaDetails schemaDetails = new SchemaDetails(details.getDatabase(), tableNames);
            model.addAttribute("schemaDetails", schemaDetails);

            // Load query history using the autowired repository
            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
            model.addAttribute("queryHistory", queryHistoryRepository.findTop10ByUserOrderByExecutionTimestampDesc(user));

            return "dashboard";
        } else {
            return "redirect:/?error=true";
        }
    }

    // ... (Your other methods: deleteConnection, showEditForm, updateConnection) ...
    @PostMapping("/connections/delete/{id}")
    public String deleteConnection(@PathVariable Long id) {
        savedConnectionRepository.deleteById(id);
        return "redirect:/";
    }

    @GetMapping("/connections/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        SavedConnection savedConn = savedConnectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Connection not found"));

        ConnectionDetails details = new ConnectionDetails();
        details.setConnectionName(savedConn.getConnectionName());
        details.setHost(savedConn.getHost());
        details.setPort(savedConn.getPort());
        details.setDatabase(savedConn.getDatabaseName());
        details.setUsername(savedConn.getDbUsername());
        details.setPassword("");

        model.addAttribute("connectionDetails", details);
        model.addAttribute("connectionId", id);
        return "edit-connection";
    }

    @PostMapping("/connections/edit/{id}")
    public String updateConnection(@PathVariable Long id, @ModelAttribute ConnectionDetails connectionDetails) {
        SavedConnection savedConn = savedConnectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Connection not found"));

        savedConn.setConnectionName(connectionDetails.getConnectionName());
        savedConn.setHost(connectionDetails.getHost());
        savedConn.setPort(connectionDetails.getPort());
        savedConn.setDatabaseName(connectionDetails.getDatabase());
        savedConn.setDbUsername(connectionDetails.getUsername());

        if (connectionDetails.getPassword() != null && !connectionDetails.getPassword().isEmpty()) {
            savedConn.setDbPassword(encryptionService.encrypt(connectionDetails.getPassword()));
        }

        savedConnectionRepository.save(savedConn);
        return "redirect:/dashboard";
    }
}