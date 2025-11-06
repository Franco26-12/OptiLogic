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
import com.franco.optilogic.Repository.RepartidorRepository;
import com.franco.optilogic.Repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Objects;

@Service
public class EnvioService {
    @Autowired private EnvioRepository envioRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private RepartidorRepository repartidorRepository;

    public List<Envio> obtenerTodosLosEnvios() { return envioRepository.findAll(); }
    public Optional<Envio> obtenerEnvioPorId(Long id) {
        Long idSeguro = Objects.requireNonNull(id, "El id del envío es obligatorio");
        return envioRepository.findById(idSeguro);
    }
    
    public List<Envio> obtenerEnviosPendientes() {
        return envioRepository.findByRepartidorIsNull();
    }
    
    public List<Envio> obtenerEnviosPorRepartidor(Long repartidorId) {
        Long idSeguro = Objects.requireNonNull(repartidorId, "El id del repartidor es obligatorio");
        Repartidor repartidor = (Repartidor) usuarioRepository.findById(idSeguro)
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
        List<Long> productoIds = Optional.ofNullable(dto.getProductoIds())
            .filter(list -> !list.isEmpty())
            .map(ArrayList::new)
            .orElseThrow(() -> new RuntimeException("Debe seleccionar al menos un producto"));

        List<Producto> productos = productoRepository.findAllById(productoIds);
        if (productos.size() != productoIds.size()) { 
            throw new RuntimeException("Uno o más productos no fueron encontrados"); 
        }

        Envio nuevoEnvio = new Envio();
        nuevoEnvio.setCliente(cliente);
        nuevoEnvio.setProductos(productos);
        nuevoEnvio.setDireccionDestino(dto.getDireccionDestino());
        nuevoEnvio.setEstado("PENDIENTE");
        nuevoEnvio.setCodigoQR(generarCodigoQR());

        Long repartidorId = dto.getRepartidorId();
        if (repartidorId != null) {
            Long idSeguro = Objects.requireNonNull(repartidorId);
            Repartidor repartidor = (Repartidor) usuarioRepository.findById(idSeguro)
                .orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));

            if (!"DISPONIBLE".equals(repartidor.getEstado())) {
                throw new RuntimeException("El repartidor no está disponible");
            }

            nuevoEnvio.setRepartidor(repartidor);
            nuevoEnvio.setEstado("EN_TRANSITO");
            repartidor.setEstado("OCUPADO");
            usuarioRepository.save(repartidor);
        }

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

    public List<Repartidor> obtenerRepartidoresDisponibles() {
        return repartidorRepository.findByEstado("DISPONIBLE");
    }
}