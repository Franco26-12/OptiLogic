package com.franco.optilogic.Repository;




import com.franco.optilogic.Repository.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.franco.optilogic.Entity.Envio;
import com.franco.optilogic.Entity.Repartidor;

import java.util.List;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {
    List<Envio> findByRepartidor(Repartidor repartidor);
}