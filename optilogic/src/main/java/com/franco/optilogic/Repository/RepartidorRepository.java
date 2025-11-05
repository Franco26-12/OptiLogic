package com.franco.optilogic.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.franco.optilogic.Entity.*;

import java.util.List;

@Repository
public interface RepartidorRepository extends JpaRepository<Repartidor, Long> {
    
    // Encontrar repartidores por estado
    List<Repartidor> findByEstado(String estado);
    
    // Contar repartidores por estado
    Long countByEstado(String estado);
}

