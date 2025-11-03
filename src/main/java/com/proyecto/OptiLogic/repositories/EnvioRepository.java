package com.proyecto.OptiLogic.repositories;

import com.proyecto.OptiLogic.entities.Envio;
import com.proyecto.OptiLogic.entities.Repartidor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {
    
    List<Envio> findByRepartidor(Repartidor repartidor);
    
    List<Envio> findByRepartidorId(Long repartidorId);
    
    List<Envio> findByClienteId(Long clienteId);
    
    List<Envio> findByEstado(Envio.EstadoEnvio estado);
    
    Optional<Envio> findByNumeroSeguimiento(String numeroSeguimiento);
    
    Optional<Envio> findByCodigoQR(String codigoQR);
    
    @Query("SELECT e FROM Envio e WHERE e.estado IN :estados ORDER BY e.prioridad DESC, e.fechaCreacion ASC")
    List<Envio> findByEstadosOrderByPrioridad(@Param("estados") List<Envio.EstadoEnvio> estados);
    
    @Query("SELECT COUNT(e) FROM Envio e WHERE e.estado = :estado")
    Long countByEstado(@Param("estado") Envio.EstadoEnvio estado);
    
    @Query("SELECT e FROM Envio e WHERE e.repartidor.id = :repartidorId AND e.estado IN :estados")
    List<Envio> findByRepartidorIdAndEstados(@Param("repartidorId") Long repartidorId, @Param("estados") List<Envio.EstadoEnvio> estados);
}