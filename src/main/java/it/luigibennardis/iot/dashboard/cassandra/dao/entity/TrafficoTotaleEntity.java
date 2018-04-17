package it.luigibennardis.iot.dashboard.cassandra.dao.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Entity class for total_traffic db table
 *
 */
@Table("traffico_totale")
public class TrafficoTotaleEntity implements Serializable{
		
	private static final long serialVersionUID = -921512025805922486L;
	
	@PrimaryKeyColumn(name = "idstrada",ordinal = 0,type = PrimaryKeyType.PARTITIONED)
	private String idStrada;
	@PrimaryKeyColumn(name = "dataInserimento",ordinal = 1,type = PrimaryKeyType.CLUSTERED)
	private String dataInserimento;
	@PrimaryKeyColumn(name = "tipoveicolo",ordinal = 2,type = PrimaryKeyType.CLUSTERED)
	private String tipoVeicolo;
	@Column(value = "numeroveicoli")
	private long numeroveicoli;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="CET")
	@Column(value = "timestamp")
	private Date timeStamp;
	
	
	public String getIdStrada() {
		return idStrada;
	}
	public void setIdStrada(String idStrada) {
		this.idStrada = idStrada;
	}
	public String getDataInserimento() {
		return dataInserimento;
	}
	public void setDataInserimento(String dataInserimento) {
		this.dataInserimento = dataInserimento;
	}
	public String getTipoVeicolo() {
		return tipoVeicolo;
	}
	public void setTipoVeicolo(String tipoVeicolo) {
		this.tipoVeicolo = tipoVeicolo;
	}
	public long getNumeroveicoli() {
		return numeroveicoli;
	}
	public void setNumeroveicoli(long numeroveicoli) {
		this.numeroveicoli = numeroveicoli;
	}
	public Date getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	
	
	@Override
	public String toString() {
		return "Dati Traffico Veicolare [idStrada=" + idStrada + ", tipoVeicolo=" + tipoVeicolo + ", numeroveicoli=" + numeroveicoli
				+ ", timeStamp=" + timeStamp + "]";
	}
	
	
}
