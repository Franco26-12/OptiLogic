package com.franco.optilogic.Controller;

import com.franco.optilogic.Entity.Categoria;
import com.franco.optilogic.Services.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @Autowired
    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Categoria>> obtenerTodasLasCategorias() {
        List<Categoria> categorias = categoriaService.obtenerTodasLasCategorias();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Categoria> obtenerCategoriaPorId(@PathVariable Long id) {
        return categoriaService.obtenerCategoriaPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> crearCategoria(@RequestBody Categoria categoria) {
        try {
            Categoria nuevaCategoria = categoriaService.crearCategoria(categoria);
            
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Categoría creada exitosamente");
            body.put("categoria", nuevaCategoria);
            return new ResponseEntity<>(body, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Error al crear categoría");
            body.put("message", e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> actualizarCategoria(
            @PathVariable Long id, 
            @RequestBody Categoria categoria) {
        try {
            Categoria categoriaActualizada = categoriaService.actualizarCategoria(id, categoria);
            
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Categoría actualizada exitosamente");
            body.put("categoria", categoriaActualizada);
            return ResponseEntity.ok(body);
        } catch (RuntimeException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Error al actualizar categoría");
            body.put("message", e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> eliminarCategoria(@PathVariable Long id) {
        try {
            categoriaService.eliminarCategoria(id);
            
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Categoría eliminada exitosamente");
            return ResponseEntity.ok(body);
        } catch (RuntimeException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Error al eliminar categoría");
            body.put("message", e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
    }
}
