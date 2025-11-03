package com.proyecto.OptiLogic.repositories;

import com.proyecto.OptiLogic.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    Optional<Producto> findBySku(String sku);
    
    Optional<Producto> findByCodigoQR(String codigoQR);
    
    List<Producto> findByCategoriaId(Long categoriaId);
    
    List<Producto> findByActivo(boolean activo);
    
    List<Producto> findByStockActual(Integer stockActual);
    
    @Query("SELECT p FROM Producto p WHERE p.stockActual < p.stockMinimo")
    List<Producto> findProductosConStockBajo();
    
    @Query("SELECT p FROM Producto p WHERE p.stockActual = 0")
    List<Producto> findProductosSinStock();
    
    @Query("SELECT p FROM Producto p WHERE p.categoria.id = :categoriaId AND p.activo = true")
    List<Producto> findByCategoriaIdAndActivo(@Param("categoriaId") Long categoriaId);
    
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.activo = true")
    long countByActivo(boolean activo);
    
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.categoria.id = :categoriaId")
    long countByCategoriaId(@Param("categoriaId") Long categoriaId);
    
    boolean existsBySku(String sku);
    
    boolean existsByCodigoQR(String codigoQR);
}
