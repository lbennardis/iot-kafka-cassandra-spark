package it.luigibennardis.iot.dashboard.cassandra.dao.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * CLASSE ENTITY PER LA TABELLA Traffico_POI
 *
 */
@Table("Traffico_POI")
public class TrafficoPOIEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -440149616692050719L;
 
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="CET")
	@PrimaryKeyColumn(name = "timeStamp",ordinal = 0,type = PrimaryKeyType.PARTITIONED)
	private Date timeStamp;
	@PrimaryKeyColumn(name = "dataInserimento",ordinal = 1,type = PrimaryKeyType.CLUSTERED)
	private String dataInserimento;
	@Column(value = "idVeicolo")
	private String idVeicolo;
	@Column(value = "distanza")
	private double distanza;
	@Column(value = "tipoVeicolo")
	private String tipoVeicolo;
	
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
	
	
	
}
