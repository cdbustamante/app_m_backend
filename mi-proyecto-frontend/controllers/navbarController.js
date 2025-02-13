document.addEventListener("DOMContentLoaded", async function() {
    const navbarContainer = document.getElementById("navbar-container");
    const apiUrl = config.backendUrl + "/api/usuarios/perfil";

    // Recuperar token JWT desde localStorage
    const token = localStorage.getItem("token");

    if (!token) {
        navbarContainer.innerHTML = await fetchNavbar("navbar-default.html");
        return;
    }

    try {
        const response = await fetch(apiUrl, {
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (!response.ok) throw new Error("Token inválido");

        const user = await response.json();
        
        // Cargar la navbar según el rol
        let navbarFile;
        switch (user.rol) {
            case "ADMIN":
                navbarFile = "navbar-admin.html";
                break;
            case "GESTOR":
                navbarFile = "navbar-gestor.html";
                break;
            case "TECNICO":
                navbarFile = "navbar-tecnico.html";
                break;
            default:
                navbarFile = "navbar-default.html";
        }

        navbarContainer.innerHTML = await fetchNavbar(navbarFile);
    } catch (error) {
        console.error("Error al validar el token:", error);
        navbarContainer.innerHTML = await fetchNavbar("navbar-default.html");
        localStorage.removeItem("token");
    }
});

// Función para cargar la navbar
async function fetchNavbar(navbarFile) {
    const response = await fetch(`../public/navbars/${navbarFile}`);
    return response.ok ? await response.text() : "<p>Error al cargar la navegación.</p>";
}

// Función para cerrar sesión
function logout() {
    localStorage.removeItem("token");
    window.location.href = "login.html";
}
