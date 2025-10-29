package com.proyecto.OptiLogic.services;

import com.proyecto.OptiLogic.dtos.CrearEnvioDTO;
import com.proyecto.OptiLogic.entities.Cliente;
import com.proyecto.OptiLogic.entities.Envio;
import com.proyecto.OptiLogic.entities.Producto;
import com.proyecto.OptiLogic.entities.Repartidor;
import com.proyecto.OptiLogic.repositories.EnvioRepository;
import com.proyecto.OptiLogic.repositories.ProductoRepository;
import com.proyecto.OptiLogic.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    @Transactional // Asegura que toda la operación se complete o no se haga nada
    public Envio crearEnvio(CrearEnvioDTO crearEnvioDTO) {
        // 1. Buscar el Cliente por su ID
        Cliente cliente = (Cliente) usuarioRepository.findById(crearEnvioDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // 2. Buscar el Repartidor por su ID
        Repartidor repartidor = (Repartidor) usuarioRepository.findById(crearEnvioDTO.getRepartidorId())
                .orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));

        // 3. Buscar la lista de Productos por sus IDs
        List<Producto> productos = productoRepository.findAllById(crearEnvioDTO.getProductoIds());
        if (productos.size() != crearEnvioDTO.getProductoIds().size()) {
            throw new RuntimeException("Uno o más productos no fueron encontrados");
        }

        // 4. Crear el nuevo objeto Envio
        Envio nuevoEnvio = new Envio();
        nuevoEnvio.setCliente(cliente);
        nuevoEnvio.setRepartidor(repartidor);
        nuevoEnvio.setProductos(productos);
        nuevoEnvio.setDireccionDestino(crearEnvioDTO.getDireccionDestino());
        nuevoEnvio.setEstado("ASIGNADO"); // Estado inicial

        // 5. Guardar el envío en la base de datos
        return envioRepository.save(nuevoEnvio);
    }
}