package com.franco.optilogic.Services;

import com.franco.optilogic.Entity.Usuario; 
import com.franco.optilogic.Repository.UsuarioRepository; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository; 
    private final PasswordEncoder passwordEncoder; 

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public Usuario registrarUsuario(Usuario usuario, String rawPassword) {
 
        String hashedPassword = passwordEncoder.encode(rawPassword);
        usuario.setPassword(hashedPassword); 
        
       
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
             throw new RuntimeException("El email ya está registrado.");
        }

        return usuarioRepository.save(usuario);
    }
}
