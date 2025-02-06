package ec.gob.mtop.conexion.seguridad;

import ec.gob.mtop.conexion.modelo.Usuario;
import ec.gob.mtop.conexion.servicio.UsuarioService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioService usuarioService;

    public UserDetailsServiceImpl(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioService.obtenerPorUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // 🔹 Convertir el rol del usuario en una lista de `GrantedAuthority`
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRolUsuario()));

        return org.springframework.security.core.userdetails.User
                .withUsername(usuario.getUsernameUsuario()) // Usuario
                .password(usuario.getPasswordUsuario()) // Contraseña
                .authorities(authorities) // Lista de roles en formato correcto
                .build();
    }
}
