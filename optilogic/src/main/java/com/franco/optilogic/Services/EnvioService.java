package com.franco.optilogic.Services;

import dtos.CrearEnvioDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.franco.optilogic.Entity.Cliente;
import com.franco.optilogic.Entity.Envio;
import com.franco.optilogic.Entity.Producto;
import com.franco.optilogic.Entity.Repartidor;
import com.franco.optilogic.Repository.EnvioRepository;
import com.franco.optilogic.Repository.ProductoRepository;
import com.franco.optilogic.Repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EnvioService {
    @Autowired private EnvioRepository envioRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ProductoRepository productoRepository;

    public List<Envio> obtenerTodosLosEnvios() { return envioRepository.findAll(); }
    public Optional<Envio> obtenerEnvioPorId(Long id) { return envioRepository.findById(id); }
    
    public List<Envio> obtenerEnviosPendientes() {
        return envioRepository.findByRepartidorIsNull();
    }
    
    public List<Envio> obtenerEnviosPorRepartidor(Long repartidorId) {
        Repartidor repartidor = (Repartidor) usuarioRepository.findById(repartidorId)
            .orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));
        return envioRepository.findByRepartidor(repartidor);
    }
    
    private String generarCodigoQR() {
        return "QR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Transactional
    public Envio crearEnvio(CrearEnvioDTO dto) {
        Cliente cliente = (Cliente) usuarioRepository.findById(dto.getClienteId())
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        List<Producto> productos = productoRepository.findAllById(dto.getProductoIds());
        if (productos.size() != dto.getProductoIds().size()) { 
            throw new RuntimeException("Uno o más productos no fueron encontrados"); 
        }

        Envio nuevoEnvio = new Envio();
        nuevoEnvio.setCliente(cliente);
        nuevoEnvio.setProductos(productos);
        nuevoEnvio.setDireccionDestino(dto.getDireccionDestino());
        nuevoEnvio.setEstado("PENDIENTE");
        nuevoEnvio.setCodigoQR(generarCodigoQR());

        return envioRepository.save(nuevoEnvio);
    }
    
    @Transactional
    public Envio asignarRepartidor(Long envioId, Long repartidorId) {
        Envio envio = envioRepository.findById(envioId)
            .orElseThrow(() -> new RuntimeException("Envío no encontrado"));
        Repartidor repartidor = (Repartidor) usuarioRepository.findById(repartidorId)
            .orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));
        
        if (!"DISPONIBLE".equals(repartidor.getEstado())) {
            throw new RuntimeException("El repartidor no está disponible");
        }
        
        envio.setRepartidor(repartidor);
        envio.setEstado("EN_TRANSITO");
        repartidor.setEstado("OCUPADO");
        
        usuarioRepository.save(repartidor);
        return envioRepository.save(envio);
    }
    
    @Transactional
    public Envio confirmarEntrega(String codigoQR) {
        Envio envio = envioRepository.findByCodigoQR(codigoQR)
            .orElseThrow(() -> new RuntimeException("Envío no encontrado con el código QR proporcionado"));
        
        if (!"EN_TRANSITO".equals(envio.getEstado())) {
            throw new RuntimeException("El envío no está en tránsito");
        }
        
        envio.setEstado("ENTREGADO");
        envio.setFechaEntrega(LocalDateTime.now());
        
        // Liberar repartidor
        if (envio.getRepartidor() != null) {
            Repartidor repartidor = envio.getRepartidor();
            repartidor.setEstado("DISPONIBLE");
            usuarioRepository.save(repartidor);
        }
        
        return envioRepository.save(envio);
    }
}