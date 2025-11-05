package com.franco.optilogic.Repository;





import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.franco.optilogic.Entity.Envio;
import com.franco.optilogic.Entity.Repartidor;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {
    List<Envio> findByRepartidor(Repartidor repartidor);
    
    // Envíos por estado
    List<Envio> findByEstado(String estado);
    
    // Contar envíos por estado
    Long countByEstado(String estado);
    
    // Envíos pendientes de asignar (sin repartidor)
    List<Envio> findByRepartidorIsNull();
    
    // Envíos activos de un repartidor (en tránsito)
    List<Envio> findByRepartidorAndEstado(Repartidor repartidor, String estado);
    
    // Buscar envío por código QR
    Optional<Envio> findByCodigoQR(String codigoQR);
}