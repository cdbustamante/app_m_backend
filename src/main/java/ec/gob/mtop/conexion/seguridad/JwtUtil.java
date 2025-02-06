package ec.gob.mtop.conexion.seguridad;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component // Anotación de Spring que permite inyectar esta clase en otros lugares con @Autowired
public class JwtUtil {

    // 🔹 La clave secreta se obtiene desde application.properties
    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * 🔹 Convierte la clave secreta (que está en Base64 en application.properties)
     * en un objeto Key válido para firmar y validar JWT.
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); // Decodifica la clave secreta de Base64 a bytes
        return Keys.hmacShaKeyFor(keyBytes); // Genera la clave de firma HMAC con SHA-256
    }

    /**
     * 🔹 Extrae el nombre de usuario (subject) de un token JWT.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 🔹 Extrae la fecha de expiración de un token JWT.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 🔹 Extrae cualquier dato (claim) dentro del token JWT, usando una función.
     * @param claimsResolver Función que determina qué dato (claim) extraer.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token); // Extrae todos los claims del token
        return claimsResolver.apply(claims); // Aplica la función para obtener el dato específico
    }

    /**
     * 🔹 Extrae todos los claims (datos) de un token JWT.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // Usa la clave secreta para validar el token
                .build()
                .parseClaimsJws(token)
                .getBody(); // Obtiene el contenido del JWT
    }

    /**
     * 🔹 Valida si un token JWT es válido y pertenece al usuario correcto.
     */
    public Boolean validarToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token); // Extrae el usuario del token
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)); // Verifica usuario y expiración
    }

    /**
     * 🔹 Verifica si un token ha expirado.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date()); // Compara la fecha de expiración con la actual
    }

    /**
     * 🔹 Genera un token JWT con un usuario como "subject" y 1 hora de validez.
     */
    public String generarToken(String username) {
        return Jwts.builder()
                .setSubject(username) // El usuario será el "subject" del token
                .setIssuedAt(new Date()) // Fecha de creación del token
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // Expira en 1 hora
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Firma el token con HMAC-SHA256
                .compact(); // Genera el token en formato String
    }
}
