package com.franco.optilogic.Services;

import com.franco.optilogic.Entity.Admin; // Asegúrate de que esta entidad sea la correcta
import com.franco.optilogic.Repository.AdminRepository; // O la interfaz de tu repositorio
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    // Cambia AdminRepository por el repositorio de tu entidad de usuario si es diferente
    private final AdminRepository adminRepository; 
    private final PasswordEncoder passwordEncoder; 

    @Autowired
    public UsuarioService(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

   
    public Admin registrarUsuario(Admin admin, String rawPassword) {
 
        String hashedPassword = passwordEncoder.encode(rawPassword);
        admin.setPassword(hashedPassword); 
        

        return adminRepository.save(admin);
    }

}
