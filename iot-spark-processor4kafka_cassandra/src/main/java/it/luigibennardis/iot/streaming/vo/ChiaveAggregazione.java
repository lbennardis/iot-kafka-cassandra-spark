package it.luigibennardis.iot.streaming.vo;

import java.io.Serializable;

/**
 * Chiave di aggregazione  
 */
public class ChiaveAggregazione implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	private String idStrada;
	private String tipoVeicolo;
	
	public ChiaveAggregazione(String idStrada, String tipoVeicolo) {
		super();
		this.idStrada = idStrada;
		this.tipoVeicolo = tipoVeicolo;
	}

	public String getIdStrada() {
		return idStrada;
	}

	public String getTipoVeicolo() {
		return tipoVeicolo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idStrada == null) ? 0 : idStrada.hashCode());
		result = prime * result + ((tipoVeicolo == null) ? 0 : tipoVeicolo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj !=null && obj instanceof ChiaveAggregazione){
			ChiaveAggregazione other = (ChiaveAggregazione)obj;
			
			if(	other.getIdStrada() != null 
					&& other.getTipoVeicolo() != null){
				if((other.getIdStrada().equals(this.idStrada)) && (other.getTipoVeicolo().equals(this.tipoVeicolo))){
					return true;
				} 
			}
		}
		return false;
	}
	

}
