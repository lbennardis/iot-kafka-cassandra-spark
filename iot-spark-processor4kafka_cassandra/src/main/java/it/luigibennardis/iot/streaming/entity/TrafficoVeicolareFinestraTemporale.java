package it.luigibennardis.iot.streaming.entity;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Class to represent traffico_finestra tabella del db 
 *
 */
public class TrafficoVeicolareFinestraTemporale implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2097677956314606414L;
	private String idStrada;
	private String tipoVeicolo;
	private long numeroVeicoli;
	
	//@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="MST")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="CET")
	private Date timeStamp;
	private String dataInserimento;
	public String getIdStrada() {
		return idStrada;
	}
	public void setIdStrada(String idStrada) {
		this.idStrada = idStrada;
	}
	public String getTipoVeicolo() {
		return tipoVeicolo;
	}
	public void setTipoVeicolo(String tipoVeicolo) {
		this.tipoVeicolo = tipoVeicolo;
	}
	public long getNumeroVeicoli() {
		return numeroVeicoli;
	}
	public void setNumeroVeicoli(long numeroVeicoli) {
		this.numeroVeicoli = numeroVeicoli;
	}
	public Date getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getDataInserimento() {
		return dataInserimento;
	}
	public void setDataInserimento(String dataInserimento) {
		this.dataInserimento = dataInserimento;
	}
	
	
	
}
