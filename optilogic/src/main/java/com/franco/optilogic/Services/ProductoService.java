package com.franco.optilogic.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.franco.optilogic.Entity.Producto;
import com.franco.optilogic.Repository.ProductoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {
    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> obtenerTodosLosProductos() { return productoRepository.findAll(); }
    public Optional<Producto> obtenerProductoPorId(Long id) { return productoRepository.findById(id); }
    public Producto guardarProducto(Producto producto) { return productoRepository.save(producto); }
    public void eliminarProducto(Long id) { productoRepository.deleteById(id); }
}