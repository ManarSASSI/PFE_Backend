package com.example.pfe_backend.service;

import com.lowagie.text.Document;
import com.example.pfe_backend.repository.ContratRepository;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.pfe_backend.model.*;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;

@Service
public class RapportService {

    // Couleurs professionnelles
    private static final Color HEADER_COLOR = new Color(13, 110, 253); // Bleu Bootstrap
    private static final Color LIGHT_GRAY = new Color(248, 249, 250); // Gris clair
    private static final Color DARK_GRAY = new Color(52, 58, 64); // Gris foncé
    private static final Color ACCENT_COLOR = new Color(108, 117, 125); // Gris moyen


    @Autowired
    private ContratRepository contratRepository;
    @Autowired
    private UserService userService;

    public byte[] genererRapportContrat(Long contratId) {
        try {
            Contrat contrat = contratRepository.findById(contratId)
                    .orElseThrow(() -> new RuntimeException("Contrat ID " + contratId + " non trouvé"));

            Document document = new Document(PageSize.A4, 36, 36, 72, 36); // Marges
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);

            // Activer la compression pour réduire la taille du fichier
            writer.setCompressionLevel(9);
            document.open();

            // Ajouter le header avec logo
            addHeader(document);

            // Titre principal
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, DARK_GRAY);
            Paragraph title = new Paragraph("RAPPORT DE CONTRAT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            // Section: Informations générales
            addSectionTitle(document, "INFORMATIONS GÉNÉRALES");
            addGeneralInfoTable(document, contrat);

            // Section: Détails du contrat
            addSectionTitle(document, "DÉTAILS DU CONTRAT");
            addContractDetailsTable(document, contrat);

            // Section: Suivis
            if (!contrat.getSuivis().isEmpty()) {
                addSectionTitle(document, "HISTORIQUE DES SUIVIS");
                addSuivisTable(document, contrat);
            }

            // Pied de page
            addFooter(writer, document);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

    private void addHeader(Document document) throws DocumentException, IOException {
        // Logo (remplacer par votre propre logo)
        try {
            Image logo = Image.getInstance(new URL("https://via.placeholder.com/150x50?text=Company+Logo"));
            logo.scaleToFit(150, 50);
            logo.setAlignment(Image.ALIGN_CENTER);
            document.add(logo);
        } catch (Exception e) {
            // Fallback si le logo n'est pas disponible
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, HEADER_COLOR);
            Paragraph fallbackHeader = new Paragraph("LEONI", headerFont);
            fallbackHeader.setAlignment(Element.ALIGN_CENTER);
            document.add(fallbackHeader);
        }

        // Ligne de séparation
        addSeparator(document);
    }

    private void addFooter(PdfWriter writer, Document document) {
        // Créer une zone de pied de page
        PdfContentByte cb = writer.getDirectContent();
        ColumnText.showTextAligned(
                cb, Element.ALIGN_CENTER,
                new Phrase("Page " + writer.getPageNumber(), FontFactory.getFont(FontFactory.HELVETICA, 10, ACCENT_COLOR)),
                (document.right() - document.left()) / 2 + document.leftMargin(),
                document.bottom() - 20, 0
        );

        ColumnText.showTextAligned(
                cb, Element.ALIGN_CENTER,
                new Phrase("Généré le: " + java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        FontFactory.getFont(FontFactory.HELVETICA, 10, ACCENT_COLOR)),
                (document.right() - document.left()) / 2 + document.leftMargin(),
                document.bottom() - 35, 0
        );
    }

    private void addSectionTitle(Document document, String titleText) throws DocumentException {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, DARK_GRAY);
        Paragraph section = new Paragraph(titleText, sectionFont);
        section.setSpacingBefore(15f);
        section.setSpacingAfter(10f);
        document.add(section);
    }

    private void addSeparator(Document document) throws DocumentException {
        LineSeparator line = new LineSeparator();
        line.setLineWidth(1f);
        line.setLineColor(ACCENT_COLOR);
        document.add(new Chunk(line));
        document.add(Chunk.NEWLINE);
    }

    private void addGeneralInfoTable(Document document, Contrat contrat) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(5f);
        table.setSpacingAfter(15f);

        // En-têtes de colonnes
        addTableHeader(table, "Champ");
        addTableHeader(table, "Valeur");

        // Contenu
        addTableRow(table, "ID Contrat", contrat.getId().toString());
        addTableRow(table, "Type", contrat.getTypeContrat().toString());
        addTableRow(table, "Statut", contrat.getStatus().toString());

        document.add(table);
    }

    private void addContractDetailsTable(Document document, Contrat contrat) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(5f);
        table.setSpacingAfter(15f);

        addTableRow(table, "Objet", contrat.getObjetContrat());
        addTableRow(table, "Partenaire", contrat.getPartner().getUsername());
        addTableRow(table, "Manager", userService.getUserById(contrat.getCreatedById()).getUsername());
        addTableRow(table, "Montant", String.format("%.2f DT", contrat.getMontant()));
        addTableRow(table, "Département", contrat.getDepartement().toString());
        addTableRow(table, "État d'exécution", contrat.getEtatExecution().toString());
        addTableRow(table, "Pénalité par jours", String.format("%.2f DT", contrat.getPenaliteParJour()));
//        addTableRow(table, "Nombre de jours de retard", String.format("",contrat.getJoursRetard()));



        document.add(table);
    }

    private void addSuivisTable(Document document, Contrat contrat) throws DocumentException {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setSpacingBefore(5f);
        table.setSpacingAfter(15f);

        float[] columnWidths = {2f, 3f, 5f};
        table.setWidths(columnWidths);

        // En-tête du tableau
        addTableHeader(table, "Date");
        addTableHeader(table, "Action");
        addTableHeader(table, "Commentaire");

        // Contenu
        contrat.getSuivis().forEach(suivi -> {
            addTableCell(table, suivi.getDateSuivi().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            addTableCell(table, suivi.getAction());
            addTableCell(table, suivi.getCommentaire());
        });

        document.add(table);
    }

    private void addTableHeader(PdfPTable table, String text) {
        PdfPCell header = new PdfPCell(new Phrase(text,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE)));
        header.setBackgroundColor(HEADER_COLOR);
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setPadding(8f);
        table.addCell(header);
    }

    private void addTableRow(PdfPTable table, String label, String value) {
        // Cellule de label
        PdfPCell labelCell = new PdfPCell(new Phrase(label,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, DARK_GRAY)));
        labelCell.setBackgroundColor(LIGHT_GRAY);
        labelCell.setPadding(6f);
        table.addCell(labelCell);

        // Cellule de valeur
        PdfPCell valueCell = new PdfPCell(new Phrase(value,
                FontFactory.getFont(FontFactory.HELVETICA, 11, DARK_GRAY)));
        valueCell.setPadding(6f);
        table.addCell(valueCell);
    }

    private void addTableCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text,
                FontFactory.getFont(FontFactory.HELVETICA, 10, DARK_GRAY)));
        cell.setPadding(5f);
        table.addCell(cell);
    }
}
