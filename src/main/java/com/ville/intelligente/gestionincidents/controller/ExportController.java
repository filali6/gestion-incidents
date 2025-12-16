package com.ville.intelligente.gestionincidents.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.ville.intelligente.gestionincidents.metier.IncidentService;
import com.ville.intelligente.gestionincidents.model.Incident;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ExportController {

    private final IncidentService incidentService;

    public ExportController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @GetMapping("/export/csv")
    public void exportCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=incidents.csv");

        List<Incident> incidents = incidentService.findAll();

        PrintWriter writer = response.getWriter();
        writer.println("ID,Titre,Statut,Catégorie,Date,Quartier,Priorité");

        for (Incident i : incidents) {
            writer.println(String.format("%d,\"%s\",%s,%s,%s,%s,%s",
                    i.getId(),
                    i.getTitre().replace("\"", "\"\""),
                    i.getStatut(),
                    i.getCategorie() != null ? i.getCategorie().getNom() : "",
                    i.getDateDeclaration(),
                    i.getQuartier() != null ? i.getQuartier().getNom() : "",
                    i.getPriorite()));
        }
    }
    @GetMapping("/export/pdf")
    public void exportPDF(HttpServletResponse response) throws DocumentException, IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=incidents.pdf");
        
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        
        document.open();
        
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Liste des Incidents", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));
        
        List<Incident> incidents = incidentService.findAll();
        
        for (Incident i : incidents) {
            document.add(new Paragraph("ID: " + i.getId()));
            document.add(new Paragraph("Titre: " + i.getTitre()));
            document.add(new Paragraph("Statut: " + i.getStatut()));
            document.add(new Paragraph(" "));
        }
        
        document.close();
    }
}