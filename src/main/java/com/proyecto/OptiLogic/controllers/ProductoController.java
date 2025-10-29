package com.proyecto.OptiLogic.controllers;

import com.proyecto.OptiLogic.entities.Producto;
import com.proyecto.OptiLogic.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // Listar todos los productos
    @GetMapping
    public List<Producto> getAll() {
        return productoService.findAll();
    }

    // Crear un producto nuevo
    @PostMapping
    public Producto create(@RequestBody Producto producto) {
        return productoService.save(producto);
    }

    // Actualizar producto por ID
    @PutMapping("/{id}")
    public ResponseEntity<Producto> update(@PathVariable Long id, @RequestBody Producto productoDetails) {
        return productoService.findById(id)
                .map(producto -> {
                    producto.setNombre(productoDetails.getNombre());
                    producto.setSku(productoDetails.getSku());
                    producto.setDescripcion(productoDetails.getDescripcion());
                    producto.setCategoria(productoDetails.getCategoria());
                    Producto updated = productoService.save(producto);
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Borrar producto por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (productoService.findById(id).isPresent()) {
            productoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
