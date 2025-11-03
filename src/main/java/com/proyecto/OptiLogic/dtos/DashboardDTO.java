package com.proyecto.OptiLogic.dtos;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DashboardDTO {
    
    // Estadísticas generales
    private Long totalProductos;
    private Long productosEnStock;
    private Long productosStockBajo;
    private Long productosSinStock;
    
    private Long totalEnvios;
    private Long enviosPendientes;
    private Long enviosEnTransito;
    private Long enviosEntregados;
    private Long enviosCancelados;
    
    private Long totalRepartidores;
    private Long repartidoresActivos;
    private Long repartidoresEnRuta;
    private Long repartidoresDisponibles;
    
    private Long totalClientes;
    private Long clientesActivos;
    
    private BigDecimal ingresosTotales;
    private BigDecimal ingresosMes;
    private BigDecimal ingresosHoy;
    
    // Listas para mostrar en el dashboard
    private List<ProductoDTO> productosStockCritico;
    private List<EnvioDTO> enviosRecientes;
    private List<EnvioDTO> enviosUrgentes;
    private List<RepartidorDTO> repartidoresDisponiblesList;
}
