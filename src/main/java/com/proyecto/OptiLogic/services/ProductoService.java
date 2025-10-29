package com.proyecto.OptiLogic.services;

import com.proyecto.OptiLogic.entities.Producto;
import com.proyecto.OptiLogic.repositories.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    // Obtiene todos los productos
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    // Busca un producto por ID (opcional)
    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id);
    }

    // Guarda o actualiza un producto
    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    // Borra un producto por ID
    public void deleteById(Long id) {
        productoRepository.deleteById(id);
    }
}
