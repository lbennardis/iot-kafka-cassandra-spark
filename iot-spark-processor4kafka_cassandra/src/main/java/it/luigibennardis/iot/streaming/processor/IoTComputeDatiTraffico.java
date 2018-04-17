package it.luigibennardis.iot.streaming.processor;

import static com.datastax.spark.connector.japi.CassandraStreamingJavaUtil.javaFunctions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function3;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.State;
import org.apache.spark.streaming.StateSpec;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaMapWithStateDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;

import com.datastax.spark.connector.japi.CassandraJavaUtil;

import it.luigibennardis.iot.streaming.entity.POIDatiTrafficoVeicolare;
import it.luigibennardis.iot.streaming.entity.TrafficoVeicolareFinestraTemporale;
import it.luigibennardis.iot.streaming.entity.TrafficoVeicolareTotale;
import it.luigibennardis.iot.streaming.util.CalcoloDistanzaGeoHaversine;
import it.luigibennardis.iot.streaming.vo.ChiaveAggregazione;
import it.luigibennardis.iot.streaming.vo.IoTData;
import it.luigibennardis.iot.streaming.vo.POIData;

import org.apache.spark.api.java.Optional;


import scala.Tuple2;
import scala.Tuple3;


public class IoTComputeDatiTraffico {
	private static final Logger logger = Logger.getLogger(IoTComputeDatiTraffico.class);

	/**
	 * Ottiene i dati di traffico totale di diversi tipi di veicoli sulle strade definite
	 * 
	 * @param filteredIotDataStream IoT data stream
	 */
	public void computeDatiTrafficoTotali(JavaDStream<IoTData> filteredIotDataStream) {

		// We need to get count of vehicle group by routeId and vehicleType
		JavaPairDStream<ChiaveAggregazione, Long> countDStreamPair = filteredIotDataStream
				.mapToPair(iot -> new Tuple2<>(new ChiaveAggregazione(iot.getIdStrada(), iot.getTipoVeicolo()), 1L))
				.reduceByKey((a, b) -> a + b);
		
		// Need to keep state for total count
		JavaMapWithStateDStream<ChiaveAggregazione, Long, Long, Tuple2<ChiaveAggregazione, Long>> countDStreamWithStatePair = countDStreamPair
				.mapWithState(StateSpec.function(totalSumFunc).timeout(Durations.seconds(3600)));//maintain state for one hour

		// Transform to dstream of TrafficData
		JavaDStream<Tuple2<ChiaveAggregazione, Long>> countDStream = countDStreamWithStatePair.map(tuple2 -> tuple2);
		JavaDStream<TrafficoVeicolareTotale> trafficDStream = countDStream.map(totalTrafficDataFunc);

		// Map Cassandra table column
		Map<String, String> columnNameMappings = new HashMap<String, String>();
		columnNameMappings.put("idStrada", "idstrada");
		columnNameMappings.put("tipoVeicolo", "tipoveicolo");
		columnNameMappings.put("numeroVeicoli", "numeroveicoli");
		columnNameMappings.put("timeStamp", "timestamp");
		columnNameMappings.put("dataInserimento", "datainserimento");
			
		// call CassandraStreamingJavaUtil function to save in DB
		javaFunctions(trafficDStream).writerBuilder("traffickeyspace", "traffico_totale",
				CassandraJavaUtil.mapToRow(TrafficoVeicolareTotale.class, columnNameMappings)).saveToCassandra();
				
		
	}

	/**
	 * Method to get window traffic counts of different type of vehicles for each route.
	 * Window duration = 30 seconds and Slide interval = 10 seconds
	 * 
	 * @param filteredIotDataStream IoT data stream
	 */
	public void computeDatiTrafficoFinestraTemporale(JavaDStream<IoTData> filteredIotDataStream) {

		// reduce by key and window (30 sec window and 10 sec slide).
		JavaPairDStream<ChiaveAggregazione, Long> countDStreamPair = filteredIotDataStream
				.mapToPair(iot -> new Tuple2<>(new ChiaveAggregazione(iot.getIdStrada(), iot.getTipoVeicolo()), 1L))
				.reduceByKeyAndWindow((a, b) -> a + b, Durations.seconds(30), Durations.seconds(10));

		// Transform to dstream of TrafficData
		JavaDStream<TrafficoVeicolareFinestraTemporale> trafficDStream = countDStreamPair.map(windowTrafficDataFunc);

		// Map Cassandra table column
		Map<String, String> columnNameMappings = new HashMap<String, String>();
		columnNameMappings.put("idStrada", "idstrada");
		columnNameMappings.put("tipoVeicolo", "tipoveicolo");
		columnNameMappings.put("numeroVeicoli", "numeroveicoli");
		columnNameMappings.put("timeStamp", "timestamp");
		columnNameMappings.put("dataInserimento", "datainserimento");
			
		
		// call CassandraStreamingJavaUtil function to save in DB
		javaFunctions(trafficDStream).writerBuilder("traffickeyspace", "traffico_finestra",
				CassandraJavaUtil.mapToRow(TrafficoVeicolareFinestraTemporale.class, columnNameMappings)).saveToCassandra();
	}

	/**
	 * Method to get the vehicles which are in radius of POI and their distance from POI.
	 * 
	 * @param nonFilteredIotDataStream original IoT data stream
	 * @param broadcastPOIValues variable containing POI coordinates, route and vehicle types to monitor.
	 */
	public void processPOIData(JavaDStream<IoTData> nonFilteredIotDataStream,Broadcast<Tuple3<POIData, String, String>> broadcastPOIValues) {
		 
		// Filter by routeId,vehicleType and in POI range
		JavaDStream<IoTData> iotDataStreamFiltered = nonFilteredIotDataStream
				.filter(iot -> (iot.getIdStrada().equals(broadcastPOIValues.value()._2())
						&& iot.getTipoVeicolo().contains(broadcastPOIValues.value()._3())
						&& CalcoloDistanzaGeoHaversine.isInPOIRadius(Double.valueOf(iot.getLatitudine()),
								Double.valueOf(iot.getLongitudine()), broadcastPOIValues.value()._1().getLatitude(),
								broadcastPOIValues.value()._1().getLongitude(),
								broadcastPOIValues.value()._1().getRadius())));

		// pair with poi
		JavaPairDStream<IoTData, POIData> poiDStreamPair = iotDataStreamFiltered
				.mapToPair(iot -> new Tuple2<>(iot, broadcastPOIValues.value()._1()));

		// Transform to dstream of POITrafficData
		JavaDStream<POIDatiTrafficoVeicolare> trafficDStream = poiDStreamPair.map(poiTrafficDataFunc);

		// Map Cassandra table column
		Map<String, String> columnNameMappings = new HashMap<String, String>();
		columnNameMappings.put("idVeicolo", "idveicolo");
		columnNameMappings.put("distanza", "distanza");
		columnNameMappings.put("tipoVeicolo", "tipoveicolo");
		columnNameMappings.put("timeStamp", "timestamp");

		// call CassandraStreamingJavaUtil function to save in DB
		javaFunctions(trafficDStream)
				.writerBuilder("traffickeyspace", "traffico_poi",CassandraJavaUtil.mapToRow(POIDatiTrafficoVeicolare.class, columnNameMappings))
				.withConstantTTL(120)//keeping data for 2 minutes
				.saveToCassandra();
	}
	
	//Function to get running sum by maintaining the state
	private static final Function3<ChiaveAggregazione, Optional<Long>, State<Long>,Tuple2<ChiaveAggregazione,Long>> totalSumFunc = (key,currentSum,state) -> {
        long totalSum = currentSum.or(0L) + (state.exists() ? state.get() : 0);
        Tuple2<ChiaveAggregazione, Long> total = new Tuple2<>(key, totalSum);
        state.update(totalSum);
        return total;
    };
    
    //Function to create TotalTrafficData object from IoT data
    private static final Function<Tuple2<ChiaveAggregazione, Long>, TrafficoVeicolareTotale> totalTrafficDataFunc = (tuple -> {
    	logger.debug("Total Count : " + "key " + tuple._1().getIdStrada() + "-" + tuple._1().getTipoVeicolo() + " value "+ tuple._2());
    	TrafficoVeicolareTotale trafficData = new TrafficoVeicolareTotale();
    	
    	trafficData.setIdStrada(tuple._1().getIdStrada());
		trafficData.setTipoVeicolo(tuple._1().getTipoVeicolo());
		trafficData.setNumeroVeicoli(tuple._2());
				
		trafficData.setTimeStamp(new Date());
		trafficData.setDataInserimento(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		
		return trafficData;
	});
    
    //Function to create WindowTrafficData object from IoT data
    private static final Function<Tuple2<ChiaveAggregazione, Long>, TrafficoVeicolareFinestraTemporale> windowTrafficDataFunc = (tuple -> {
		logger.debug("Window Count : " + "key " + tuple._1().getIdStrada() + "-" + tuple._1().getTipoVeicolo()+ " value " + tuple._2());
		TrafficoVeicolareFinestraTemporale trafficData = new TrafficoVeicolareFinestraTemporale();
		trafficData.setIdStrada(tuple._1().getIdStrada());
		trafficData.setTipoVeicolo(tuple._1().getTipoVeicolo());
		trafficData.setNumeroVeicoli(tuple._2());
		trafficData.setTimeStamp(new Date());
		trafficData.setDataInserimento(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		return trafficData;
	});
    
    //Function to create POITrafficData object from IoT data
    private static final Function<Tuple2<IoTData, POIData>, POIDatiTrafficoVeicolare> poiTrafficDataFunc = (tuple -> {
		POIDatiTrafficoVeicolare poiTraffic = new POIDatiTrafficoVeicolare();
		poiTraffic.setIdVeicolo(tuple._1.getIdVeicolo());
		poiTraffic.setTipoVeicolo(tuple._1.getTipoVeicolo());
		poiTraffic.setTimeStamp(new Date());
		double distance = CalcoloDistanzaGeoHaversine.getDistance(Double.valueOf(tuple._1.getLatitudine()).doubleValue(),
				Double.valueOf(tuple._1.getLongitudine()).doubleValue(), tuple._2.getLatitude(), tuple._2.getLongitude());
		logger.debug("Distance for " + tuple._1.getLatitudine() + "," + tuple._1.getLongitudine() + ","+ tuple._2.getLatitude() + "," + tuple._2.getLongitude() + " = " + distance);
		poiTraffic.setDistanza(distance);
		return poiTraffic;
	});

}
