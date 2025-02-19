document.addEventListener("DOMContentLoaded", function () {
    document.getElementById("btnBuscarUsuario").addEventListener("click", buscarUsuario);
});

// 🔹 Función para buscar usuario por cédula
async function buscarUsuario() {
    const cedula = document.getElementById("inputCedula").value.trim();

    if (!cedula) {
        alert("Por favor, ingrese una cédula.");
        return;
    }

    try {
        const token = localStorage.getItem("token");
        if (!token) {
            console.error("No hay token disponible. Redirigiendo al login...");
            window.location.href = "/login";
            return;
        }

        // 🔹 Petición al backend con la URL correcta
        const response = await fetch(`${config.backendUrl}/api/gestion-persona/equipos/${cedula}`, {
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (!response.ok) {
            throw new Error("Error al obtener datos del usuario.");
        }

        const data = await response.json();

        // 🔹 Solo mostrar Cédula, Nombres y Alias
        renderizarUsuario(data);

    } catch (error) {
        console.error("Error en la búsqueda:", error);
        alert("No se encontraron datos para la cédula ingresada.");
    }
}

// 🔹 Función para mostrar datos en la tabla
function renderizarUsuario(usuario) {
    const tablaUsuario = document.getElementById("tablaUsuario");

    // Limpiar la tabla antes de agregar nuevos datos
    tablaUsuario.innerHTML = "";

    const fila = document.createElement("tr");
    fila.innerHTML = `
        <td>${usuario.cedula || "N/A"}</td>
        <td>${usuario.nombres || "N/A"}</td>
        <td>${usuario.alias || "N/A"}</td>
        <td>
            <button class="btn btn-warning btn-sm" onclick="abrirModalEditar('${usuario.cedula}', '${usuario.nombres}', '${usuario.alias}')">
                <i class="bi bi-pencil-square"></i> Editar
            </button>
        </td>
    `;
    
    tablaUsuario.appendChild(fila);
}

// 🔹 Función para abrir el modal con los datos actuales
function abrirModalEditar(cedula, nombres, alias) {
    document.getElementById("editarNombre").value = nombres;
    document.getElementById("editarAlias").value = alias;
    document.getElementById("formEditarUsuario").dataset.cedula = cedula; // Guardamos la cédula en el formulario

    // Mostrar el modal de edición
    const modal = new bootstrap.Modal(document.getElementById("modalEditarUsuario"));
    modal.show();
}

// Función auxiliar para obtener la clase de tabla según el tipo de equipo
function getTableClassForEquipment(equipmentType) {
    switch (equipmentType.toLowerCase()) {
        case 'laptop':
            return 'table-laptop';
        case 'impresora':
            return 'table-impresora';
        case 'pc':
            return 'table-pc';
        default:
            return 'table-equipo-default';
    }
}

// Función para renderizar los datos del usuario y, si existen, los equipos
function renderizarUsuario(usuario) {
    const tablaUsuario = document.getElementById("tablaUsuario");
    tablaUsuario.innerHTML = ""; // Limpiar la tabla de usuario

    const fila = document.createElement("tr");
    fila.innerHTML = `
        <td>${usuario.cedula || "N/A"}</td>
        <td>${usuario.nombres || "N/A"}</td>
        <td>${usuario.alias || "N/A"}</td>
        <td>
            <button class="btn btn-warning btn-sm" onclick="abrirModalEditar('${usuario.cedula}', '${usuario.nombres}', '${usuario.alias}')">
                <i class="bi bi-pencil-square"></i> Editar
            </button>
        </td>
    `;
    tablaUsuario.appendChild(fila);

    // Si la API devuelve equipos, renderizarlos
    if (usuario.equiposPorConexion) {
        renderizarEquipos(usuario.equiposPorConexion);
    }
}

// Función para renderizar los equipos divididos por conexión y tipo de equipo
function renderizarEquipos(equiposPorConexion) {
    const equiposContainer = document.getElementById("equiposContainer");
    equiposContainer.innerHTML = ""; // Limpiar el contenedor

    // Recorrer cada tipo de conexión (por ejemplo, "Alámbricos", "Inalámbricos")
    for (const conexionType in equiposPorConexion) {
        // Crear contenedor para cada conexión
        const containerDiv = document.createElement("div");
        containerDiv.classList.add("p-3", "mb-3");
        if (conexionType === "Alámbricos") {
            containerDiv.classList.add("container-alambricos");
        } else if (conexionType === "Inalámbricos") {
            containerDiv.classList.add("container-inalambricos");
        } else {
            containerDiv.style.border = "2px solid #000";
        }

        // Título del tipo de conexión
        const title = document.createElement("h4");
        title.textContent = conexionType;
        containerDiv.appendChild(title);

        // Recorrer cada tipo de equipo dentro de la conexión (Laptop, Impresora, PC, etc.)
        const equiposPorTipo = equiposPorConexion[conexionType];
        for (const tipoEquipo in equiposPorTipo) {
            // Subtítulo para el tipo de equipo
            const subTitle = document.createElement("h5");
            subTitle.textContent = tipoEquipo;
            containerDiv.appendChild(subTitle);

            // Crear la tabla para el equipo
            const table = document.createElement("table");
            const tableClass = getTableClassForEquipment(tipoEquipo);
            table.classList.add("table", "table-bordered", tableClass, "mb-3");

            // Crear el encabezado de la tabla
            const thead = document.createElement("thead");
            const headerRow = document.createElement("tr");
            ["Nombre", "IP", "MAC"].forEach(headerText => {
                const th = document.createElement("th");
                th.textContent = headerText;
                headerRow.appendChild(th);
            });
            thead.appendChild(headerRow);
            table.appendChild(thead);

            // Crear el cuerpo de la tabla
            const tbody = document.createElement("tbody");
            equiposPorTipo[tipoEquipo].forEach(equipo => {
                const row = document.createElement("tr");
                row.innerHTML = `
                    <td>${equipo.nombre || "N/A"}</td>
                    <td>${equipo.ip || "N/A"}</td>
                    <td>${equipo.mac || "N/A"}</td>
                `;
                tbody.appendChild(row);
            });
            table.appendChild(tbody);

            // Envolver la tabla en un contenedor responsive para evitar que se extienda hasta el borde
            const responsiveDiv = document.createElement("div");
            responsiveDiv.classList.add("table-responsive");
            responsiveDiv.appendChild(table);
            containerDiv.appendChild(responsiveDiv);
        }

        equiposContainer.appendChild(containerDiv);
    }
}
