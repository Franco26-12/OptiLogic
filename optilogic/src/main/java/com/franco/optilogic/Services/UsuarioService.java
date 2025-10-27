package com.franco.optilogic.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.franco.optilogic.Entity.Usuario;
import com.franco.optilogic.Repository.UsuarioRepository;

import dtos.RegistroDTO;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;
        private final PasswordEncoder passwordEncoder;
      public UsuarioService(PasswordEncoder passwordEncoder, UsuarioRepository usuarioRepository) {
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario registrarUsuario(Usuario usuario, String passwordPlano) {
        usuario.setPassword(passwordEncoder.encode(passwordPlano));  // Usas passwordEncoder aquí
        return usuarioRepository.save(usuario);
    }

    
    public Usuario registrarNuevoUsuario(RegistroDTO registroDto) {
        Usuario usuario = new Usuario();
        usuario.setEmail(registroDto.getEmail());
        usuario.setPassword(passwordEncoder.encode(registroDto.getPassword()));
        usuario.setRol(registroDto.getRol()); // Ejemplo: "ADMIN"
        return usuarioRepository.save(usuario);
    }



    public List<Usuario> obtenerTodosLosUsuarios() { return usuarioRepository.findAll(); }
    public Optional<Usuario> obtenerUsuarioPorId(Long id) { return usuarioRepository.findById(id); }
    public Usuario guardarUsuario(Usuario usuario) { return usuarioRepository.save(usuario); }
    public void eliminarUsuario(Long id) { usuarioRepository.deleteById(id); }
}