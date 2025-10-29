package com.proyecto.OptiLogic.repositories;

import com.proyecto.OptiLogic.entities.Repartidor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepartidorRepository extends JpaRepository<Repartidor, Long> {
}
