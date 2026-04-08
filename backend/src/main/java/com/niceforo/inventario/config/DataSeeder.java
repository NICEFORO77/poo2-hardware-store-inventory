package com.niceforo.inventario.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.niceforo.inventario.dto.CompraItemRequest;
import com.niceforo.inventario.dto.CompraRequest;
import com.niceforo.inventario.dto.MovimientoRequest;
import com.niceforo.inventario.dto.VentaItemRequest;
import com.niceforo.inventario.dto.VentaRequest;
import com.niceforo.inventario.model.Categoria;
import com.niceforo.inventario.model.EstadoProducto;
import com.niceforo.inventario.model.Producto;
import com.niceforo.inventario.model.Proveedor;
import com.niceforo.inventario.model.Role;
import com.niceforo.inventario.model.TipoMovimiento;
import com.niceforo.inventario.model.Usuario;
import com.niceforo.inventario.repository.CategoriaRepository;
import com.niceforo.inventario.repository.ProductoRepository;
import com.niceforo.inventario.repository.ProveedorRepository;
import com.niceforo.inventario.repository.UsuarioRepository;
import com.niceforo.inventario.service.CompraService;
import com.niceforo.inventario.service.MovimientoInventarioService;
import com.niceforo.inventario.service.VentaService;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(
            UsuarioRepository usuarioRepository,
            CategoriaRepository categoriaRepository,
            ProveedorRepository proveedorRepository,
            ProductoRepository productoRepository,
            PasswordEncoder passwordEncoder,
            CompraService compraService,
            VentaService ventaService,
            MovimientoInventarioService movimientoInventarioService
    ) {
        return args -> {
            if (usuarioRepository.count() > 0) {
                return;
            }

            usuarioRepository.saveAll(List.of(
                    Usuario.builder()
                            .nombreCompleto("Administrador General")
                            .username("admin")
                            .password(passwordEncoder.encode("admin123"))
                            .correo("admin@ferreteria.local")
                            .rol(Role.ADMINISTRADOR)
                            .activo(true)
                            .build(),
                    Usuario.builder()
                            .nombreCompleto("Operador Principal")
                            .username("operador")
                            .password(passwordEncoder.encode("operador123"))
                            .correo("operador@ferreteria.local")
                            .rol(Role.OPERADOR)
                            .activo(true)
                            .build()
            ));

            Categoria herramientas = categoriaRepository.save(Categoria.builder()
                    .nombre("Herramientas")
                    .descripcion("Herramientas manuales y electricas")
                    .build());
            Categoria electricidad = categoriaRepository.save(Categoria.builder()
                    .nombre("Electricidad")
                    .descripcion("Materiales electricos y accesorios")
                    .build());
            Categoria pintura = categoriaRepository.save(Categoria.builder()
                    .nombre("Pintura")
                    .descripcion("Pinturas, brochas y accesorios")
                    .build());

            Proveedor ferreMax = proveedorRepository.save(Proveedor.builder()
                    .nombre("FerreMax Distribuciones")
                    .documento("20600011122")
                    .telefono("999111222")
                    .correo("ventas@ferremax.com")
                    .direccion("Av. Industrial 120 - Lima")
                    .build());
            Proveedor powerTools = proveedorRepository.save(Proveedor.builder()
                    .nombre("Power Tools SAC")
                    .documento("20512345678")
                    .telefono("988777666")
                    .correo("contacto@powertools.com")
                    .direccion("Jr. Los Andes 550 - Lima")
                    .build());

            Producto martillo = productoRepository.save(Producto.builder()
                    .codigo("FER-001")
                    .nombre("Martillo carpintero 16oz")
                    .descripcion("Martillo de acero con mango ergonomico")
                    .unidadMedida("unidad")
                    .precioCompra(new BigDecimal("18.50"))
                    .precioVenta(new BigDecimal("29.90"))
                    .stockActual(0)
                    .stockMinimo(8)
                    .estado(EstadoProducto.ACTIVO)
                    .categoria(herramientas)
                    .proveedorPrincipal(ferreMax)
                    .build());
            Producto taladro = productoRepository.save(Producto.builder()
                    .codigo("FER-002")
                    .nombre("Taladro percutor 650W")
                    .descripcion("Taladro electrico de uso profesional")
                    .unidadMedida("unidad")
                    .precioCompra(new BigDecimal("145.00"))
                    .precioVenta(new BigDecimal("199.90"))
                    .stockActual(0)
                    .stockMinimo(4)
                    .estado(EstadoProducto.ACTIVO)
                    .categoria(herramientas)
                    .proveedorPrincipal(powerTools)
                    .build());
            Producto cable = productoRepository.save(Producto.builder()
                    .codigo("FER-003")
                    .nombre("Cable THW 12 AWG")
                    .descripcion("Cable electrico flexible por metro")
                    .unidadMedida("metro")
                    .precioCompra(new BigDecimal("2.10"))
                    .precioVenta(new BigDecimal("3.60"))
                    .stockActual(0)
                    .stockMinimo(100)
                    .estado(EstadoProducto.ACTIVO)
                    .categoria(electricidad)
                    .proveedorPrincipal(ferreMax)
                    .build());
            Producto pinturaBlanca = productoRepository.save(Producto.builder()
                    .codigo("FER-004")
                    .nombre("Pintura latex blanca 1 galon")
                    .descripcion("Pintura latex lavable para interiores")
                    .unidadMedida("galon")
                    .precioCompra(new BigDecimal("42.00"))
                    .precioVenta(new BigDecimal("63.50"))
                    .stockActual(0)
                    .stockMinimo(6)
                    .estado(EstadoProducto.ACTIVO)
                    .categoria(pintura)
                    .proveedorPrincipal(ferreMax)
                    .build());
            Producto brocha = productoRepository.save(Producto.builder()
                    .codigo("FER-005")
                    .nombre("Brocha profesional 3 pulgadas")
                    .descripcion("Brocha para pintura de acabado fino")
                    .unidadMedida("unidad")
                    .precioCompra(new BigDecimal("7.50"))
                    .precioVenta(new BigDecimal("12.90"))
                    .stockActual(0)
                    .stockMinimo(12)
                    .estado(EstadoProducto.ACTIVO)
                    .categoria(pintura)
                    .proveedorPrincipal(ferreMax)
                    .build());

            compraService.create(new CompraRequest(
                    LocalDateTime.now().minusDays(12),
                    ferreMax.getId(),
                    List.of(
                            new CompraItemRequest(martillo.getId(), 20, new BigDecimal("18.50")),
                            new CompraItemRequest(cable.getId(), 250, new BigDecimal("2.10")),
                            new CompraItemRequest(pinturaBlanca.getId(), 12, new BigDecimal("42.00")),
                            new CompraItemRequest(brocha.getId(), 30, new BigDecimal("7.50"))
                    )
            ));

            compraService.create(new CompraRequest(
                    LocalDateTime.now().minusDays(8),
                    powerTools.getId(),
                    List.of(new CompraItemRequest(taladro.getId(), 10, new BigDecimal("145.00")))
            ));

            ventaService.create(new VentaRequest(
                    LocalDateTime.now().minusDays(4),
                    "Constructora San Miguel",
                    List.of(
                            new VentaItemRequest(martillo.getId(), 4, new BigDecimal("29.90")),
                            new VentaItemRequest(cable.getId(), 80, new BigDecimal("3.60")),
                            new VentaItemRequest(taladro.getId(), 2, new BigDecimal("199.90"))
                    )
            ));

            ventaService.create(new VentaRequest(
                    LocalDateTime.now().minusDays(2),
                    "Cliente Mostrador",
                    List.of(
                            new VentaItemRequest(pinturaBlanca.getId(), 3, new BigDecimal("63.50")),
                            new VentaItemRequest(brocha.getId(), 6, new BigDecimal("12.90"))
                    )
            ));

            movimientoInventarioService.create(new MovimientoRequest(
                    LocalDateTime.now().minusDays(1),
                    brocha.getId(),
                    TipoMovimiento.AJUSTE,
                    -2,
                    "Ajuste por diferencia en conteo fisico",
                    "AJUSTE-INICIAL"
            ));
        };
    }
}
