package com.franco.optilogic.Config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // Obtiene la clave secreta desde application.properties
    @Value("${app.jwt-secret}")
    private String jwtSecret;

    // Obtiene el tiempo de expiración desde application.properties
    @Value("${app.jwt-expiration-milliseconds}")
    private long jwtExpirationDate;

    // Método auxiliar para obtener la clave secreta
    private Key key() {
        // Usa el Decoders.BASE64 para asegurar que la clave sea segura
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // Genera el JWT
    public String generateToken(Authentication authentication, String role) {
        String username = authentication.getName(); // En este caso es el email

        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role) // Añade el rol al cuerpo del token
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(key(), SignatureAlgorithm.HS512) // Especificar algoritmo
                .compact();
    }

    // Obtiene el email (subject) del token
    public String getEmailFromJwt(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // Obtiene el rol del token (útil para la seguridad)
    public String getRoleFromJwt(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("role", String.class);
    }

    // Valida el token JWT
    public boolean validateToken(String token) throws JwtException {
        try {
            Jwts.parser()
                    .setSigningKey(key())
                    .build()
                    .parse(token);
            return true;
        } catch (MalformedJwtException ex) {
            throw new JwtException("Token JWT inválido");
        } catch (ExpiredJwtException ex) {
            throw new JwtException("Token JWT expirado");
        } catch (UnsupportedJwtException ex) {
            throw new JwtException("Token JWT no soportado");
        } catch (IllegalArgumentException ex) {
            throw new JwtException("Claims string vacío o nulo");
        }
    }
}
