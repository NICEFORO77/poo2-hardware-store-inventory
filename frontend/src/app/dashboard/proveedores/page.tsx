"use client";

import { EntityCrudPage } from "@/components/entity-crud-page";

export default function ProveedoresPage() {
  return (
    <EntityCrudPage
      title="Nuevo proveedor"
      description="Registra a las empresas o personas que abastecen tu ferreteria."
      endpoint="/proveedores"
      initialValues={{
        nombre: "",
        documento: "",
        telefono: "",
        correo: "",
        direccion: ""
      }}
      fields={[
        { name: "nombre", label: "Nombre" },
        { name: "documento", label: "RUC o documento" },
        { name: "telefono", label: "Telefono" },
        { name: "correo", label: "Correo", type: "email" },
        { name: "direccion", label: "Direccion", type: "textarea" }
      ]}
      columns={[
        { key: "nombre", label: "Proveedor" },
        { key: "documento", label: "Documento" },
        { key: "telefono", label: "Telefono" },
        { key: "correo", label: "Correo" }
      ]}
    />
  );
}
