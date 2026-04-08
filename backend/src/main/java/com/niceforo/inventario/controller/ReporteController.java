package com.niceforo.inventario.controller;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.niceforo.inventario.service.ReportePdfService;
import com.niceforo.inventario.service.ReporteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;
    private final ReportePdfService reportePdfService;

    @GetMapping
    public Map<String, Object> allReports() {
        return Map.of(
                "stockActual", reporteService.stockActual(),
                "stockBajo", reporteService.stockBajo(),
                "entradasInventario", reporteService.entradasInventario(),
                "salidasInventario", reporteService.salidasInventario(),
                "compras", reporteService.compras(),
                "ventas", reporteService.ventas(),
                "productosMasVendidos", reporteService.productosMasVendidos(),
                "valorizacionInventario", reporteService.valorizacionInventario(),
                "utilidadBasica", reporteService.utilidadBasica()
        );
    }

    @GetMapping(value = "/{key}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> pdf(@PathVariable String key) {
        byte[] pdf = reportePdfService.generatePdf(key);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + key + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
