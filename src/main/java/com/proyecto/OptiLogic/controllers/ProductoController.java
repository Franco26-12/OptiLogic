package com.proyecto.OptiLogic.controllers;

import com.proyecto.OptiLogic.dtos.ProductoDTO;
import com.proyecto.OptiLogic.entities.Producto;
import com.proyecto.OptiLogic.services.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listarTodos() {
        List<ProductoDTO> productos = productoService.findAll().stream()
                .map(productoService::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productos);
    }
    
    @GetMapping("/paginado")
    public ResponseEntity<Page<ProductoDTO>> listarPaginado(Pageable pageable) {
        Page<ProductoDTO> productos = productoService.findAllPaginated(pageable)
                .map(productoService::convertirADTO);
        return ResponseEntity.ok(productos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> obtenerPorId(@PathVariable Long id) {
        return productoService.findById(id)
                .map(producto -> ResponseEntity.ok(productoService.convertirADTO(producto)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductoDTO> obtenerPorSku(@PathVariable String sku) {
        return productoService.findBySku(sku)
                .map(producto -> ResponseEntity.ok(productoService.convertirADTO(producto)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/qr/{codigoQR}")
    public ResponseEntity<ProductoDTO> obtenerPorQR(@PathVariable String codigoQR) {
        return productoService.findByCodigoQR(codigoQR)
                .map(producto -> ResponseEntity.ok(productoService.convertirADTO(producto)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProductoDTO>> listarPorCategoria(@PathVariable Long categoriaId) {
        List<ProductoDTO> productos = productoService.findByCategoria(categoriaId).stream()
                .map(productoService::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productos);
    }
    
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<ProductoDTO>> listarStockBajo() {
        List<ProductoDTO> productos = productoService.findProductosConStockBajo().stream()
                .map(productoService::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productos);
    }
    
    @GetMapping("/sin-stock")
    public ResponseEntity<List<ProductoDTO>> listarSinStock() {
        List<ProductoDTO> productos = productoService.findProductosSinStock().stream()
                .map(productoService::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productos);
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody ProductoDTO productoDTO) {
        try {
            Producto producto = productoService.crearProducto(productoDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(productoService.convertirADTO(producto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id, 
            @Valid @RequestBody ProductoDTO productoDTO) {
        try {
            Producto producto = productoService.actualizarProducto(id, productoDTO);
            return ResponseEntity.ok(productoService.convertirADTO(producto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/stock")
    public ResponseEntity<?> actualizarStock(
            @PathVariable Long id,
            @RequestParam Integer cantidad,
            @RequestParam String tipoOperacion) {
        try {
            productoService.actualizarStock(id, cantidad, tipoOperacion);
            return ResponseEntity.ok(Map.of("mensaje", "Stock actualizado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            productoService.deleteById(id);
            return ResponseEntity.ok(Map.of("mensaje", "Producto desactivado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        Map<String, Object> stats = Map.of(
                "totalActivos", productoService.contarProductosActivos(),
                "conStockBajo", productoService.findProductosConStockBajo().size(),
                "sinStock", productoService.findProductosSinStock().size()
        );
        return ResponseEntity.ok(stats);
    }
}
