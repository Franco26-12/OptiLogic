package com.franco.optilogic.Services;

import com.franco.optilogic.Entity.Admin;
import com.franco.optilogic.Entity.Repartidor;
import com.franco.optilogic.Entity.Usuario; 
import com.franco.optilogic.Repository.UsuarioRepository; 

import dtos.GestionUsuarioDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        
        // Validar email único
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
             throw new RuntimeException("El email ya está registrado.");
        }
        
        // Validar cédula única
        if (usuarioRepository.existsByCedula(usuario.getCedula())) {
             throw new RuntimeException("La cédula ya está registrada.");
        }

        return usuarioRepository.save(usuario);
    }

    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerPorCedula(String cedula) {
        return usuarioRepository.findByCedula(cedula);
    }

    public Usuario crearUsuario(GestionUsuarioDTO dto) {
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new RuntimeException("La contraseña es obligatoria para crear un usuario.");
        }

        Usuario usuario;
        if ("ADMIN".equalsIgnoreCase(dto.getRol())) {
            Admin admin = new Admin();
            admin.setRol("ADMIN");
            usuario = admin;
        } else if ("REPARTIDOR".equalsIgnoreCase(dto.getRol())) {
            Repartidor repartidor = new Repartidor();
            repartidor.setRol("REPARTIDOR");
            repartidor.setEstado(dto.getEstado() != null ? dto.getEstado() : "DISPONIBLE");
            usuario = repartidor;
        } else {
            throw new RuntimeException("Rol inválido. Use ADMIN o REPARTIDOR.");
        }

        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setCedula(dto.getCedula());
        usuario.setEmail(dto.getEmail());

        return registrarUsuario(usuario, dto.getPassword());
    }

    public Usuario actualizarUsuario(Long id, GestionUsuarioDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!usuario.getRol().equalsIgnoreCase(dto.getRol())) {
            throw new RuntimeException("No se puede cambiar el rol del usuario desde esta operación.");
        }

        if (!usuario.getEmail().equalsIgnoreCase(dto.getEmail())
                && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("El email ya está registrado.");
        }

        if (!usuario.getCedula().equalsIgnoreCase(dto.getCedula())
                && usuarioRepository.existsByCedula(dto.getCedula())) {
            throw new RuntimeException("La cédula ya está registrada.");
        }

        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setCedula(dto.getCedula());
        usuario.setEmail(dto.getEmail());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (usuario instanceof Repartidor repartidor) {
            repartidor.setEstado(dto.getEstado() != null ? dto.getEstado() : repartidor.getEstado());
        }

        return usuarioRepository.save(usuario);
    }

    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }
}
