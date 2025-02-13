document.addEventListener("DOMContentLoaded", function () {
    asignarEventosCrud();
});

// 🔹 Asignar eventos a los formularios
function asignarEventosCrud() {
    document.getElementById("formAgregarDepartamento").addEventListener("submit", agregarDepartamento);
    document.getElementById("formEditarDepartamento").addEventListener("submit", actualizarDepartamento);
}

// 🔹 Función para agregar un nuevo departamento
async function agregarDepartamento(event) {
    event.preventDefault();

    const token = localStorage.getItem("token");
    if (!token) {
        console.error("No hay token disponible. Redirigiendo al login...");
        window.location.href = "/login";
        return;
    }

    const codigo = document.getElementById("codigoAgregarDepartamento").value.trim();
    const nombre = document.getElementById("nombreAgregarDepartamento").value.trim();

    if (!codigo || !nombre) {
        alert("Todos los campos son obligatorios.");
        return;
    }

    try {
        const response = await fetch(`${config.backendUrl}/api/departamentos`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify({ codigoDepar: codigo, nombreDepar: nombre })
        });

        if (!response.ok) throw new Error("Error al crear el departamento");

        cerrarModal("modalAgregarDepartamento");
        mostrarModalExito("Departamento agregado correctamente");
        cargarDepartamentos();
    } catch (error) {
        console.error("Error al agregar el departamento:", error);
    }
}

// 🔹 Función para editar un departamento (abrir modal con datos)
async function editarDepartamento(id) {
    const token = localStorage.getItem("token");
    if (!token) {
        console.error("No hay token disponible. Redirigiendo al login...");
        window.location.href = "/login";
        return;
    }

    try {
        const response = await fetch(`${config.backendUrl}/api/departamentos/${id}`, {
            method: "GET",
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (!response.ok) throw new Error("Error al obtener datos del departamento");

        const departamento = await response.json();

        // Llenar el modal con los datos obtenidos
        document.getElementById("idEditarDepartamento").value = departamento.id;
        document.getElementById("codigoEditarDepartamento").value = departamento.codigoDepar;
        document.getElementById("nombreEditarDepartamento").value = departamento.nombreDepar;

        var modal = new bootstrap.Modal(document.getElementById("modalEditarDepartamento"));
        modal.show();
    } catch (error) {
        console.error("Error al obtener el departamento para edición:", error);
    }
}

// 🔹 Función para actualizar un departamento
async function actualizarDepartamento(event) {
    event.preventDefault();

    const token = localStorage.getItem("token");
    if (!token) {
        console.error("No hay token disponible. Redirigiendo al login...");
        window.location.href = "/login";
        return;
    }

    const id = document.getElementById("idEditarDepartamento").value;
    const codigo = document.getElementById("codigoEditarDepartamento").value.trim();
    const nombre = document.getElementById("nombreEditarDepartamento").value.trim();

    if (!codigo || !nombre) {
        alert("Todos los campos son obligatorios.");
        return;
    }

    try {
        const response = await fetch(`${config.backendUrl}/api/departamentos/${id}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify({ codigoDepar: codigo, nombreDepar: nombre })
        });

        if (!response.ok) throw new Error("Error al actualizar el departamento");

        cerrarModal("modalEditarDepartamento");
        mostrarModalExito("Departamento actualizado correctamente");
        cargarDepartamentos();
    } catch (error) {
        console.error("Error al actualizar el departamento:", error);
    }
}

// 🔹 Mostrar modal de confirmación antes de eliminar
function mostrarModalEliminar(id) {
    const btnConfirmar = document.getElementById("confirmarEliminar");
    btnConfirmar.onclick = function () {
        eliminarDepartamento(id);
    };
    var modal = new bootstrap.Modal(document.getElementById("modalEliminar"));
    modal.show();
}

// 🔹 Función para eliminar un departamento
async function eliminarDepartamento(id) {
    const token = localStorage.getItem("token");
    if (!token) {
        console.error("No hay token disponible. Redirigiendo al login...");
        window.location.href = "/login";
        return;
    }

    try {
        const response = await fetch(`${config.backendUrl}/api/departamentos/${id}`, {
            method: "DELETE",
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (!response.ok) throw new Error("Error al eliminar el departamento");

        cerrarModal("modalEliminar");
        mostrarModalExito("Departamento eliminado correctamente");
        cargarDepartamentos();
    } catch (error) {
        console.error("Error al eliminar el departamento:", error);
    }
}

// 🔹 Mostrar modal de éxito con mensaje dinámico
function mostrarModalExito(mensaje) {
    const mensajeExito = document.getElementById("mensajeExito");
    if (!mensajeExito) {
        console.error("Elemento mensajeExito no encontrado.");
        return;
    }

    mensajeExito.innerText = mensaje;
    var modal = new bootstrap.Modal(document.getElementById("modalExito"));
    modal.show();
}

// 🔹 Cerrar un modal específico
function cerrarModal(id) {
    var modal = bootstrap.Modal.getInstance(document.getElementById(id));
    if (modal) modal.hide();
}
