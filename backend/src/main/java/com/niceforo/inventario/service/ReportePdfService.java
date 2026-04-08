package com.niceforo.inventario.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import lombok.RequiredArgsConstructor;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
@RequiredArgsConstructor
public class ReportePdfService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ReporteService reporteService;
    private final ObjectMapper objectMapper;

    public byte[] generatePdf(String key) {
        String title = reporteService.getReportTitle(key);
        Object reportData = reporteService.getReportByKey(key);

        List<LinkedHashMap<String, Object>> rows = objectMapper.convertValue(
                reportData,
                new TypeReference<List<LinkedHashMap<String, Object>>>() {
                }
        );

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate(), 24, 24, 30, 24);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            addHeader(document, title);
            addSummary(document, rows.size());
            addTable(document, rows);

            document.close();
            return outputStream.toByteArray();
        } catch (IOException | DocumentException exception) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "No se pudo generar el PDF del reporte");
        }
    }

    private void addHeader(Document document, String title) throws IOException, DocumentException {
        ClassPathResource resource = new ClassPathResource("report/logo-report.png");
        if (resource.exists()) {
            Image logo = Image.getInstance(resource.getContentAsByteArray());
            logo.scaleToFit(110, 90);
            logo.setAlignment(Image.ALIGN_LEFT);
            document.add(logo);
        }

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 19, new java.awt.Color(15, 118, 110));
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, new java.awt.Color(75, 85, 99));

        Paragraph titleParagraph = new Paragraph(title, titleFont);
        titleParagraph.setSpacingBefore(4);
        titleParagraph.setSpacingAfter(6);
        document.add(titleParagraph);

        Paragraph subtitle = new Paragraph(
                "Ferreteria Central · Generado el " + LocalDateTime.now().format(FORMATTER),
                subtitleFont
        );
        subtitle.setSpacingAfter(14);
        document.add(subtitle);
    }

    private void addSummary(Document document, int rowsCount) throws DocumentException {
        Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new java.awt.Color(146, 64, 14));
        PdfPCell summaryCell = new PdfPCell(new Phrase("Total de registros: " + rowsCount, summaryFont));
        summaryCell.setPadding(10);
        summaryCell.setBorder(Rectangle.NO_BORDER);
        summaryCell.setBackgroundColor(new java.awt.Color(254, 243, 199));

        PdfPTable summaryTable = new PdfPTable(1);
        summaryTable.setWidthPercentage(100);
        summaryTable.setSpacingAfter(12);
        summaryTable.addCell(summaryCell);
        document.add(summaryTable);
    }

    private void addTable(Document document, List<LinkedHashMap<String, Object>> rows) throws DocumentException {
        if (rows.isEmpty()) {
            Paragraph empty = new Paragraph(
                    "No hay datos disponibles para este reporte.",
                    FontFactory.getFont(FontFactory.HELVETICA, 11, new java.awt.Color(107, 114, 128))
            );
            document.add(empty);
            return;
        }

        List<String> headers = rows.getFirst().keySet().stream().toList();
        PdfPTable table = new PdfPTable(headers.size());
        table.setWidthPercentage(100);
        table.setSpacingBefore(4);

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(toLabel(header),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, java.awt.Color.WHITE)));
            cell.setBackgroundColor(new java.awt.Color(15, 118, 110));
            cell.setPadding(8);
            table.addCell(cell);
        }

        boolean alternate = false;
        for (Map<String, Object> row : rows) {
            for (String header : headers) {
                Object rawValue = row.get(header);
                PdfPCell cell = new PdfPCell(new Phrase(formatValue(rawValue),
                        FontFactory.getFont(FontFactory.HELVETICA, 8, new java.awt.Color(31, 41, 55))));
                cell.setPadding(7);
                cell.setBackgroundColor(alternate ? new java.awt.Color(248, 250, 252) : java.awt.Color.WHITE);
                table.addCell(cell);
            }
            alternate = !alternate;
        }

        document.add(table);
    }

    private String toLabel(String key) {
        return key
                .replaceAll("([A-Z])", " $1")
                .replace('_', ' ')
                .replaceFirst("^.", String.valueOf(Character.toUpperCase(key.charAt(0))));
    }

    private String formatValue(Object value) {
        if (value == null) {
            return "";
        }
        return String.valueOf(value);
    }
}
