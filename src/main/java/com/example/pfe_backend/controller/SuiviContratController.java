package com.example.pfe_backend.controller;

import com.example.pfe_backend.model.SuiviContrat;
import com.example.pfe_backend.model.User;
import com.example.pfe_backend.service.SuiviContratService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suivi-contrats")
public class SuiviContratController {
    @Autowired
    private SuiviContratService suiviService;

    @PostMapping("/{contratId}")
    public ResponseEntity<Void> ajouterSuivi(
            @PathVariable Long contratId,
            @RequestBody SuiviContrat suivi) {

        suiviService.ajouterSuivi(contratId, suivi);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{contratId}/historique")
    public ResponseEntity<List<SuiviContrat>> getHistorique(@PathVariable Long contratId) {
        List<SuiviContrat> historique = suiviService.getHistorique(contratId);
        return ResponseEntity.ok(historique);
    }

    @GetMapping
    public ResponseEntity<List<SuiviContrat>> getAllSuiviContrats() {
        List<SuiviContrat> suiviContrats = suiviService.getAllSuiviContrats();
        return new ResponseEntity<>(suiviContrats, HttpStatus.OK);
    }

}
