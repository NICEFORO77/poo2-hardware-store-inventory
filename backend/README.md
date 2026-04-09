# Backend - Inventario de Ferreteria

## 1. Descripcion

El backend es una API REST construida con Spring Boot 4.0.3. Su responsabilidad es manejar la logica del inventario, seguridad, persistencia, reportes y exportacion PDF.

## 2. Stack

- Java 25
- Spring Boot 4.0.3
- Spring Web
- Spring Security
- Spring Data JPA
- Spring Validation
- Spring Actuator
- PostgreSQL
- JWT con `jjwt`
- OpenPDF
- Lombok

## 3. Estructura de paquetes

```text
com.niceforo.inventario
├── auth
├── config
├── controller
├── dto
├── model
├── repository
├── security
└── service
```

### `config`

Contiene configuraciones globales:

- `SecurityConfig`
- `CorsConfig`
- `WebConfig`
- `ApiExceptionHandler`
- `DataSeeder`

### `controller`

Expone la API REST:

- `AuthController`
- `CategoriaController`
- `ProveedorController`
- `ProductoController`
- `CompraController`
- `VentaController`
- `MovimientoInventarioController`
- `UsuarioController`
- `DashboardController`
- `ReporteController`

### `service`

Contiene la logica de negocio:

- `ProductoService`
- `CompraService`
- `VentaService`
- `MovimientoInventarioService`
- `DashboardService`
- `ReporteService`
- `ReportePdfService`
- `StorageService`
- `UsuarioService`

### `model`

Entidades JPA del dominio:

- `Categoria`
- `Proveedor`
- `Producto`
- `Compra`
- `DetalleCompra`
- `Venta`
- `DetalleVenta`
- `MovimientoInventario`
- `Usuario`

Enums:

- `EstadoProducto`
- `TipoMovimiento`
- `Role`

## 4. Seguridad

La autenticacion se realiza con JWT.

### Flujo

1. el frontend envia usuario y clave a `/api/auth/login`
2. el backend valida credenciales
3. genera un token JWT
4. el frontend lo guarda en `localStorage`
5. el token se manda en `Authorization: Bearer <token>`

### Rutas publicas

- `/api/auth/**`
- `/uploads/**`
- `/actuator/health`

### Rutas protegidas

Todo lo demas requiere autenticacion.

## 5. Persistencia y base de datos

### Motor

- PostgreSQL 17

### Configuracion principal

Archivo:

- [application.yml](C:\Users\ffigu\OneDrive\Documentos\niceforo-project\poo2-hardware-store-inventory\backend\src\main\resources\application.yml)

Puntos importantes:

- `ddl-auto: update`
- `open-in-view: false`
- carga maxima de archivos: `15MB`
- directorio de uploads configurable

## 6. Entidades funcionales

### Categoria

Agrupa productos por linea de negocio.

### Proveedor

Representa a quien abastece la ferreteria.

### Producto

Entidad central del catalogo:

- codigo
- nombre
- descripcion
- unidad de medida
- precio de compra
- precio de venta
- stock actual
- stock minimo
- estado
- categoria
- proveedor principal
- foto opcional

### Compra y detalle de compra

Registran ingresos de mercaderia.

Al crear una compra:

- se guarda la cabecera
- se guardan los detalles
- aumenta el stock del producto
- se registran movimientos de tipo `ENTRADA`

### Venta y detalle de venta

Registran salidas por comercializacion.

Al crear una venta:

- se guarda la cabecera
- se guardan los detalles
- disminuye el stock
- se registran movimientos de tipo `SALIDA`

### MovimientoInventario

Sirve para auditar entradas, salidas y ajustes.

Tipos:

- `ENTRADA`
- `SALIDA`
- `AJUSTE`

### Usuario

Usuarios autenticables del sistema.

Roles:

- `ADMINISTRADOR`
- `OPERADOR`

## 7. Modulo de productos e imagenes

El backend soporta productos con o sin imagen.

### Casos soportados

- crear producto sin foto
- crear producto con foto
- actualizar producto sin cambiar foto
- actualizar producto cambiando foto

### Mecanismos soportados

- `MultipartFile` para escenarios multipart
- `fotoBase64` y `fotoNombre` para JSON

### Implementacion

- `ProductoRequest` acepta foto multipart o foto base64
- `StorageService` valida extension y tamano
- las imagenes se guardan en `uploads`
- el backend responde con `fotoUrl`

## 8. Seeder inicial

`DataSeeder` inserta automaticamente:

- usuarios
- categorias
- proveedores
- productos
- compras
- ventas
- ajustes

Solo se ejecuta si la base de datos esta vacia.

## 9. Dashboard

Endpoint:

- `GET /api/dashboard/resumen`

Devuelve indicadores como:

- total de productos
- productos activos
- productos con bajo stock
- total de categorias
- total de proveedores
- total de compras
- total de ventas
- valorizacion del inventario
- movimientos recientes

## 10. Reportes

Endpoint consolidado:

- `GET /api/reportes`

PDF por reporte:

- `GET /api/reportes/{key}/pdf`

Claves disponibles:

- `stockActual`
- `stockBajo`
- `entradasInventario`
- `salidasInventario`
- `compras`
- `ventas`
- `productosMasVendidos`
- `valorizacionInventario`
- `utilidadBasica`

## 11. Endpoints principales

### Autenticacion

- `POST /api/auth/login`

### Catalogos

- `GET|POST|PUT|DELETE /api/categorias`
- `GET|POST|PUT|DELETE /api/proveedores`
- `GET|POST|PUT|DELETE /api/productos`
- `GET|POST|PUT|DELETE /api/usuarios`

### Operacion

- `GET|POST /api/compras`
- `GET|POST /api/ventas`
- `GET|POST /api/movimientos`

### Consulta

- `GET /api/dashboard/resumen`
- `GET /api/reportes`
- `GET /api/reportes/{key}/pdf`

## 12. Manejo de errores

La API centraliza errores en `ApiExceptionHandler`.

Resuelve:

- errores de negocio con `ResponseStatusException`
- errores de validacion
- errores de tamano de archivo
- errores genericos no controlados

## 13. Recursos estaticos

Las imagenes guardadas se exponen por:

- `/uploads/**`

Configuracion:

- `WebConfig`

## 14. Ejecucion

### Con Docker

Desde la raiz:

```bash
docker compose --env-file .env.local -f docker-compose.dev.yml up --build backend postgres
```

### Local con Maven Wrapper

```bash
cd backend
./mvnw spring-boot:run
```

En Windows:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

## 15. Build

Compilar:

```bash
cd backend
./mvnw -DskipTests package
```

En Windows:

```powershell
cd backend
.\mvnw.cmd -DskipTests package
```

## 16. Health check

Endpoint:

- `/actuator/health`

Se usa tambien por Docker Compose para saber cuando el backend esta listo.

## 17. Notas de mantenimiento

- si cambias reglas de negocio de stock, revisa `CompraService`, `VentaService` y `MovimientoInventarioService`
- si cambias el flujo de imagenes, revisa `ProductoRequest`, `ProductoService` y `StorageService`
- si cambias autenticacion, revisa `SecurityConfig`, `AuthService`, `JwtAuthenticationFilter` y `CustomUserDetailsService`
- si agregas reportes, actualiza `ReporteService`, `ReportePdfService` y `ReporteController`
