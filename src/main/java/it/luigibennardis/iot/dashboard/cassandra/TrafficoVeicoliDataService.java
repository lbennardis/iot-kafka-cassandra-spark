package it.luigibennardis.iot.dashboard.cassandra;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import it.luigibennardis.iot.dashboard.cassandra.dao.TrafficoPOIDataRepo;
import it.luigibennardis.iot.dashboard.cassandra.dao.TrafficoTotaleDataRepo;
import it.luigibennardis.iot.dashboard.cassandra.dao.TrafficoFinestraTemporaleDataRepo;
import it.luigibennardis.iot.dashboard.cassandra.dao.entity.TrafficoPOIEntity;
import it.luigibennardis.iot.dashboard.cassandra.dao.entity.TrafficoTotaleEntity;
import it.luigibennardis.iot.dashboard.cassandra.dao.entity.TrafficoFinestraTemporaleEntity;
import it.luigibennardis.iot.dashboard.cassandra.vo.HTTPResponse;

/**
 * CLASSE UTILITY IN TECNOLOGIA WEB SOCKET
 * INVIA I MESSAGGI SULLA DASHBOARD DI CONTROLLA A INTERVALLI FISSI ATTRAVERSO WEB SOCKET
*/
@Service
public class TrafficoVeicoliDataService {
	
	
	private static final Logger logger = Logger.getLogger(TrafficoVeicoliDataService.class);
	
	
	@Autowired
	private SimpMessagingTemplate messageTemplate;
	
	@Autowired
	private TrafficoTotaleDataRepo trafficoTotaleRepo;
	
	@Autowired
	private TrafficoFinestraTemporaleDataRepo trafficoFinestraTemporaleRepo;
	
	@Autowired
	private TrafficoPOIDataRepo trafficoPOIRepo;
	
	private static DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	
	//***Invia i dati del traffico letto su Cassandra alla GUI ogni 2.5 secondi 
	@Scheduled(fixedRate = 2500)
	public void trigger() {
						
		List<TrafficoTotaleEntity> listaTrafficoTotale = new ArrayList<TrafficoTotaleEntity>();
		List<TrafficoFinestraTemporaleEntity> listaTrafficoFinestraTemporale = new ArrayList<TrafficoFinestraTemporaleEntity>();
		List<TrafficoPOIEntity> listaTrafficoAlPOI = new ArrayList<TrafficoPOIEntity>();
		
		//***TRAFFICO TOTALE
		trafficoTotaleRepo.findTrafficDataByDate(simpleDateFormat.format(new Date())).forEach(e -> listaTrafficoTotale.add(e));	
		
		//***TRAFFICO FINESTRA TEMPORALE
		trafficoFinestraTemporaleRepo.findTrafficDataByDate(simpleDateFormat.format(new Date())).forEach(e -> listaTrafficoFinestraTemporale.add(e));	
		

		//***TRAFFICO AL POI
		trafficoPOIRepo.findAllPoi().forEach(e -> listaTrafficoAlPOI.add(e));
		
		
		HTTPResponse httpResponse = new HTTPResponse();
		
		httpResponse.setTotalTraffic(listaTrafficoTotale);
		httpResponse.setWindowTraffic(listaTrafficoFinestraTemporale);
		httpResponse.setPoiTraffic(listaTrafficoAlPOI);
		
		logger.info("INVIA ALLA USER INTERFACE LA RESPONSE: "+ httpResponse);
				 
		this.messageTemplate.convertAndSend("/topic/trafficData", httpResponse);
				
		
	}
	
}
