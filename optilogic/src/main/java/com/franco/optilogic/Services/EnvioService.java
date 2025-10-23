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

import java.util.List;
import java.util.Optional;

@Service
public class EnvioService {
    @Autowired private EnvioRepository envioRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ProductoRepository productoRepository;

    public List<Envio> obtenerTodosLosEnvios() { return envioRepository.findAll(); }
    public Optional<Envio> obtenerEnvioPorId(Long id) { return envioRepository.findById(id); }

    @Transactional
    public Envio crearEnvio(CrearEnvioDTO dto) {
        Cliente cliente = (Cliente) usuarioRepository.findById(dto.getClienteId()).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        Repartidor repartidor = (Repartidor) usuarioRepository.findById(dto.getRepartidorId()).orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));
        List<Producto> productos = productoRepository.findAllById(dto.getProductoIds());
        if (productos.size() != dto.getProductoIds().size()) { throw new RuntimeException("Uno o más productos no fueron encontrados"); }

        Envio nuevoEnvio = new Envio();
        nuevoEnvio.setCliente(cliente);
        nuevoEnvio.setRepartidor(repartidor);
        nuevoEnvio.setProductos(productos);
        nuevoEnvio.setDireccionDestino(dto.getDireccionDestino());
        nuevoEnvio.setEstado("ASIGNADO");

        return envioRepository.save(nuevoEnvio);
    }
}