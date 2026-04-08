"use client";

import { EntityCrudPage } from "@/components/entity-crud-page";

export default function CategoriasPage() {
  return (
    <EntityCrudPage
      title="Nueva categoria"
      description="Agrupa tus productos por linea comercial para ordenar mejor el inventario."
      endpoint="/categorias"
      initialValues={{ nombre: "", descripcion: "" }}
      fields={[
        { name: "nombre", label: "Nombre" },
        { name: "descripcion", label: "Descripcion", type: "textarea" }
      ]}
      columns={[
        { key: "nombre", label: "Nombre" },
        { key: "descripcion", label: "Descripcion" }
      ]}
    />
  );
}
