package it.luigibennardis.iot.dataproducer.kafka.vo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;


public class IoTVehicleData implements Serializable{
	
	
	private static final long serialVersionUID = 2674616887537988278L;
	
	private String idVeicolo;
	private String tipoVeicolo;
	private String idStrada;
	private String longitudine;
	private String latitudine;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="CET")
	private Date timestamp;
	private double velocita;
	private double benzina;
	
	public IoTVehicleData(){
		
	}
	
	public IoTVehicleData(String idVeicolo, String tipoVeicolo, String idStrada, String latitudine, String longitudine,
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

	public String getLongitudine() {
		return longitudine;
	}

	public void setLongitudine(String longitudine) {
		this.longitudine = longitudine;
	}

	public String getLatitudine() {
		return latitudine;
	}

	public void setLatitudine(String latitudine) {
		this.latitudine = latitudine;
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
