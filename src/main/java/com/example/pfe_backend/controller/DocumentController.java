package com.example.pfe_backend.controller;


import com.example.pfe_backend.model.Document;
import com.example.pfe_backend.service.DocumentService;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping(value = "/upload/{contratId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadDocument(
            @PathVariable Long contratId,
            @RequestParam("file") MultipartFile file) {

        try {
            String filePath = documentService.storeFile(contratId, file);
            return ResponseEntity.ok("Document uploadé avec succès: " + filePath);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Échec de l'upload: " + e.getMessage());
        }
    }

    @GetMapping("/contrat/{contratId}")
    public ResponseEntity<List<Document>> getDocumentsByContrat(@PathVariable Long contratId) {
        List<Document> documents = documentService.getDocumentsByContrat(contratId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/download/{documentId}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long documentId) {
        try {
            Document document = documentService.getDocument(documentId)
                    .orElseThrow(() -> new RuntimeException("Document non trouvé"));

            Path filePath = Paths.get(document.getCheminStockage());
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("Le fichier n'existe pas ou n'est pas lisible");
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(document.getType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + document.getNom() + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @DeleteMapping("/{documentId}")
    @PreAuthorize("hasRole('ADMIN')") // Sécurité si nécessaire
    public ResponseEntity<?> deleteDocument(@PathVariable Long documentId) {
        try {
            documentService.deleteDocument(documentId);
            return ResponseEntity.ok().build();
        } catch (DocumentNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erreur lors de la suppression: " + e.getMessage());
        }
    }


}
