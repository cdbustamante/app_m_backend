document.addEventListener("DOMContentLoaded", async function () {
    const navbarContainer = document.getElementById("navbar-container");
    const loginButton = document.querySelector(".btn-primary"); // Botón de iniciar sesión

    // Recuperar token JWT desde localStorage
    const token = localStorage.getItem("token");

    // Si hay token, ocultar el botón de iniciar sesión
    if (token) {
        loginButton.style.display = "none";
        navbarContainer.innerHTML = await fetchNavbar("navbar-authenticated.html"); // Navbar para usuarios logueados
    } else {
        loginButton.style.display = "block";
        navbarContainer.innerHTML = await fetchNavbar("navbar-default.html"); // Navbar para usuarios no autenticados
    }
});

// Función para cargar la navbar
async function fetchNavbar(navbarFile) {
    const response = await fetch(`/public/navbars/${navbarFile}`);
    return response.ok ? await response.text() : "<p>Error al cargar la navegación.</p>";
}
