package it.luigibennardis.iot.streaming.vo;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Class to represent the IoT vehicle data.
 * 
 * @author abaghel
 *
 */
public class IoTData implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5871631529144278180L;
	
	private String idVeicolo;
	private String tipoVeicolo;
	private String idStrada;
	private String latitudine;
	private String longitudine;
	//@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="IST")
	//@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="CET")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date timestamp;
	private double velocita;
	private double benzina;
	
	public IoTData(){
		
	}
	
	public IoTData(String idVeicolo, String tipoVeicolo, String idStrada, String latitudine, String longitudine, 
			Date timestamp, double velocita, double benzina) {
		super();
		this.idVeicolo = idVeicolo;
		this.tipoVeicolo = tipoVeicolo;
		this.idStrada = idStrada;
		this.longitudine = longitudine;
		this.latitudine = latitudine;
		this.timestamp = timestamp;
		this.velocita = velocita;
		this.benzina = benzina;
	}

	public String getIdVeicolo() {
		return idVeicolo;
	}

	public void setIdVeicolo(String idVeicolo) {
		this.idVeicolo = idVeicolo;
	}

	public String getTipoVeicolo() {
		return tipoVeicolo;
	}

	public void setTipoVeicolo(String tipoVeicolo) {
		this.tipoVeicolo = tipoVeicolo;
	}

	public String getIdStrada() {
		return idStrada;
	}

	public void setIdStrada(String idStrada) {
		this.idStrada = idStrada;
	}

	public String getLatitudine() {
		return latitudine;
	}

	public void setLatitudine(String latitudine) {
		this.latitudine = latitudine;
	}

	public String getLongitudine() {
		return longitudine;
	}

	public void setLongitudine(String longitudine) {
		this.longitudine = longitudine;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public double getVelocita() {
		return velocita;
	}

	public void setVelocita(double velocita) {
		this.velocita = velocita;
	}

	public double getBenzina() {
		return benzina;
	}

	public void setBenzina(double benzina) {
		this.benzina = benzina;
	}

	
}
