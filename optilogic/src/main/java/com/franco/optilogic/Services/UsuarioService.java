package com.franco.optilogic.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.franco.optilogic.Entity.Usuario;
import com.franco.optilogic.Repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> obtenerTodosLosUsuarios() { return usuarioRepository.findAll(); }
    public Optional<Usuario> obtenerUsuarioPorId(Long id) { return usuarioRepository.findById(id); }
    public Usuario guardarUsuario(Usuario usuario) { return usuarioRepository.save(usuario); }
    public void eliminarUsuario(Long id) { usuarioRepository.deleteById(id); }
}