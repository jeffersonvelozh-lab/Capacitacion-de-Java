package com.gestiontarea.demo.infrastructure.adapter.in.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.gestiontarea.demo.application.port.out.TokenGeneratorPort;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * COMPLETO (borrador funcional): intercepta el header "Authorization: Bearer <token>",
 * valida el JWT y, si es valido, deja al usuario autenticado en el SecurityContext
 * para el resto del request.
 *
 * TODO: hoy solo mete el subject (email) como "principal" y le da una authority
 * generica. Cuando implementes ServicioAutenticacion/UsuarioRepository completos,
 * mejora esto para leer el claim "rol" del token y mapearlo a
 * new SimpleGrantedAuthority("ROLE_" + rol) -- eso es lo que @PreAuthorize("hasRole('ADMIN')")
 * necesita para funcionar.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenGeneratorPort tokenGenerator;

    public JwtAuthenticationFilter(TokenGeneratorPort tokenGenerator) {
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                Optional<String> email = tokenGenerator.validarYObtenerSubject(token);

                // TODO: reemplazar authority fija por el rol real del claim del token
                var authentication = new UsernamePasswordAuthenticationToken(
                        email, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (RuntimeException e) {
                // Token invalido: no autenticamos, dejamos que Spring Security
                // decida (401/403) segun la regla de la ruta.
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
