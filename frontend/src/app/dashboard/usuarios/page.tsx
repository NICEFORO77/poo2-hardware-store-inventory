"use client";

import { EntityCrudPage } from "@/components/entity-crud-page";

export default function UsuariosPage() {
  return (
    <EntityCrudPage
      title="Nuevo usuario"
      description="Crea cuentas para administradores y operadores del sistema."
      endpoint="/usuarios"
      initialValues={{
        nombreCompleto: "",
        username: "",
        password: "",
        correo: "",
        rol: "OPERADOR",
        activo: true
      }}
      fields={[
        { name: "nombreCompleto", label: "Nombre completo" },
        { name: "username", label: "Usuario" },
        { name: "password", label: "Contrasena", type: "password" },
        { name: "correo", label: "Correo", type: "email" },
        {
          name: "rol",
          label: "Rol",
          type: "select",
          options: [
            { value: "ADMINISTRADOR", label: "Administrador" },
            { value: "OPERADOR", label: "Operador" }
          ]
        },
        { name: "activo", label: "Estado", type: "checkbox" }
      ]}
      columns={[
        { key: "nombreCompleto", label: "Nombre" },
        { key: "username", label: "Usuario" },
        { key: "correo", label: "Correo" },
        { key: "rol", label: "Rol" },
        {
          key: "activo",
          label: "Estado",
          render: (row) => (row.activo ? "Activo" : "Inactivo")
        }
      ]}
    />
  );
}
