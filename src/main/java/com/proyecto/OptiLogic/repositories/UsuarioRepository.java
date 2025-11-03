package com.proyecto.OptiLogic.repositories;

import com.proyecto.OptiLogic.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByCedula(String cedula);
    
    Optional<Usuario> findByEmail(String email);
    
    Optional<Usuario> findByTelefono(String telefono);
    
    List<Usuario> findByActivo(boolean activo);
    
    @Query("SELECT u FROM Usuario u WHERE u.rol = :rol")
    List<Usuario> findByRol(@Param("rol") String rol);
    
    @Query("SELECT u FROM Usuario u WHERE TYPE(u) = :tipoUsuario")
    List<Usuario> findByTipoUsuario(@Param("tipoUsuario") Class<?> tipoUsuario);
    
    boolean existsByCedula(String cedula);
    
    boolean existsByEmail(String email);
    
    boolean existsByTelefono(String telefono);
}