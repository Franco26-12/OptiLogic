package com.proyecto.OptiLogic.services;

import com.proyecto.OptiLogic.dtos.*;
import com.proyecto.OptiLogic.entities.*;
import com.proyecto.OptiLogic.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EnvioService {

    @Autowired
    private EnvioRepository envioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    public List<Envio> obtenerTodosLosEnvios() {
        return envioRepository.findAll();
    }

    public Optional<Envio> obtenerEnvioPorId(Long id) {
        return envioRepository.findById(id);
    }
    
    public Optional<Envio> obtenerEnvioPorNumeroSeguimiento(String numeroSeguimiento) {
        return envioRepository.findByNumeroSeguimiento(numeroSeguimiento);
    }
    
    public Optional<Envio> obtenerEnvioPorCodigoQR(String codigoQR) {
        return envioRepository.findByCodigoQR(codigoQR);
    }
    
    public List<Envio> obtenerEnviosPorRepartidor(Long repartidorId) {
        return envioRepository.findByRepartidorId(repartidorId);
    }
    
    public List<Envio> obtenerEnviosPorCliente(Long clienteId) {
        return envioRepository.findByClienteId(clienteId);
    }
    
    public List<Envio> obtenerEnviosPorEstado(Envio.EstadoEnvio estado) {
        return envioRepository.findByEstado(estado);
    }

    @Transactional
    public Envio crearEnvio(CrearEnvioDTO crearEnvioDTO, Long adminId) {
        // Buscar el Cliente por su ID
        Cliente cliente = (Cliente) usuarioRepository.findById(crearEnvioDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        // Buscar el Admin creador
        Admin adminCreador = (Admin) usuarioRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin no encontrado"));

        // Buscar Repartidor si fue asignado
        Repartidor repartidor = null;
        if (crearEnvioDTO.getRepartidorId() != null) {
            repartidor = (Repartidor) usuarioRepository.findById(crearEnvioDTO.getRepartidorId())
                    .orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));
        }

        // Buscar y validar productos con cantidades
        Map<Producto, Integer> productosConCantidad = new HashMap<>();
        for (Map.Entry<Long, Integer> entry : crearEnvioDTO.getProductosConCantidad().entrySet()) {
            Producto producto = productoRepository.findById(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + entry.getKey()));
            
            // Validar stock disponible
            if (producto.getStockActual() < entry.getValue()) {
                throw new RuntimeException("Stock insuficiente para producto: " + producto.getNombre());
            }
            
            productosConCantidad.put(producto, entry.getValue());
        }

        // Crear el nuevo envío
        Envio nuevoEnvio = new Envio();
        nuevoEnvio.setCliente(cliente);
        nuevoEnvio.setAdminCreador(adminCreador);
        nuevoEnvio.setDireccionOrigen(crearEnvioDTO.getDireccionOrigen());
        nuevoEnvio.setDireccionDestino(crearEnvioDTO.getDireccionDestino());
        nuevoEnvio.setCiudadDestino(crearEnvioDTO.getCiudadDestino());
        nuevoEnvio.setCodigoPostalDestino(crearEnvioDTO.getCodigoPostalDestino());
        nuevoEnvio.setCostoEnvio(crearEnvioDTO.getCostoEnvio());
        nuevoEnvio.setPrioridad(crearEnvioDTO.getPrioridad());
        nuevoEnvio.setFechaEntregaEstimada(crearEnvioDTO.getFechaEntregaEstimada());
        nuevoEnvio.setObservaciones(crearEnvioDTO.getObservaciones());
        nuevoEnvio.setProductos(new ArrayList<>(productosConCantidad.keySet()));
        nuevoEnvio.setCantidadPorProducto(productosConCantidad);
        
        if (repartidor != null) {
            nuevoEnvio.setRepartidor(repartidor);
            nuevoEnvio.setEstado(Envio.EstadoEnvio.ASIGNADO);
            nuevoEnvio.setFechaAsignacion(LocalDateTime.now());
            
            // Actualizar estado del repartidor
            repartidor.setEstado(Repartidor.EstadoRepartidor.OCUPADO);
            usuarioRepository.save(repartidor);
        } else {
            nuevoEnvio.setEstado(Envio.EstadoEnvio.PENDIENTE);
        }

        // Actualizar stock de productos
        for (Map.Entry<Producto, Integer> entry : productosConCantidad.entrySet()) {
            Producto producto = entry.getKey();
            producto.setStockActual(producto.getStockActual() - entry.getValue());
            productoRepository.save(producto);
        }

        return envioRepository.save(nuevoEnvio);
    }
    
    @Transactional
    public Envio actualizarEstadoEnvio(ActualizarEstadoEnvioDTO dto) {
        Envio envio = envioRepository.findById(dto.getEnvioId())
                .orElseThrow(() -> new RuntimeException("Envío no encontrado"));
        
        // Si se confirma por QR, validar código
        if (dto.isConfirmadoPorQR() && dto.getCodigoQR() != null) {
            if (!envio.getCodigoQR().equals(dto.getCodigoQR())) {
                throw new RuntimeException("Código QR no coincide con el envío");
            }
        }
        
        Envio.EstadoEnvio nuevoEstado = Envio.EstadoEnvio.valueOf(dto.getNuevoEstado());
        Envio.EstadoEnvio estadoAnterior = envio.getEstado();
        
        // Actualizar estado y campos relacionados
        envio.setEstado(nuevoEstado);
        if (dto.getObservaciones() != null) {
            envio.setObservaciones(envio.getObservaciones() + "\n" + dto.getObservaciones());
        }
        
        // Manejar casos especiales según el nuevo estado
        switch (nuevoEstado) {
            case EN_TRANSITO:
                if (envio.getFechaSalida() == null) {
                    envio.setFechaSalida(LocalDateTime.now());
                }
                break;
            case ENTREGADO:
                envio.setFechaEntregaReal(LocalDateTime.now());
                if (dto.getFirmaDigital() != null) {
                    envio.setFirmaDigital(dto.getFirmaDigital());
                }
                if (dto.getFotoEntrega() != null) {
                    envio.setFotoEntrega(dto.getFotoEntrega());
                }
                // Liberar repartidor
                if (envio.getRepartidor() != null) {
                    Repartidor repartidor = envio.getRepartidor();
                    repartidor.setEstado(Repartidor.EstadoRepartidor.DISPONIBLE);
                    usuarioRepository.save(repartidor);
                }
                break;
            case CANCELADO:
            case DEVUELTO:
                // Restaurar stock de productos
                if (envio.getCantidadPorProducto() != null) {
                    for (Map.Entry<Producto, Integer> entry : envio.getCantidadPorProducto().entrySet()) {
                        Producto producto = entry.getKey();
                        producto.setStockActual(producto.getStockActual() + entry.getValue());
                        productoRepository.save(producto);
                    }
                }
                // Liberar repartidor si estaba asignado
                if (envio.getRepartidor() != null) {
                    Repartidor repartidor = envio.getRepartidor();
                    repartidor.setEstado(Repartidor.EstadoRepartidor.DISPONIBLE);
                    usuarioRepository.save(repartidor);
                }
                break;
        }
        
        return envioRepository.save(envio);
    }
    
    @Transactional
    public Envio asignarRepartidor(Long envioId, Long repartidorId) {
        Envio envio = envioRepository.findById(envioId)
                .orElseThrow(() -> new RuntimeException("Envío no encontrado"));
        
        Repartidor repartidor = (Repartidor) usuarioRepository.findById(repartidorId)
                .orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));
        
        // Validar que el repartidor esté disponible
        if (repartidor.getEstado() != Repartidor.EstadoRepartidor.DISPONIBLE) {
            throw new RuntimeException("Repartidor no disponible");
        }
        
        envio.setRepartidor(repartidor);
        envio.setEstado(Envio.EstadoEnvio.ASIGNADO);
        envio.setFechaAsignacion(LocalDateTime.now());
        
        repartidor.setEstado(Repartidor.EstadoRepartidor.OCUPADO);
        usuarioRepository.save(repartidor);
        
        return envioRepository.save(envio);
    }
    
    public EnvioDTO convertirADTO(Envio envio) {
        EnvioDTO dto = new EnvioDTO();
        dto.setId(envio.getId());
        dto.setNumeroSeguimiento(envio.getNumeroSeguimiento());
        dto.setCodigoQR(envio.getCodigoQR());
        dto.setDireccionOrigen(envio.getDireccionOrigen());
        dto.setDireccionDestino(envio.getDireccionDestino());
        dto.setCiudadDestino(envio.getCiudadDestino());
        dto.setCodigoPostalDestino(envio.getCodigoPostalDestino());
        dto.setEstado(envio.getEstado().name());
        dto.setObservaciones(envio.getObservaciones());
        dto.setCostoEnvio(envio.getCostoEnvio());
        dto.setPrioridad(envio.getPrioridad());
        dto.setFechaCreacion(envio.getFechaCreacion());
        dto.setFechaAsignacion(envio.getFechaAsignacion());
        dto.setFechaSalida(envio.getFechaSalida());
        dto.setFechaEntregaEstimada(envio.getFechaEntregaEstimada());
        dto.setFechaEntregaReal(envio.getFechaEntregaReal());
        dto.setFechaConfirmacionCliente(envio.getFechaConfirmacionCliente());
        dto.setFirmaDigital(envio.getFirmaDigital());
        dto.setFotoEntrega(envio.getFotoEntrega());
        
        // Cliente info
        if (envio.getCliente() != null) {
            dto.setClienteId(envio.getCliente().getId());
            dto.setClienteNombre(envio.getCliente().getNombre() + " " + envio.getCliente().getApellido());
            dto.setClienteCedula(envio.getCliente().getCedula());
        }
        
        // Repartidor info
        if (envio.getRepartidor() != null) {
            dto.setRepartidorId(envio.getRepartidor().getId());
            dto.setRepartidorNombre(envio.getRepartidor().getNombre() + " " + envio.getRepartidor().getApellido());
            dto.setRepartidorCedula(envio.getRepartidor().getCedula());
        }
        
        // Admin creador info
        if (envio.getAdminCreador() != null) {
            dto.setAdminCreadorId(envio.getAdminCreador().getId());
            dto.setAdminCreadorNombre(envio.getAdminCreador().getNombre() + " " + envio.getAdminCreador().getApellido());
        }
        
        // Productos
        if (envio.getProductos() != null) {
            List<EnvioDTO.ProductoEnvioDTO> productosDTO = new ArrayList<>();
            for (Producto producto : envio.getProductos()) {
                EnvioDTO.ProductoEnvioDTO prodDTO = new EnvioDTO.ProductoEnvioDTO();
                prodDTO.setId(producto.getId());
                prodDTO.setNombre(producto.getNombre());
                prodDTO.setSku(producto.getSku());
                prodDTO.setCodigoQR(producto.getCodigoQR());
                if (envio.getCantidadPorProducto() != null && envio.getCantidadPorProducto().containsKey(producto)) {
                    prodDTO.setCantidad(envio.getCantidadPorProducto().get(producto));
                }
                productosDTO.add(prodDTO);
            }
            dto.setProductos(productosDTO);
        }
        
        return dto;
    }
}