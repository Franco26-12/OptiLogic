package com.franco.optilogic.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.franco.optilogic.Entity.Producto;
import com.franco.optilogic.Services.ProductoService;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    @Autowired private ProductoService productoService;

    @GetMapping
    public List<Producto> listarProductos() { return productoService.obtenerTodosLosProductos(); }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProducto(@PathVariable Long id) {
        return productoService.obtenerProductoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Producto crearProducto(@RequestBody Producto producto) { return productoService.guardarProducto(producto); }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @RequestBody Producto detalles) {
        return productoService.obtenerProductoPorId(id).map(existente -> {
            existente.setNombre(detalles.getNombre());
            existente.setSku(detalles.getSku());
            existente.setDescripcion(detalles.getDescripcion());
            return ResponseEntity.ok(productoService.guardarProducto(existente));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        return productoService.obtenerProductoPorId(id).map(p -> {
            productoService.eliminarProducto(id);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}