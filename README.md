# Inventario de Ferreteria

Sistema full stack para gestionar una ferreteria mediana. El proyecto incluye autenticacion con JWT, CRUD de entidades principales, control de inventario, reportes operativos, exportacion PDF y carga inicial de datos ficticios.

## 1. Objetivo del proyecto

Este sistema fue construido para cubrir el flujo operativo basico de una ferreteria:

- registrar categorias y proveedores
- registrar productos con foto opcional
- controlar compras y entradas de inventario
- registrar ventas y salidas de inventario
- registrar ajustes manuales del inventario
- administrar usuarios con rol `ADMINISTRADOR` y `OPERADOR`
- visualizar indicadores en dashboard
- consultar reportes de stock, ventas, compras y utilidad
- exportar reportes a PDF

## 2. Stack tecnologico

### Backend

- Java 25
- Spring Boot 4.0.3
- Spring Security
- JWT con `jjwt`
- Spring Data JPA
- PostgreSQL 17
- OpenPDF para exportacion PDF
- Maven Wrapper

### Frontend

- Next.js 16
- React 19
- TypeScript
- Tailwind CSS 4
- `lucide-react` para iconografia

### Infraestructura

- Docker y Docker Compose
- entornos separados para desarrollo y produccion

## 3. Versiones recomendadas

- Java: `25`
- Node.js: `24.13.0`
- Maven: usar `backend/mvnw.cmd` o `backend/mvnw`
- Docker Desktop activo para el flujo principal

## 4. Credenciales iniciales

El proyecto crea usuarios de prueba automaticamente al primer arranque:

- `admin / admin123`
- `operador / operador123`

## 5. Arranque rapido

### Desarrollo con Docker

```bash
docker compose --env-file .env.local -f docker-compose.dev.yml up --build
```

Servicios:

- frontend: [http://localhost:3000](http://localhost:3000)
- backend: [http://localhost:8080/api](http://localhost:8080/api)
- health backend: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

### Produccion con Docker

```bash
docker compose --env-file .env.prod -f docker-compose.prod.yml up --build -d
```

## 6. Ejecucion local sin Docker

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

En Windows:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Nota importante:

- el modo `dev` del frontend se dejo en `webpack` para evitar falsos `404` que aparecian con Turbopack en este proyecto

## 7. Variables de entorno

### Archivo `.env.local`

Se usa para desarrollo:

```env
POSTGRES_DB=inventario_ferreteria
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres123
POSTGRES_PORT=5432
BACKEND_PORT=8080
FRONTEND_PORT=3000
JWT_SECRET=change-me-local-ferreteria-inventory-secret-2026
CORS_ALLOWED_ORIGINS=http://localhost:3000
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

### Archivo `.env.prod`

Se usa para produccion:

```env
POSTGRES_DB=inventario_ferreteria_prod
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres_prod_2026
POSTGRES_PORT=5432
BACKEND_PORT=8080
FRONTEND_PORT=3000
JWT_SECRET=change-me-production-ferreteria-inventory-secret-2026
CORS_ALLOWED_ORIGINS=http://localhost:3000
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

## 8. Estructura del repositorio

```text
.
├── backend/
├── frontend/
├── docker-compose.dev.yml
├── docker-compose.prod.yml
├── .env.local
├── .env.prod
├── .nvmrc
└── .vscode/
```

### Raiz

En la raiz viven:

- los archivos Docker Compose
- los `.env` de desarrollo y produccion
- la configuracion general del workspace
- la version de Node en `.nvmrc`

## 9. Arquitectura general

### Backend

El backend expone una API REST segura con JWT. Todas las rutas protegidas requieren token, excepto:

- `/api/auth/**`
- `/uploads/**`
- `/actuator/health`

La API maneja:

- autenticacion
- CRUD de catalogos
- compras con detalle
- ventas con detalle
- movimientos de inventario
- reportes consolidados
- exportacion PDF

### Frontend

El frontend es una SPA construida con App Router. Maneja:

- login
- dashboard principal
- modulos CRUD
- formulario avanzado de productos
- visor de reportes
- sesion persistida en `localStorage`

## 10. Modelo funcional del negocio

Las entidades principales del sistema son:

- `categorias`
- `proveedores`
- `productos`
- `compras`
- `detalle_compra`
- `ventas`
- `detalle_venta`
- `movimientos_inventario`
- `usuarios`

### Reglas importantes

- el stock no se debe editar manualmente como operacion normal del negocio
- el stock cambia por compras, ventas o ajustes
- cada compra genera entradas de inventario
- cada venta genera salidas de inventario
- los ajustes generan movimientos auditables
- la foto del producto es opcional
- la foto puede enviarse o no al crear y actualizar

## 11. Reportes incluidos

El sistema incluye estos reportes:

- stock actual
- productos con stock bajo
- entradas de inventario
- salidas de inventario
- compras
- ventas
- productos mas vendidos
- valorizacion de inventario
- utilidad basica

Cada reporte puede:

- verse en pantalla
- convertirse a PDF

## 12. Flujo de imagenes de producto

El modulo de productos soporta imagen opcional.

### Comportamiento actual

- si no envias foto, el producto se crea o actualiza normal
- si envias foto, el frontend la convierte a base64 y la manda dentro del JSON
- el backend decodifica la imagen y la guarda en `uploads`
- el producto guarda la ruta publica en `fotoUrl`

Esto se dejo asi porque en este entorno el flujo multipart podia devolver `413` incluso con imagenes pequenas. El flujo base64 quedo verificado y estable.

## 13. Datos iniciales

Al arrancar por primera vez, el backend inserta:

- usuarios iniciales
- categorias base
- proveedores base
- productos base
- compras iniciales
- ventas iniciales
- un ajuste inicial de inventario

Esto se hace en el seeder del backend para que el sistema sea demostrable desde el primer inicio.

## 14. Docker

### Desarrollo

`docker-compose.dev.yml` levanta:

- `postgres`
- `backend`
- `frontend`

Caracteristicas:

- backend montado con volumen del codigo fuente
- frontend montado con volumen del codigo fuente
- cache de Maven y dependencias de Node en volumenes

### Produccion

`docker-compose.prod.yml` levanta:

- `postgres`
- `backend`
- `frontend`

Caracteristicas:

- imagenes construidas para produccion
- arranque desacoplado de los servicios

## 15. Documentacion por modulo

Para detalle tecnico de cada capa revisa:

- [backend/README.md](backend/README.md)
- [frontend/README.md](frontend/README.md)

## 16. Troubleshooting

### El frontend muestra 404 en las subpaginas

Causa probable:

- el servidor `dev` estaba corriendo con Turbopack

Solucion aplicada:

- el script `npm run dev` usa `next dev --webpack`

### El backend no arranca por configuracion

Verifica:

- `JWT_SECRET`
- credenciales de PostgreSQL
- puertos ocupados

### Las fotos de productos fallaban con error de tamano

Solucion aplicada:

- se subio el limite del backend a `15MB`
- se cambio el envio de fotos del frontend a JSON base64
- se mantiene validacion de formatos y tamano

### Los reportes no cargan o fallan

Verifica:

- que el backend este arriba
- que el token de sesion exista
- que `NEXT_PUBLIC_API_URL` apunte a `/api`

## 17. Estado actual del proyecto

El proyecto queda documentado y funcional con:

- backend con Java 25 y Maven Wrapper
- frontend con Next.js 16 y modo `dev` estable
- CRUD completos
- login con JWT
- dashboard
- reportes y PDF
- productos con foto opcional
- Docker para desarrollo y produccion
