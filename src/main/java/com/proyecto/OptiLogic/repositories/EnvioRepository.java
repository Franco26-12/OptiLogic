package com.proyecto.OptiLogic.repositories;

import com.proyecto.OptiLogic.entities.Envio;
import com.proyecto.OptiLogic.entities.Repartidor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {
    // Spring crea esta consulta automáticamente por el nombre del método
    List<Envio> findByRepartidor(Repartidor repartidor);
}