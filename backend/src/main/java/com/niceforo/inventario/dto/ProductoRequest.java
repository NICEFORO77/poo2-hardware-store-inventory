package com.niceforo.inventario.dto;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

import com.niceforo.inventario.model.EstadoProducto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductoRequest {
    private String codigo;
    private String nombre;
    private String descripcion;
    private String unidadMedida;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private Integer stockActual;
    private Integer stockMinimo;
    private EstadoProducto estado;
    private Long idCategoria;
    private Long idProveedorPrincipal;
    private MultipartFile foto;
    private String fotoBase64;
    private String fotoNombre;
}
