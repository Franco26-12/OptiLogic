package com.proyecto.OptiLogic.services;

import com.proyecto.OptiLogic.entities.Categoria;
import com.proyecto.OptiLogic.repositories.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    // Obtiene todas las categorías
    public List<Categoria> findAll() {
        return categoriaRepository.findAll();
    }

    // Busca una categoría por ID (opcional)
    public Optional<Categoria> findById(Long id) {
        return categoriaRepository.findById(id);
    }

    // Guarda o actualiza una categoría
    public Categoria save(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    // Borra una categoría por ID
    public void deleteById(Long id) {
        categoriaRepository.deleteById(id);
    }
}
