package it.luigibennardis.iot.streaming.entity;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Class to represent poi_tarffic db table
 * 
 * @author  
 *
 */
public class POIDatiTrafficoVeicolare implements Serializable {


	private static final long serialVersionUID = -2476553074533606675L;
	private String idVeicolo;
	private double distanza;
	private String tipoVeicolo;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="CET")
	private Date timeStamp;

	

	public String getIdVeicolo() {
		return idVeicolo;
	}

	public void setIdVeicolo(String idVeicolo) {
		this.idVeicolo = idVeicolo;
	}

	public double getDistanza() {
		return distanza;
	}

	public void setDistanza(double distanza) {
		this.distanza = distanza;
	}

	public String getTipoVeicolo() {
		return tipoVeicolo;
	}

	public void setTipoVeicolo(String tipoVeicolo) {
		this.tipoVeicolo = tipoVeicolo;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

}
