export type Categoria = {
  id: number;
  nombre: string;
  descripcion: string;
};

export type Proveedor = {
  id: number;
  nombre: string;
  documento: string;
  telefono: string;
  correo: string;
  direccion: string;
};

export type Producto = {
  id: number;
  codigo: string;
  nombre: string;
  descripcion: string;
  unidadMedida: string;
  precioCompra: number;
  precioVenta: number;
  stockActual: number;
  stockMinimo: number;
  estado: "ACTIVO" | "INACTIVO";
  fotoUrl?: string | null;
  categoria: Categoria;
  proveedorPrincipal?: Proveedor | null;
};

export type Usuario = {
  id: number;
  nombreCompleto: string;
  username: string;
  correo: string;
  rol: "ADMINISTRADOR" | "OPERADOR";
  activo: boolean;
};

export type CompraDetalle = {
  idDetalleCompra: number;
  idProducto: number;
  codigoProducto: string;
  producto: string;
  cantidad: number;
  precioCompra: number;
  subtotal: number;
};

export type Compra = {
  idCompra: number;
  fecha: string;
  idProveedor: number;
  proveedor: string;
  total: number;
  detalles: CompraDetalle[];
};

export type VentaDetalle = {
  idDetalleVenta: number;
  idProducto: number;
  codigoProducto: string;
  producto: string;
  cantidad: number;
  precioVenta: number;
  subtotal: number;
};

export type Venta = {
  idVenta: number;
  fecha: string;
  cliente: string;
  total: number;
  detalles: VentaDetalle[];
};

export type Movimiento = {
  id: number;
  fecha: string;
  tipoMovimiento: "ENTRADA" | "SALIDA" | "AJUSTE";
  cantidad: number;
  motivo: string;
  referencia?: string;
  producto: Producto;
};

export type DashboardSummary = {
  totalProductos: number;
  productosActivos: number;
  productosBajoStock: number;
  totalCategorias: number;
  totalProveedores: number;
  totalVentas: number;
  totalCompras: number;
  valorInventarioCosto: number;
  valorInventarioVenta: number;
  movimientosRecientes: {
    id: number;
    fecha: string;
    producto: string;
    tipo: string;
    cantidad: number;
    motivo: string;
  }[];
};

export type Reportes = {
  stockActual: Array<Record<string, string | number>>;
  stockBajo: Array<Record<string, string | number>>;
  entradasInventario: Array<Record<string, string | number>>;
  salidasInventario: Array<Record<string, string | number>>;
  compras: Array<Record<string, string | number>>;
  ventas: Array<Record<string, string | number>>;
  productosMasVendidos: Array<Record<string, string | number>>;
  valorizacionInventario: Array<Record<string, string | number>>;
  utilidadBasica: Array<Record<string, string | number>>;
};
