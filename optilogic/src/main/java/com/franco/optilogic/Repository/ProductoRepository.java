package com.franco.optilogic.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.franco.optilogic.Entity.Categoria;
import com.franco.optilogic.Entity.Producto;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    // Productos con stock bajo (menos del mínimo)
    @Query("SELECT p FROM Producto p WHERE p.stockDisponible < p.stockMinimo")
    List<Producto> findProductosConStockBajo();
    
    // Productos sin stock
    List<Producto> findByStockDisponible(Integer stock);
    
    // Productos por categoría
    List<Producto> findByCategoria(Categoria categoria);
    
    // Contar productos disponibles (con stock > 0)
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.stockDisponible > 0")
    Long countProductosDisponibles();
    
    // Contar productos sin stock
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.stockDisponible = 0")
    Long countProductosSinStock();
}