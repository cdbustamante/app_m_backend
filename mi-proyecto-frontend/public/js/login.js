document.addEventListener("DOMContentLoaded", function () {
    const loginForm = document.getElementById("loginForm");
    const errorMessage = document.getElementById("error-message");

    loginForm.addEventListener("submit", async function (event) {
        event.preventDefault(); // Evita el envío por defecto del formulario

        const username = document.getElementById("username").value.trim();
        const password = document.getElementById("password").value.trim();

        if (!username || !password) {
            errorMessage.textContent = "Todos los campos son obligatorios.";
            errorMessage.style.display = "block";
            return;
        }

        try {
            const response = await fetch(`${config.backendUrl}/api/usuarios/login`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ username, password }),
            });

            if (!response.ok) {
                throw new Error("Credenciales incorrectas");
            }

            const data = await response.json();
            localStorage.setItem("token", data.token); // Guardar el token en localStorage

            // Redirigir al home después del login exitoso
            window.location.href = "/";
        } catch (error) {
            errorMessage.textContent = "Usuario o contraseña incorrectos.";
            errorMessage.style.display = "block";
        }
    });
});
