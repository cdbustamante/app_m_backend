package ec.gob.mtop.conexion.seguridad;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Spring detecta esta clase como un componente de seguridad
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil; // Herramienta para validar y extraer datos del token
    private final UserDetailsService userDetailsService; // Servicio para cargar detalles del usuario

    // Constructor que inyecta JwtUtil y UserDetailsService
    public JwtFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * 🔹 Este método intercepta todas las peticiones HTTP y verifica si hay un token JWT válido.
     * Si el token es válido, se autentica el usuario en el contexto de seguridad.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 📌 1️⃣ Leer el encabezado "Authorization"
        String authHeader = request.getHeader("Authorization");

        // 📌 2️⃣ Verificar si el token está presente y tiene el formato correcto ("Bearer <token>")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response); // Si no hay token, continuar sin autenticar
            return;
        }

        // 📌 3️⃣ Extraer el token JWT (quitando "Bearer ")
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token); // Obtener el usuario del token

        // 📌 4️⃣ Si hay un usuario en el token y aún no está autenticado en el contexto
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Cargar el usuario desde la base de datos usando UserDetailsService
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 📌 5️⃣ Validar el token con la información del usuario
            if (jwtUtil.validarToken(token, userDetails)) {

                // 📌 6️⃣ Crear un objeto de autenticación y configurarlo en el contexto de seguridad
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // 📌 7️⃣ Continuar con el siguiente filtro en la cadena
        chain.doFilter(request, response);
    }
}
