package com.example.pfe_backend.controller;

import com.example.pfe_backend.service.RapportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/rapports")
public class RapportController {
    @Autowired
    private RapportService rapportService;
    private static final Logger logger = LoggerFactory.getLogger(RapportController.class);


    @GetMapping(value = "/contrats/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> genererRapportContrat(@PathVariable Long id) {
        try {
            byte[] pdf = rapportService.genererRapportContrat(id);

            if (pdf == null || pdf.length == 0) {
                throw new IllegalStateException("PDF vide généré");
            }

            // Création des headers avec les éléments critiques
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentLength(pdf.length);
            headers.setContentDisposition(
                    ContentDisposition.builder("inline")
                            .filename("rapport-contrat-" + id + ".pdf")
                            .build()
            );
            headers.set("Accept-Ranges", "bytes");
            headers.setAccessControlExposeHeaders(Arrays.asList("Content-Disposition"));

            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Erreur génération PDF pour contrat ID {} : {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Erreur génération PDF: " + e.getMessage());
        }
    }
//    public ResponseEntity<?> genererRapportContrat(@PathVariable Long id) {
//        try {
//            byte[] pdf = rapportService.genererRapportContrat(id);
//
//            // Validation renforcée
//            if (pdf == null || pdf.length == 0) {
//                throw new IllegalStateException("PDF vide généré");
//            }
//
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION,
//                            "attachment; filename=rapport-contrat-" + id + ".pdf")
//                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE) // Ajout crucial
////                    .contentType(MediaType.APPLICATION_PDF)
//                    .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
//                    .cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES))
//                    .body(pdf);
//
//        } catch (Exception e) {
//            logger.error("Erreur génération PDF pour contrat ID {} : {}", id, e.getMessage(), e);
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .contentType(MediaType.APPLICATION_JSON) // Type cohérent pour les erreurs
////                    .contentType(MediaType.TEXT_PLAIN) // Type correct pour les erreurs
//                    .body("Erreur génération PDF: " + e.getMessage());
//        }
//    }


//    public ResponseEntity<byte[]> genererRapportContrat(@PathVariable Long id) {
//        try {
//            byte[] pdf = rapportService.genererRapportContrat(id);
//
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rapport-contrat-" + id + ".pdf")
//                    .body(pdf);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(("Erreur : " + e.getMessage()).getBytes(StandardCharsets.UTF_8));
//
////            return ResponseEntity.internalServerError().build();
//        }
//    }
}
