# Frontend - Inventario de Ferreteria

## 1. Descripcion

El frontend es una aplicacion en Next.js 16 con App Router. Su objetivo es entregar una interfaz profesional para operar la ferreteria desde navegador.

## 2. Stack

- Next.js 16
- React 19
- TypeScript
- Tailwind CSS 4
- `lucide-react`

## 3. Estructura principal

```text
frontend/src
├── app
├── components
├── lib
└── types
```

### `app`

Contiene las rutas de la aplicacion:

- `/`
- `/login`
- `/dashboard`
- `/dashboard/categorias`
- `/dashboard/proveedores`
- `/dashboard/productos`
- `/dashboard/compras`
- `/dashboard/ventas`
- `/dashboard/movimientos`
- `/dashboard/usuarios`
- `/dashboard/reportes`

### `components`

Componentes reutilizables y modulos funcionales:

- `dashboard-shell.tsx`
- `data-table.tsx`
- `entity-crud-page.tsx`
- `product-manager.tsx`
- `purchase-manager.tsx`
- `sale-manager.tsx`
- `movement-manager.tsx`
- `reports-panel.tsx`
- `ui.tsx`

### `lib`

Utilidades compartidas:

- `api.ts`
- `auth.ts`
- `types.ts`

## 4. Rutas y flujo de navegacion

### `/login`

Pantalla de acceso.

Responsabilidades:

- pedir usuario y clave
- llamar al backend
- guardar sesion
- redirigir a `/dashboard`

### `/dashboard`

Pagina de resumen general:

- metricas principales
- valorizacion del inventario
- movimientos recientes

### Modulos del dashboard

Cada subruta monta un componente concreto:

- categorias
- proveedores
- productos
- compras
- ventas
- movimientos
- usuarios
- reportes

## 5. Autenticacion en frontend

La sesion se guarda en `localStorage`.

Archivo:

- [auth.ts](C:\Users\ffigu\OneDrive\Documentos\niceforo-project\poo2-hardware-store-inventory\frontend\src\lib\auth.ts)

Funciones:

- `saveSession`
- `getSession`
- `clearSession`

Datos guardados:

- token
- username
- nombreCompleto
- rol

## 6. Cliente API

Archivo:

- [api.ts](C:\Users\ffigu\OneDrive\Documentos\niceforo-project\poo2-hardware-store-inventory\frontend\src\lib\api.ts)

Responsabilidades:

- inyectar `Authorization: Bearer`
- apuntar a `NEXT_PUBLIC_API_URL`
- parsear respuestas JSON o texto
- lanzar errores legibles en el frontend
- descargar blobs PDF para reportes

## 7. Dashboard shell

Archivo:

- [dashboard-shell.tsx](C:\Users\ffigu\OneDrive\Documentos\niceforo-project\poo2-hardware-store-inventory\frontend\src\components\dashboard-shell.tsx)

Responsabilidades:

- layout del panel
- menu lateral
- deteccion de sesion
- redireccion a login si no hay token
- cabecera con titulo dinamico

## 8. CRUD generico

Archivo:

- `entity-crud-page.tsx`

Se reutiliza para modulos simples como:

- categorias
- proveedores
- usuarios

Beneficio:

- reduce duplicacion
- centraliza tablas y formularios basicos

## 9. Modulo de productos

Archivo:

- [product-manager.tsx](C:\Users\ffigu\OneDrive\Documentos\niceforo-project\poo2-hardware-store-inventory\frontend\src\components\product-manager.tsx)

Es el modulo mas particular del frontend.

### Funciones

- crear productos
- editar productos
- eliminar productos
- asociar categoria y proveedor
- mostrar foto si existe
- permitir foto opcional

### Flujo de imagen

El frontend ya no depende del flujo multipart para el navegador.

Comportamiento:

- si no eliges foto, manda JSON normal
- si eliges foto, la convierte a base64
- envia `fotoBase64` y `fotoNombre`
- el backend guarda la imagen y devuelve `fotoUrl`

### Validaciones de la imagen

- formatos permitidos: JPG, JPEG, PNG, WEBP
- tamano maximo mostrado al usuario: `15 MB`
- si supera el limite, el input se limpia y muestra error

### Edicion

Si estas editando:

- puedes dejar vacio el campo foto
- el producto se actualiza sin cambiar la imagen actual

## 10. Modulos operativos

### Compras

Componente:

- `purchase-manager.tsx`

Permite:

- registrar compra
- agregar multiples items
- recalcular total

### Ventas

Componente:

- `sale-manager.tsx`

Permite:

- registrar venta
- agregar multiples items
- recalcular total

### Movimientos

Componente:

- `movement-manager.tsx`

Permite:

- registrar ajustes o movimientos manuales
- consultar historial de inventario

## 11. Reportes

Componente:

- [reports-panel.tsx](C:\Users\ffigu\OneDrive\Documentos\niceforo-project\poo2-hardware-store-inventory\frontend\src\components\reports-panel.tsx)

Funciones:

- cargar todos los reportes en pantalla
- mostrar tablas por tipo de reporte
- abrir visor PDF dentro de la misma experiencia del sistema

## 12. Estilos

Archivos principales:

- `src/app/globals.css`
- componentes con clases Tailwind

Lineamiento visual:

- apariencia profesional
- panel de vidrio y fondos suaves
- menu lateral prominente
- dashboard amplio

## 13. Variables de entorno del frontend

### `frontend/.env.local`

Archivo local para ejecucion directa fuera de Docker:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

### `frontend/.env.production`

Archivo para build o pruebas de produccion:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

## 14. Ejecucion

### Local

```bash
cd frontend
npm install
npm run dev
```

### Docker

Desde la raiz:

```bash
docker compose --env-file .env.local -f docker-compose.dev.yml up --build frontend
```

## 15. Notas sobre desarrollo

El script `dev` se dejo asi:

```json
"dev": "next dev --webpack"
```

Motivo:

- con Turbopack aparecian falsos `404` en subrutas del dashboard
- con webpack las rutas del App Router quedaron verificadas en `200`

## 16. Troubleshooting

### Todas las paginas del dashboard salen 404

Verifica:

- que el frontend este corriendo con `webpack`
- que el contenedor haya reiniciado despues del cambio

Comando recomendado:

```bash
docker compose --env-file .env.local -f docker-compose.dev.yml restart frontend
```

### No muestra datos del backend

Verifica:

- `NEXT_PUBLIC_API_URL`
- backend arriba en `:8080`
- sesion JWT valida

### No inicia sesion

Verifica:

- usuario y clave iniciales
- `JWT_SECRET`
- que el backend responda en `/api/auth/login`

### La foto de producto falla

Verifica:

- formato valido
- tamano maximo
- que el backend este reconstruido con el soporte `fotoBase64`

## 17. Resumen funcional del frontend

El frontend ya cubre:

- login
- dashboard
- CRUD de catalogos
- flujo de compras y ventas
- modulo de movimientos
- reportes en pantalla
- exportacion y visualizacion PDF
- manejo de imagenes de producto
