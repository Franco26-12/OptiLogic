package com.franco.optilogic.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.franco.optilogic.Entity.*;

@Repository
public interface RepartidorRepository extends JpaRepository<Repartidor, Long> {}

