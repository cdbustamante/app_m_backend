package ec.gob.mtop.conexion.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration // Indica que esta clase es de configuración en Spring
@EnableWebSecurity // Habilita Spring Security
public class SecurityConfig {

    private final JwtFilter jwtFilter; // Filtro para validar los tokens JWT

    // Constructor para inyectar JwtFilter
    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * 🔹 Configuración de seguridad principal de Spring Security.
     * Se define qué rutas necesitan autenticación y cómo se gestionan las sesiones.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ✅ Habilita CORS usando un Bean separado
            .csrf(csrf -> csrf.disable()) // ✅ Deshabilita CSRF porque se usa JWT
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // ✅ Define que las sesiones son sin estado
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/usuarios/perfil").authenticated() // ✅ SOLO autenticados pueden acceder a su perfil
                .requestMatchers("/api/usuarios/login").permitAll()  // Permitir login sin autenticación
                .requestMatchers("/api/usuarios/admin/**").hasRole("ADMIN") // Solo ADMIN accede a rutas de usuario
                .requestMatchers("/api/departamentos").authenticated() // Todos los usuarios autenticados pueden ver los departamentos
                .requestMatchers("/api/departamentos/**").hasRole("ADMIN") // Solo ADMIN puede modificar departamentos
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // ✅ Agregar filtro JWT antes de UsernamePassword
            .build();
    }

    /**
     * 🔹 Configuración global de CORS.
     * Permite solicitudes desde el frontend y habilita métodos seguros.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // ✅ Permite solicitudes desde el frontend
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // ✅ Métodos permitidos
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type")); // ✅ Permitir token en headers
        configuration.setAllowCredentials(true); // ✅ Permitir credenciales (tokens)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 🔹 Configuración del AuthenticationManager.
     * Este se encarga de autenticar usuarios con el UserDetailsService y BCrypt.
     */
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService); // Usa UserDetailsService para cargar los usuarios
        provider.setPasswordEncoder(passwordEncoder()); // Usa BCrypt para comparar contraseñas cifradas
        return new ProviderManager(List.of(provider));
    }

    /**
     * 🔹 Configuración del cifrado de contraseñas con BCrypt.
     * Spring Security usa esto para almacenar y verificar contraseñas de forma segura.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
