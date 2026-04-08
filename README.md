# Inventario de Ferreteria

Proyecto full stack para una ferreteria mediana con:

- Backend en Spring Boot 4.0.3, JWT y PostgreSQL
- Frontend en Next.js 16 con Tailwind CSS
- CRUD para categorias, proveedores, productos, compras, ventas, movimientos y usuarios
- Dashboard y reportes de inventario
- Datos ficticios al iniciar
- Docker para desarrollo y produccion

## Versiones recomendadas

- Java: 25
- Node.js: 24.13.0
- Maven: preferible usar Maven Wrapper (`mvnw`) cuando este generado en `backend`

## Usuarios iniciales

- `admin / admin123`
- `operador / operador123`

## Ejecutar en desarrollo

```bash
docker compose --env-file .env.local -f docker-compose.dev.yml up --build
```

Frontend:

- [http://localhost:3000](http://localhost:3000)

Backend:

- [http://localhost:8080/api](http://localhost:8080/api)

## Ejecutar en produccion

```bash
docker compose --env-file .env.prod -f docker-compose.prod.yml up --build -d
```

## Estructura funcional

- `backend`: API REST, seguridad JWT, seed inicial y logica de inventario
- `frontend`: login, dashboard, CRUD y reportes
- `.env.local`: variables para entorno de desarrollo
- `.env.prod`: variables para entorno productivo

## Reportes incluidos

- Stock actual
- Productos con stock bajo
- Entradas de inventario
- Salidas de inventario
- Compras
- Ventas
- Productos mas vendidos
- Valorizacion de inventario
- Utilidad basica
