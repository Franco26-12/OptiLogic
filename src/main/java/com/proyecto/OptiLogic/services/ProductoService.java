package com.proyecto.OptiLogic.services;

import com.proyecto.OptiLogic.dtos.ProductoDTO;
import com.proyecto.OptiLogic.entities.Producto;
import com.proyecto.OptiLogic.entities.Categoria;
import com.proyecto.OptiLogic.repositories.ProductoRepository;
import com.proyecto.OptiLogic.repositories.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }
    
    public Page<Producto> findAllPaginated(Pageable pageable) {
        return productoRepository.findAll(pageable);
    }

    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id);
    }
    
    public Optional<Producto> findBySku(String sku) {
        return productoRepository.findBySku(sku);
    }
    
    public Optional<Producto> findByCodigoQR(String codigoQR) {
        return productoRepository.findByCodigoQR(codigoQR);
    }
    
    public List<Producto> findByCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId);
    }
    
    public List<Producto> findProductosConStockBajo() {
        return productoRepository.findAll().stream()
                .filter(p -> p.getStockActual() < p.getStockMinimo())
                .collect(Collectors.toList());
    }
    
    public List<Producto> findProductosSinStock() {
        return productoRepository.findByStockActual(0);
    }
    
    public List<Producto> findProductosActivos() {
        return productoRepository.findByActivo(true);
    }

    @Transactional
    public Producto crearProducto(ProductoDTO productoDTO) {
        // Verificar SKU único
        if (productoRepository.findBySku(productoDTO.getSku()).isPresent()) {
            throw new RuntimeException("El SKU ya existe");
        }
        
        // Buscar categoría
        Categoria categoria = categoriaRepository.findById(productoDTO.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        
        Producto producto = new Producto();
        producto.setNombre(productoDTO.getNombre());
        producto.setSku(productoDTO.getSku());
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setPrecio(productoDTO.getPrecio());
        producto.setStockActual(productoDTO.getStockActual());
        producto.setStockMinimo(productoDTO.getStockMinimo());
        producto.setPeso(productoDTO.getPeso());
        producto.setDimensiones(productoDTO.getDimensiones());
        producto.setUbicacionAlmacen(productoDTO.getUbicacionAlmacen());
        producto.setCategoria(categoria);
        producto.setActivo(productoDTO.isActivo());
        
        // Generar código QR único
        producto.setCodigoQR("QR-PROD-" + producto.getSku() + "-" + UUID.randomUUID().toString().substring(0, 8));
        
        return productoRepository.save(producto);
    }
    
    @Transactional
    public Producto actualizarProducto(Long id, ProductoDTO productoDTO) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        // Verificar SKU único si cambió
        if (!producto.getSku().equals(productoDTO.getSku())) {
            if (productoRepository.findBySku(productoDTO.getSku()).isPresent()) {
                throw new RuntimeException("El SKU ya existe");
            }
            producto.setSku(productoDTO.getSku());
        }
        
        // Actualizar categoría si cambió
        if (!producto.getCategoria().getId().equals(productoDTO.getCategoriaId())) {
            Categoria categoria = categoriaRepository.findById(productoDTO.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            producto.setCategoria(categoria);
        }
        
        producto.setNombre(productoDTO.getNombre());
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setPrecio(productoDTO.getPrecio());
        producto.setStockActual(productoDTO.getStockActual());
        producto.setStockMinimo(productoDTO.getStockMinimo());
        producto.setPeso(productoDTO.getPeso());
        producto.setDimensiones(productoDTO.getDimensiones());
        producto.setUbicacionAlmacen(productoDTO.getUbicacionAlmacen());
        producto.setActivo(productoDTO.isActivo());
        producto.setFechaActualizacion(LocalDateTime.now());
        
        return productoRepository.save(producto);
    }
    
    @Transactional
    public void actualizarStock(Long id, Integer cantidad, String tipoOperacion) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        switch (tipoOperacion.toUpperCase()) {
            case "ENTRADA":
                producto.setStockActual(producto.getStockActual() + cantidad);
                break;
            case "SALIDA":
                if (producto.getStockActual() < cantidad) {
                    throw new RuntimeException("Stock insuficiente");
                }
                producto.setStockActual(producto.getStockActual() - cantidad);
                break;
            case "AJUSTE":
                producto.setStockActual(cantidad);
                break;
            default:
                throw new RuntimeException("Tipo de operación no válido");
        }
        
        producto.setFechaActualizacion(LocalDateTime.now());
        productoRepository.save(producto);
    }

    @Transactional
    public void deleteById(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        // En lugar de eliminar, desactivar
        producto.setActivo(false);
        producto.setFechaActualizacion(LocalDateTime.now());
        productoRepository.save(producto);
    }
    
    public ProductoDTO convertirADTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setSku(producto.getSku());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setStockActual(producto.getStockActual());
        dto.setStockMinimo(producto.getStockMinimo());
        dto.setPeso(producto.getPeso());
        dto.setDimensiones(producto.getDimensiones());
        dto.setUbicacionAlmacen(producto.getUbicacionAlmacen());
        dto.setCodigoQR(producto.getCodigoQR());
        dto.setActivo(producto.isActivo());
        
        if (producto.getCategoria() != null) {
            dto.setCategoriaId(producto.getCategoria().getId());
            dto.setCategoriaNombre(producto.getCategoria().getNombre());
        }
        
        return dto;
    }
    
    public long contarProductosActivos() {
        return productoRepository.countByActivo(true);
    }
    
    public long contarProductosPorCategoria(Long categoriaId) {
        return productoRepository.countByCategoriaId(categoriaId);
    }
}
