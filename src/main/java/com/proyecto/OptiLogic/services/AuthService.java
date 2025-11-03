package com.proyecto.OptiLogic.services;

import com.proyecto.OptiLogic.dtos.*;
import com.proyecto.OptiLogic.entities.*;
import com.proyecto.OptiLogic.repositories.*;
import com.proyecto.OptiLogic.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Transactional
    public Map<String, Object> login(LoginDTO loginDTO) {
        // Buscar usuario por cédula
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCedula(loginDTO.getCedula());
        
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }
        
        Usuario usuario = usuarioOpt.get();
        
        // Verificar contraseña
        if (!passwordEncoder.matches(loginDTO.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }
        
        // Verificar si está activo
        if (!usuario.isActivo()) {
            throw new RuntimeException("Usuario inactivo");
        }
        
        // Actualizar último acceso
        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);
        
        // Generar token JWT
        String token = jwtUtil.generateToken(usuario.getCedula(), usuario.getRol());
        
        // Preparar respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("usuario", convertirUsuarioADTO(usuario));
        
        return response;
    }
    
    @Transactional
    public Map<String, Object> registrarAdmin(RegistroDTO registroDTO) {
        // Verificar si la cédula ya existe
        if (usuarioRepository.findByCedula(registroDTO.getCedula()).isPresent()) {
            throw new RuntimeException("La cédula ya está registrada");
        }
        
        // Crear nuevo admin
        Admin admin = new Admin();
        admin.setCedula(registroDTO.getCedula());
        admin.setNombre(registroDTO.getNombre());
        admin.setApellido(registroDTO.getApellido());
        admin.setPassword(passwordEncoder.encode(registroDTO.getPassword()));
        admin.setTelefono(registroDTO.getTelefono());
        admin.setEmail(registroDTO.getEmail());
        admin.setEsSuperAdmin(registroDTO.isEsSuperAdmin());
        admin.setNivelAcceso(registroDTO.isEsSuperAdmin() ? 3 : 1);
        admin.setActivo(true);
        admin.setFechaRegistro(LocalDateTime.now());
        
        Admin adminGuardado = usuarioRepository.save(admin);
        
        // Generar token JWT
        String token = jwtUtil.generateToken(adminGuardado.getCedula(), adminGuardado.getRol());
        
        // Preparar respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("usuario", convertirUsuarioADTO(adminGuardado));
        response.put("mensaje", "Admin registrado exitosamente");
        
        return response;
    }
    
    @Transactional
    public RepartidorDTO registrarRepartidor(RepartidorDTO repartidorDTO, String passwordTemporal, Long adminCreadorId) {
        // Verificar si la cédula ya existe
        if (usuarioRepository.findByCedula(repartidorDTO.getCedula()).isPresent()) {
            throw new RuntimeException("La cédula ya está registrada");
        }
        
        // Buscar admin creador
        Admin adminCreador = (Admin) usuarioRepository.findById(adminCreadorId)
                .orElseThrow(() -> new RuntimeException("Admin creador no encontrado"));
        
        // Crear nuevo repartidor
        Repartidor repartidor = new Repartidor();
        repartidor.setCedula(repartidorDTO.getCedula());
        repartidor.setNombre(repartidorDTO.getNombre());
        repartidor.setApellido(repartidorDTO.getApellido());
        repartidor.setPassword(passwordEncoder.encode(passwordTemporal));
        repartidor.setTelefono(repartidorDTO.getTelefono());
        repartidor.setEmail(repartidorDTO.getEmail());
        repartidor.setLicenciaConducir(repartidorDTO.getLicenciaConducir());
        repartidor.setVehiculoAsignado(repartidorDTO.getVehiculoAsignado());
        repartidor.setZonaAsignada(repartidorDTO.getZonaAsignada());
        repartidor.setEstado(Repartidor.EstadoRepartidor.DISPONIBLE);
        repartidor.setAdminCreador(adminCreador);
        repartidor.setActivo(true);
        repartidor.setFechaRegistro(LocalDateTime.now());
        
        Repartidor repartidorGuardado = usuarioRepository.save(repartidor);
        
        return convertirRepartidorADTO(repartidorGuardado);
    }
    
    @Transactional
    public Cliente registrarCliente(Cliente cliente, String password) {
        // Verificar si la cédula ya existe
        if (usuarioRepository.findByCedula(cliente.getCedula()).isPresent()) {
            throw new RuntimeException("La cédula ya está registrada");
        }
        
        cliente.setPassword(passwordEncoder.encode(password));
        cliente.setActivo(true);
        cliente.setFechaRegistro(LocalDateTime.now());
        
        return usuarioRepository.save(cliente);
    }
    
    @Transactional
    public void cambiarPassword(Long usuarioId, String passwordActual, String passwordNuevo) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Verificar contraseña actual
        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }
        
        // Actualizar contraseña
        usuario.setPassword(passwordEncoder.encode(passwordNuevo));
        usuarioRepository.save(usuario);
    }
    
    @Transactional
    public void resetearPassword(String cedula, String nuevaPasswordTemporal) {
        Usuario usuario = usuarioRepository.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        usuario.setPassword(passwordEncoder.encode(nuevaPasswordTemporal));
        usuarioRepository.save(usuario);
    }
    
    public Usuario validarToken(String token) {
        String cedula = jwtUtil.extractCedula(token);
        return usuarioRepository.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Usuario no válido"));
    }
    
    private Map<String, Object> convertirUsuarioADTO(Usuario usuario) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", usuario.getId());
        dto.put("cedula", usuario.getCedula());
        dto.put("nombre", usuario.getNombre());
        dto.put("apellido", usuario.getApellido());
        dto.put("email", usuario.getEmail());
        dto.put("telefono", usuario.getTelefono());
        dto.put("rol", usuario.getRol());
        dto.put("activo", usuario.isActivo());
        dto.put("fechaRegistro", usuario.getFechaRegistro());
        dto.put("ultimoAcceso", usuario.getUltimoAcceso());
        
        if (usuario instanceof Admin) {
            Admin admin = (Admin) usuario;
            dto.put("esSuperAdmin", admin.isEsSuperAdmin());
            dto.put("nivelAcceso", admin.getNivelAcceso());
        } else if (usuario instanceof Repartidor) {
            Repartidor repartidor = (Repartidor) usuario;
            dto.put("estado", repartidor.getEstado().name());
            dto.put("licenciaConducir", repartidor.getLicenciaConducir());
            dto.put("vehiculoAsignado", repartidor.getVehiculoAsignado());
            dto.put("zonaAsignada", repartidor.getZonaAsignada());
        } else if (usuario instanceof Cliente) {
            Cliente cliente = (Cliente) usuario;
            dto.put("direccion", cliente.getDireccion());
            dto.put("ciudad", cliente.getCiudad());
            dto.put("codigoPostal", cliente.getCodigoPostal());
        }
        
        return dto;
    }
    
    private RepartidorDTO convertirRepartidorADTO(Repartidor repartidor) {
        RepartidorDTO dto = new RepartidorDTO();
        dto.setId(repartidor.getId());
        dto.setCedula(repartidor.getCedula());
        dto.setNombre(repartidor.getNombre());
        dto.setApellido(repartidor.getApellido());
        dto.setTelefono(repartidor.getTelefono());
        dto.setEmail(repartidor.getEmail());
        dto.setEstado(repartidor.getEstado().name());
        dto.setLicenciaConducir(repartidor.getLicenciaConducir());
        dto.setVehiculoAsignado(repartidor.getVehiculoAsignado());
        dto.setZonaAsignada(repartidor.getZonaAsignada());
        dto.setFechaRegistro(repartidor.getFechaRegistro());
        dto.setUltimoAcceso(repartidor.getUltimoAcceso());
        dto.setActivo(repartidor.isActivo());
        
        return dto;
    }
}
