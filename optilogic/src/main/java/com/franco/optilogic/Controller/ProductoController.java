package com.franco.optilogic.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.franco.optilogic.Entity.Producto;
import com.franco.optilogic.Services.ProductoService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/productos")
public class ProductoController {
    @Autowired private ProductoService productoService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Producto> listarProductos() { 
        return productoService.obtenerTodosLosProductos(); 
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Producto> obtenerProducto(@PathVariable Long id) {
        return productoService.obtenerProductoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/stock-bajo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Producto>> obtenerProductosStockBajo() {
        List<Producto> productos = productoService.obtenerProductosConStockBajo();
        return ResponseEntity.ok(productos);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> crearProducto(@RequestBody Producto producto) { 
        try {
            Producto nuevoProducto = productoService.guardarProducto(producto);
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Producto creado exitosamente");
            body.put("producto", nuevoProducto);
            return new ResponseEntity<>(body, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Error al crear producto");
            body.put("message", e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> actualizarProducto(@PathVariable Long id, @RequestBody Producto detalles) {
        return productoService.obtenerProductoPorId(id).map(existente -> {
            existente.setNombre(detalles.getNombre());
            existente.setSku(detalles.getSku());
            existente.setDescripcion(detalles.getDescripcion());
            existente.setStockDisponible(detalles.getStockDisponible());
            existente.setStockMinimo(detalles.getStockMinimo());
            existente.setCategoria(detalles.getCategoria());
            
            Producto actualizado = productoService.guardarProducto(existente);
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Producto actualizado exitosamente");
            body.put("producto", actualizado);
            return ResponseEntity.ok(body);
        }).orElseGet(() -> {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Producto no encontrado");
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        });
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> eliminarProducto(@PathVariable Long id) {
        return productoService.obtenerProductoPorId(id).map(p -> {
            productoService.eliminarProducto(id);
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Producto eliminado exitosamente");
            return ResponseEntity.ok(body);
        }).orElseGet(() -> {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Producto no encontrado");
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        });
    }
}