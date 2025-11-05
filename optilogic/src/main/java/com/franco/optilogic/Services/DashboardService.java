package com.franco.optilogic.Services;

import com.franco.optilogic.Repository.AdminRepository;
import com.franco.optilogic.Repository.EnvioRepository;
import com.franco.optilogic.Repository.ProductoRepository;
import com.franco.optilogic.Repository.RepartidorRepository;
import dtos.DashboardStatsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final ProductoRepository productoRepository;
    private final EnvioRepository envioRepository;
    private final RepartidorRepository repartidorRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public DashboardService(ProductoRepository productoRepository, EnvioRepository envioRepository,
                           RepartidorRepository repartidorRepository, AdminRepository adminRepository) {
        this.productoRepository = productoRepository;
        this.envioRepository = envioRepository;
        this.repartidorRepository = repartidorRepository;
        this.adminRepository = adminRepository;
    }

    public DashboardStatsDTO obtenerEstadisticas() {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        
        // Estadísticas de productos
        stats.setTotalProductos(productoRepository.count());
        stats.setProductosDisponibles(productoRepository.countProductosDisponibles());
        stats.setProductosSinStock(productoRepository.countProductosSinStock());
        stats.setProductosStockBajo((long) productoRepository.findProductosConStockBajo().size());
        
        // Estadísticas de envíos
        stats.setTotalEnvios(envioRepository.count());
        stats.setEnviosPendientes(envioRepository.countByEstado("PENDIENTE"));
        stats.setEnviosEnTransito(envioRepository.countByEstado("EN_TRANSITO"));
        stats.setEnviosEntregados(envioRepository.countByEstado("ENTREGADO"));
        
        // Estadísticas de repartidores
        stats.setTotalRepartidores(repartidorRepository.count());
        stats.setRepartidoresDisponibles(repartidorRepository.countByEstado("DISPONIBLE"));
        stats.setRepartidoresOcupados(repartidorRepository.countByEstado("OCUPADO"));
        
        // Estadísticas de admins
        stats.setTotalAdmins(adminRepository.count());
        
        return stats;
    }
}
