package dtos;

public class DashboardStatsDTO {
    
    // Estadísticas de productos
    private Long productosDisponibles;
    private Long productosSinStock;
    private Long productosStockBajo;
    private Long totalProductos;
    
    // Estadísticas de envíos
    private Long enviosPendientes;
    private Long enviosEnTransito;
    private Long enviosEntregados;
    private Long totalEnvios;
    
    // Estadísticas de repartidores
    private Long repartidoresDisponibles;
    private Long repartidoresOcupados;
    private Long totalRepartidores;
    
    // Estadísticas de usuarios
    private Long totalAdmins;
    
    public DashboardStatsDTO() {}

    // Getters y Setters
    public Long getProductosDisponibles() {
        return productosDisponibles;
    }

    public void setProductosDisponibles(Long productosDisponibles) {
        this.productosDisponibles = productosDisponibles;
    }

    public Long getProductosSinStock() {
        return productosSinStock;
    }

    public void setProductosSinStock(Long productosSinStock) {
        this.productosSinStock = productosSinStock;
    }

    public Long getProductosStockBajo() {
        return productosStockBajo;
    }

    public void setProductosStockBajo(Long productosStockBajo) {
        this.productosStockBajo = productosStockBajo;
    }

    public Long getTotalProductos() {
        return totalProductos;
    }

    public void setTotalProductos(Long totalProductos) {
        this.totalProductos = totalProductos;
    }

    public Long getEnviosPendientes() {
        return enviosPendientes;
    }

    public void setEnviosPendientes(Long enviosPendientes) {
        this.enviosPendientes = enviosPendientes;
    }

    public Long getEnviosEnTransito() {
        return enviosEnTransito;
    }

    public void setEnviosEnTransito(Long enviosEnTransito) {
        this.enviosEnTransito = enviosEnTransito;
    }

    public Long getEnviosEntregados() {
        return enviosEntregados;
    }

    public void setEnviosEntregados(Long enviosEntregados) {
        this.enviosEntregados = enviosEntregados;
    }

    public Long getTotalEnvios() {
        return totalEnvios;
    }

    public void setTotalEnvios(Long totalEnvios) {
        this.totalEnvios = totalEnvios;
    }

    public Long getRepartidoresDisponibles() {
        return repartidoresDisponibles;
    }

    public void setRepartidoresDisponibles(Long repartidoresDisponibles) {
        this.repartidoresDisponibles = repartidoresDisponibles;
    }

    public Long getRepartidoresOcupados() {
        return repartidoresOcupados;
    }

    public void setRepartidoresOcupados(Long repartidoresOcupados) {
        this.repartidoresOcupados = repartidoresOcupados;
    }

    public Long getTotalRepartidores() {
        return totalRepartidores;
    }

    public void setTotalRepartidores(Long totalRepartidores) {
        this.totalRepartidores = totalRepartidores;
    }

    public Long getTotalAdmins() {
        return totalAdmins;
    }

    public void setTotalAdmins(Long totalAdmins) {
        this.totalAdmins = totalAdmins;
    }
}
