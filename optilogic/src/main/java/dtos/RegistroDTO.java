package dtos;

public class RegistroDTO {
    private String email;
    private String password;
    private String rol; // Puedes inicializar en "ADMIN" o definir setter

    // Constructor vacío
    public RegistroDTO() {}

    // Getters y setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
