package com.example.pfe_backend.controller;
import com.example.pfe_backend.model.*;

import com.example.pfe_backend.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    @Autowired
    private AlertService alertService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Alert>> getUserAlerts(@PathVariable Long userId) {
        List<Alert> alerts = alertService.getAlertsForUser(userId);
        return ResponseEntity.ok(alerts);
    }

    @PostMapping("/{alertId}/mark-as-read")
    public ResponseEntity<Void> markAlertAsRead(@PathVariable Long alertId) {
        alertService.markAsRead(alertId);
        return ResponseEntity.ok().build();
    }

}
