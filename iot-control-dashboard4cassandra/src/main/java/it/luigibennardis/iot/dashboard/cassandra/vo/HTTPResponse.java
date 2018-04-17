package it.luigibennardis.iot.dashboard.cassandra.vo;

import java.io.Serializable;
import java.util.List;

import it.luigibennardis.iot.dashboard.cassandra.dao.entity.TrafficoPOIEntity;
import it.luigibennardis.iot.dashboard.cassandra.dao.entity.TrafficoTotaleEntity;
import it.luigibennardis.iot.dashboard.cassandra.dao.entity.TrafficoFinestraTemporaleEntity;

/**
 * OGGETTO RESPONSE CONTENENTE I DATI I TRAFFICO TOTALE, FINESTRA TEMPORALE E POI 
 * INVIATI ALLA DASHBOARD
 */
public class HTTPResponse implements Serializable {
	

	private static final long serialVersionUID = -1803204407348847955L;
	
	private List<TrafficoTotaleEntity> trafficoTotale;
	private List<TrafficoFinestraTemporaleEntity> trafficoFinestraTemporale;
	private List<TrafficoPOIEntity> trafficoPOI;
	
	public List<TrafficoTotaleEntity> getTotalTraffic() {
		return trafficoTotale;
	}
	public void setTotalTraffic(List<TrafficoTotaleEntity> trafficoTotale) {
		this.trafficoTotale = trafficoTotale;
	}
	public List<TrafficoFinestraTemporaleEntity> getWindowTraffic() {
		return trafficoFinestraTemporale;
	}
	public void setWindowTraffic(List<TrafficoFinestraTemporaleEntity> trafficoFinestraTemporale) {
		this.trafficoFinestraTemporale = trafficoFinestraTemporale;
	}
	public List<TrafficoPOIEntity> getPoiTraffic() {
		return trafficoPOI;
	}
	public void setPoiTraffic(List<TrafficoPOIEntity> trafficoPOI) {
		this.trafficoPOI = trafficoPOI;
	}

}
