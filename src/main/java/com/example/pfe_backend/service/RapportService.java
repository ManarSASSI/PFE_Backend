package com.example.pfe_backend.service;

import com.lowagie.text.Document;
import com.example.pfe_backend.repository.ContratRepository;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.pfe_backend.model.*;

import java.awt.*;
import java.io.ByteArrayOutputStream;

@Service
public class RapportService {

    @Autowired
    private ContratRepository contratRepository;

    public byte[] genererRapportContrat(Long contratId) throws DocumentException {
        Contrat contrat = contratRepository.findById(contratId)
                .orElseThrow(() -> new RuntimeException("Contrat non trouvé"));

        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        document.open();

        // Titre
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Rapport de Contrat #" + contrat.getId(), titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // Détails du contrat
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Type: " + contrat.getTypeContrat()));
        document.add(new Paragraph("Objet: " + contrat.getObjetContrat()));
        document.add(new Paragraph("Statut: " + contrat.getStatus()));

        // Tableau des suivis
        PdfPTable table = new PdfPTable(3);
        table.addCell("Date");
        table.addCell("Action");
        table.addCell("Commentaire");

        contrat.getSuivis().forEach(suivi -> {
            table.addCell(suivi.getDateSuivi().toString());
            table.addCell(suivi.getAction());
            table.addCell(suivi.getCommentaire());
        });

        document.add(table);
        document.close();

        return baos.toByteArray();
    }
}
