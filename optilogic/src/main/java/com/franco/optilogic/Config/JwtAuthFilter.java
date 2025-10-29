package com.franco.optilogic.Config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Usa 'OncePerRequestFilter' para garantizar que el filtro se ejecuta solo una vez por petición
public class JwtAuthFilter extends OncePerRequestFilter {

    // Necesitamos el proveedor para leer/validar el token
    @Autowired
    private JwtTokenProvider tokenProvider;

    // Necesitamos este servicio para cargar los detalles del usuario
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 1. Obtener el token JWT de la solicitud (del encabezado Authorization)
        String token = getJwtFromRequest(request);

        // 2. Validar el token y autenticar al usuario
        if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
            // Obtener el email del token
            String email = tokenProvider.getEmailFromJwt(token);

            // Cargar el usuario
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // Crear el objeto de autenticación
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            // Establecer detalles de la solicitud (IP, etc.)
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Establecer el usuario en el contexto de seguridad de Spring
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 3. Continuar con la cadena de filtros de Spring Security
        filterChain.doFilter(request, response);
    }

    // Método auxiliar para extraer el JWT del encabezado
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // Verificar que exista y comience con "Bearer "
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // Devolver el token (saltando los primeros 7 caracteres: "Bearer ")
            return bearerToken.substring(7);
        }
        return null;
    }
}
