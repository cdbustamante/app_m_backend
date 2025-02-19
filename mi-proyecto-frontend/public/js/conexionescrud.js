document.addEventListener("DOMContentLoaded", function () {
    document.getElementById("formEditarUsuario").addEventListener("submit", actualizarUsuario);
});

// 🔹 Función para actualizar los datos del usuario
async function actualizarUsuario(event) {
    event.preventDefault(); // Evita el envío tradicional del formulario

    const cedula = document.getElementById("formEditarUsuario").dataset.cedula;
    const nombres = document.getElementById("editarNombre").value.trim();
    const alias = document.getElementById("editarAlias").value.trim();

    if (!nombres || !alias) {
        alert("Todos los campos son obligatorios.");
        return;
    }

    try {
        const token = localStorage.getItem("token");
        if (!token) {
            console.error("No hay token disponible. Redirigiendo al login...");
            window.location.href = "/login";
            return;
        }

        // 🔹 Datos a enviar al backend
        const usuarioActualizado = {
            nombres: nombres,
            alias: alias
        };

        // 🔹 Petición al backend para actualizar el usuario
        const response = await fetch(`${config.backendUrl}/api/gestion-persona/actualizar/${cedula}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify(usuarioActualizado)
        });

        if (!response.ok) {
            throw new Error("Error al actualizar el usuario.");
        }

        alert("Usuario actualizado correctamente.");

        // 🔹 Cerrar el modal después de la actualización
        const modal = bootstrap.Modal.getInstance(document.getElementById("modalEditarUsuario"));
        modal.hide();

        // 🔹 Recargar la tabla con los nuevos datos
        buscarUsuario();

    } catch (error) {
        console.error("Error al actualizar:", error);
        alert("Hubo un problema al actualizar el usuario.");
    }
}
