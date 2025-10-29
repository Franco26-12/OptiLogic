package com.proyecto.OptiLogic.controllers;

import com.proyecto.OptiLogic.entities.Cliente;
import com.proyecto.OptiLogic.services.ClienteService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public List<Cliente> listarClientes() {
        return clienteService.obtenerTodosLosClientes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerCliente(@PathVariable Long id) {
        return clienteService.obtenerClientePorId(id)
                .map(cliente -> ResponseEntity.ok(cliente))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Cliente crearCliente(@RequestBody Cliente cliente) {
        return clienteService.guardarCliente(cliente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizarCliente(@PathVariable Long id, @RequestBody Cliente detallesCliente) {
        return clienteService.obtenerClientePorId(id)
                .map(clienteExistente -> {
                    clienteExistente.setNombre(detallesCliente.getNombre());
                    clienteExistente.setApellido(detallesCliente.getApellido());
                    clienteExistente.setEmail(detallesCliente.getEmail());
                    clienteExistente.setPassword(detallesCliente.getPassword());
                    clienteExistente.setDireccion(detallesCliente.getDireccion());
                    clienteExistente.setTelefono(detallesCliente.getTelefono());
                    Cliente actualizado = clienteService.guardarCliente(clienteExistente);
                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCliente(@PathVariable Long id) {
        return clienteService.obtenerClientePorId(id)
                .map(cliente -> {
                    clienteService.eliminarCliente(id);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

 }
    

