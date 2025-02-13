document.addEventListener("DOMContentLoaded", function () {
    cargarDepartamentos();
});

// 🔹 Variables de paginación
let currentPage = 0;
const pageSize = 10;

// 🔹 Función para cargar los departamentos con paginación
async function cargarDepartamentos(page = 0) {
    try {
        const token = localStorage.getItem("token");
        if (!token) {
            console.error("No hay token disponible. Redirigiendo al login...");
            window.location.href = "/login";
            return;
        }

        const response = await fetch(`${config.backendUrl}/api/departamentos?page=${page}&size=${pageSize}`, {
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (!response.ok) throw new Error("Error al obtener departamentos");

        const data = await response.json();
        
        // 🔹 Se verifica si hay datos antes de renderizar
        if (data.content && data.content.length > 0) {
            renderizarDepartamentos(data.content);
        } else {
            document.getElementById("tablaDepartamentos").innerHTML = "<tr><td colspan='8' class='text-center'>No hay departamentos disponibles.</td></tr>";
        }
        
        actualizarPaginacion(data.totalPages, page);
    } catch (error) {
        console.error("Error cargando departamentos:", error);
    }
}

// 🔹 Función para actualizar la paginación
function actualizarPaginacion(totalPages, paginaActual) {
    const paginacion = document.getElementById("paginacion");
    if (!paginacion) return; // Evitar error si el elemento no existe

    paginacion.innerHTML = "";

    for (let i = 0; i < totalPages; i++) {
        paginacion.innerHTML += `<button onclick="cargarDepartamentos(${i})" class="btn ${i === paginaActual ? 'btn-primary' : 'btn-light'}">${i + 1}</button>`;
    }
}

// 🔹 Renderizar departamentos en la tabla
function renderizarDepartamentos(departamentos) {
    const tablaDepartamentos = document.getElementById("tablaDepartamentos");
    tablaDepartamentos.innerHTML = "";

    departamentos.forEach(departamento => {
        const fila = document.createElement("tr");
        fila.innerHTML = `
            <td>${departamento.codigoDepar || "N/A"}</td>
            <td>${departamento.nombreDepar}</td>
            <td>${departamento.fechaCreacion ? new Date(departamento.fechaCreacion).toLocaleDateString() : "N/A"}</td>
            <td>${departamento.fechaModificacion ? new Date(departamento.fechaModificacion).toLocaleDateString() : "N/A"}</td>
            <td>${departamento.creadoPorUsuario || "N/A"}</td>
            <td>${departamento.modificadoPorUsuario || "N/A"}</td>
            <td class="${departamento.registroActivo ? "text-success" : "text-danger"}">
                ${departamento.registroActivo ? "Activo" : "Inactivo"}
            </td>
            <td>
                <button class="btn btn-warning btn-sm" onclick="editarDepartamento(${departamento.id})">
                    <i class="bi bi-pencil-square"></i>
                </button>
                <button class="btn btn-danger btn-sm" onclick="mostrarModalEliminar(${departamento.id})">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        `;
        tablaDepartamentos.appendChild(fila);
    });
}
