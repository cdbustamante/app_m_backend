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
        http
            .csrf(csrf -> csrf.disable())  // Deshabilita CSRF (usualmente no se usa en APIs REST)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/usuarios/login").permitAll()  // Permitir acceso al login sin autenticación
                .requestMatchers("/api/usuarios/admin/**").hasRole("ADMIN") // Solo los ADMIN pueden acceder
                .requestMatchers("/api/usuarios/user/**").hasAnyRole("USER", "ADMIN") // Solo los usuarios autenticados pueden acceder
                .anyRequest().authenticated() // Cualquier otra ruta requiere autenticación
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // No usar sesiones
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // Agregar el filtro JWT antes de validar credenciales
    
        return http.build();
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
