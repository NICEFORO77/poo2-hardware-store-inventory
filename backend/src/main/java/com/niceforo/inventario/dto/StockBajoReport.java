package com.niceforo.inventario.dto;

public record StockBajoReport(
        String producto,
        Integer stockActual,
        Integer stockMinimo,
        String proveedor
) {
}
