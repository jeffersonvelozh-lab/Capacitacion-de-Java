package com.gestiontarea.demo.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.gestiontarea.demo.infrastructure.adapter.in.security.JwtAuthenticationFilter;

/**
 * COMPLETO (borrador funcional): define que rutas son publicas (login/registro)
 * y cuales requieren JWT valido. Sesion STATELESS porque la autenticacion
 * viaja en el token en cada request, no en una sesion de servidor.
 *
 * cuando implementes roles: agrega reglas mas finas, ej:
 *   .requestMatchers(HttpMethod.DELETE, "/api/proyectos/**").hasRole("ADMIN")
 * y anota los metodos de los use cases o controllers con @PreAuthorize
 * (requiere @EnableMethodSecurity, ya activado abajo).
 */

@Configuration 
@EnableWebSecurity 
@EnableMethodSecurity 
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // no hay sesion/cookies -> no hace falta CSRF
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
