const express = require("express");
const path = require("path");

const app = express();
const PORT = 3000;

// ✅ Servir archivos estáticos correctamente
app.use("/public", express.static(path.join(__dirname, "public")));
app.use("/controllers", express.static(path.join(__dirname, "controllers")));
app.use("/config", express.static(path.join(__dirname, "config")));
app.use("/views", express.static(path.join(__dirname, "views"))); 

// ✅ Función para servir vistas dinámicamente
app.get("/:page?", (req, res) => {
    const page = req.params.page || "home"; // Si no hay parámetro, carga "home"
    const filePath = path.join(__dirname, "views", `${page}.html`);
    res.sendFile(filePath, (err) => {
        if (err) {
            res.status(404).sendFile(path.join(__dirname, "views", "404.html")); // Página de error 404
        }
    });
});

// Iniciar el servidor
app.listen(PORT, () => {
  console.log(`✅ Servidor corriendo en http://localhost:${PORT}`);
});
