package dtos;

public class JwtAuthResponseDTO {
    private String accessToken;
    private String tokenType = "Bearer";
    private String role; // Incluimos el rol para que el frontend lo use
    private String nombre;

    public JwtAuthResponseDTO(String accessToken, String role, String nombre) {
        this.accessToken = accessToken;
        this.role = role;
        this.nombre = nombre;
    }

    // Getters y Setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}