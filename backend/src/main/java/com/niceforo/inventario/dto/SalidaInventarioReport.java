package com.niceforo.inventario.dto;

public record SalidaInventarioReport(
        String fecha,
        String producto,
        Integer cantidad,
        String motivo
) {
}
