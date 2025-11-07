package com.franco.optilogic.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.franco.optilogic.Entity.Categoria;
import com.franco.optilogic.Entity.Producto;
import com.franco.optilogic.Repository.CategoriaRepository;
import com.franco.optilogic.Repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<Producto> obtenerTodosLosProductos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> obtenerProductoPorId(Long id) {
        return productoRepository.findById(id);
    }

    public Producto guardarProducto(Producto producto) {
        if (producto.getCategoria() != null) {
            Long categoriaId = producto.getCategoria().getId();
            if (categoriaId != null) {
                Categoria categoria = categoriaRepository.findById(categoriaId)
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
                producto.setCategoria(categoria);
            } else {
                producto.setCategoria(null);
            }
        }
        return productoRepository.save(producto);
    }

    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }

    public List<Producto> obtenerProductosConStockBajo() {
        return productoRepository.findProductosConStockBajo();
    }
}