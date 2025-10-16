package com.franco.optilogic.Controller;



import java.util.List;
import org.springframework.web.bind.annotation.*;

import com.franco.optilogic.Entity.Cliente;

import com.franco.optilogic.Services.*;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteServices service;

    public ClienteController(ClienteServices service) {
        this.service = service;
    }

    @GetMapping
    public List<Cliente> listar() {
        return service.listarClientes();
    }

    @PostMapping
    public Cliente crear(@RequestBody Cliente c) {
        return service.guardar(c);
    }

    @DeleteMapping("/{id}")
    public void borrar(@PathVariable Long id) {
        service.borrar(id);
    }
}
