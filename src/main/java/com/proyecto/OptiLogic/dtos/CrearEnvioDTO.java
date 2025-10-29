package com.proyecto.OptiLogic.dtos;

import lombok.Data;
import java.util.List;

@Data // Lombok nos da getters y setters
public class CrearEnvioDTO {
    private Long clienteId;
    private Long repartidorId;
    private String direccionDestino;
    private List<Long> productoIds;
}