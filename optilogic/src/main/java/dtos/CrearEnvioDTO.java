package dtos;

import java.util.List;

import lombok.Data;
@Data
public class CrearEnvioDTO {
	private Long clienteId;
    private Long repartidorId;
    private String direccionDestino;
    private List<Long> productoIds;
	public Long getClienteId() {
		return clienteId;
	}
	public void setClienteId(Long clienteId) {
		this.clienteId = clienteId;
	}
	public Long getRepartidorId() {
		return repartidorId;
	}
	public void setRepartidorId(Long repartidorId) {
		this.repartidorId = repartidorId;
	}
	public String getDireccionDestino() {
		return direccionDestino;
	}
	public void setDireccionDestino(String direccionDestino) {
		this.direccionDestino = direccionDestino;
	}
	public List<Long> getProductoIds() {
		return productoIds;
	}
	public void setProductoIds(List<Long> productoIds) {
		this.productoIds = productoIds;
	}

}
