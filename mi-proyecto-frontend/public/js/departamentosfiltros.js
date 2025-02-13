document.addEventListener("DOMContentLoaded", function () {
    // Asignar eventos a los filtros
    document.getElementById("btnToggleFiltros").addEventListener("click", toggleFiltros);
    document.getElementById("btnAplicarFiltros").addEventListener("click", aplicarFiltros);
    
    // Manejo dinámico de los inputs de filtros
    const filtros = document.querySelectorAll(".filtro-toggle");
    filtros.forEach(filtro => {
        filtro.addEventListener("change", function () {
            let inputRelacionado = document.getElementById("input" + this.id.replace("filtro", ""));
            if (this.checked) {
                inputRelacionado.style.display = "block"; // Mostrar input
            } else {
                inputRelacionado.style.display = "none"; // Ocultar input
                inputRelacionado.value = ""; // Limpiar el campo
            }
        });
    });
});

// 🔹 Mostrar u ocultar el contenedor de filtros
function toggleFiltros() {
    let contenedorFiltros = document.getElementById("contenedorFiltros");
    if (contenedorFiltros.style.display === "none") {
        contenedorFiltros.style.display = "block";
    } else {
        contenedorFiltros.style.display = "none";
    }
}

// 🔹 Aplicar los filtros seleccionados
function aplicarFiltros() {
    let filtrosAplicados = {};

    // Obtener los valores de los filtros activados
    if (document.getElementById("filtroCodigo").checked) {
        filtrosAplicados.codigo = document.getElementById("inputCodigo").value.trim();
    }
    if (document.getElementById("filtroNombre").checked) {
        filtrosAplicados.nombre = document.getElementById("inputNombre").value.trim();
    }
    if (document.getElementById("filtroFechaCreacion").checked) {
        filtrosAplicados.fechaCreacion = document.getElementById("inputFechaCreacion").value;
    }
    if (document.getElementById("filtroFechaModificacion").checked) {
        filtrosAplicados.fechaModificacion = document.getElementById("inputFechaModificacion").value;
    }
    if (document.getElementById("filtroCreador").checked) {
        filtrosAplicados.creador = document.getElementById("inputCreador").value.trim();
    }
    if (document.getElementById("filtroModificador").checked) {
        filtrosAplicados.modificador = document.getElementById("inputModificador").value.trim();
    }
    console.log("📌 Filtros aplicados:", filtrosAplicados);


    // Enviar los filtros al backend
    cargarDepartamentosConFiltros(filtrosAplicados);
}

// 🔹 Función para cargar departamentos con filtros
async function cargarDepartamentosConFiltros(filtros, page = 0) {
    try {
        const token = localStorage.getItem("token");
        if (!token) {
            console.error("No hay token disponible. Redirigiendo al login...");
            window.location.href = "/login";
            return;
        }

        // Construcción dinámica de la URL con parámetros de filtros
        let queryParams = new URLSearchParams({ page, size: 10 });

        if (filtros.codigo) queryParams.append("codigo", filtros.codigo);
        if (filtros.nombre) queryParams.append("nombre", filtros.nombre);
        if (filtros.fechaCreacion) queryParams.append("fechaCreacion", filtros.fechaCreacion);
        if (filtros.fechaModificacion) queryParams.append("fechaModificacion", filtros.fechaModificacion);
        if (filtros.creador) queryParams.append("creador", filtros.creador);
        if (filtros.modificador) queryParams.append("modificador", filtros.modificador);

        const response = await fetch(`${config.backendUrl}/api/departamentos?${queryParams.toString()}`, {
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (!response.ok) throw new Error("Error al obtener departamentos");

        const data = await response.json();

        if (data.content && data.content.length > 0) {
            renderizarDepartamentos(data.content);
        } else {
            document.getElementById("tablaDepartamentos").innerHTML = "<tr><td colspan='8' class='text-center'>No hay departamentos disponibles.</td></tr>";
        }

        actualizarPaginacion(data.totalPages, page);
    } catch (error) {
        console.error("Error cargando departamentos con filtros:", error);
    }
}
