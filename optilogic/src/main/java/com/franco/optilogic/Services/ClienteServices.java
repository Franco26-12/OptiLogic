package com.franco.optilogic.Services;



import java.util.List;
import org.springframework.stereotype.Service;

import com.franco.optilogic.Entity.Cliente;

import com.franco.optilogic.Repository.*;

@Service
public class ClienteServices {

    private final ClienteRepository repo;

    public ClienteServices(ClienteRepository repo) {
        this.repo = repo;
    }

    public List<Cliente> listarClientes() {
        return repo.findAll();
    }

    public Cliente guardar(Cliente c) {
        return repo.save(c);
    }

    public void borrar(Long id) {
        repo.deleteById(id);
    }
}
