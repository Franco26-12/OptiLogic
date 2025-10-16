package com.franco.optilogic.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.franco.optilogic.Entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
