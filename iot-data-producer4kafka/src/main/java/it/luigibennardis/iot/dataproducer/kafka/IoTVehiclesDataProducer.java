package it.luigibennardis.iot.dataproducer.kafka;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import org.apache.log4j.Logger;

import it.luigibennardis.iot.dataproducer.kafka.util.PropertyFileConfigurationReader;
import it.luigibennardis.iot.dataproducer.kafka.vo.IoTVehicleData;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * CLASSE CHE GENERA EVENTI IOT E LI PUBBLICA SU KAFKA 
 * 
 */
public class IoTVehiclesDataProducer {
	
	private static final Logger logger = Logger.getLogger(IoTVehiclesDataProducer.class);

	public static void main(String[] args) throws Exception {
		
		Properties prop = PropertyFileConfigurationReader.readPropertyFile();		
		String zookeeper = prop.getProperty("it.luigibennardis.iot.dataproducer.kafka.zookeeper");
		String brokerList = prop.getProperty("it.luigibennardis.iot.dataproducer.kafka.brokerlist");
		String topic = prop.getProperty("it.luigibennardis.iot.dataproducer.kafka.vehicleTopic");
		
		logger.info("istanza zookeeper:" + zookeeper + " ,Lista dei broker:" + brokerList + " , nome topic " + topic);

		Properties properties = new Properties();
		properties.put("zookeeper.connect", zookeeper);
		properties.put("metadata.broker.list", brokerList);
		properties.put("request.required.acks", "1");
		properties.put("serializer.class", "it.luigibennardis.iot.dataproducer.kafka.util.IoTVehiclesDataEncoder");
		
		Producer<String, IoTVehicleData> producer = new Producer<String, IoTVehicleData>(new ProducerConfig(properties));
		IoTVehiclesDataProducer iotProducer = new IoTVehiclesDataProducer();
		iotProducer.generaEventiIOTsuKafka(producer,topic);		
	}


	/**
	 * GENERAZIONE DI DATI RANDOM IOT IN FORMATO JSON 
	 * 
	 * {
	 * "idVeicolo":"33f08f02-cd21-411a-5aef-ba87c9a88997",
	 * "tipoVeicolo":"Automobile",
	 * "idStrada":"A24",
	 * "latitude":",-85.673435",
	 * "longitude":"38.345395",
	 * "timestamp":1465471124373,
	 * "velocità":109.0,
	 * "benzina":58.0}
	 *  	 * 
	 */
	
	private void generaEventiIOTsuKafka(Producer<String, IoTVehicleData> producer, String topic) throws InterruptedException {
		List<String> routeList = Arrays.asList(new String[]{
				"A24", 
				"SS5", 
				"GRA"});
		
		List<String> vehicleTypeList = Arrays.asList(new String[]{
				"TIR", 
				"Furgone", 
				"Automobile", 
				"Autobus", 
				"Taxi"});
		
		
		Random rand = new Random();
		
		
		while (true) {
			List<IoTVehicleData> eventList = new ArrayList<IoTVehicleData>();
			for (int i = 0; i < 50; i++) {
				//***CREA 50 VEICOLI RANDOM
				String vehicleId = UUID.randomUUID().toString();
				String vehicleType = vehicleTypeList.get(rand.nextInt(5));
				String routeId = routeList.get(rand.nextInt(3));
				
				Date timestamp = new Date();
				//***VELOCITA' RANDOM TRA 5 E 130 KMH
				double speed = rand.nextInt(130 - 5) + 5; 
				
				
				//***BENZINA DISPONIBILE RANDOM TRA 10 E 40 LITRI
				double fuelLevel = rand.nextInt(40 - 10) + 10;
				for (int j = 0; j < 10; j++) {
					
					//***GENERA 10 EVENTI PER OGNI VEICOLO 
					String coords = getCoordinates(routeId);
					String latitude = coords.substring(0, coords.indexOf(","));
					String longitude = coords.substring(coords.indexOf(",") + 1, coords.length());
					IoTVehicleData event = new IoTVehicleData(
							vehicleId, vehicleType, routeId, latitude, longitude, timestamp, speed,fuelLevel);
					
					eventList.add(event);
				}
			}
			
			Collections.shuffle(eventList);
			
			for (IoTVehicleData event : eventList) {
				
				//***INVIA CIASCUN EVENTO ALLA CODA
				KeyedMessage<String, IoTVehicleData> data = new KeyedMessage<String, IoTVehicleData>(topic, event);
				producer.send(data);
				
				//***RITARDO RANDOM TRA 1 E 3 SEONDI  
				Thread.sleep(rand.nextInt(3000 - 1000) + 1000);
			}
		}
	}
	
	//***GENERAZIONE DEI DATI CASUALI DI LATITUDINE E LONGITUDINE
	private String  getCoordinates(String idStrada) {
		Random numeroCasuale = new Random();
		
		int prefissoLat = 0;
		int prefissoLong = -0;
		
		if (idStrada.equals("A24")) {
			prefissoLat = 33;
			prefissoLong = -96;
		} else if (idStrada.equals("SS5")) {
			prefissoLat = 34;
			prefissoLong = -97;
		} else if (idStrada.equals("GRA")) {
			prefissoLat = 35;
			prefissoLong = -98;
		} 
		Float latitudine = prefissoLat + numeroCasuale.nextFloat();
		Float longitudine = prefissoLong + numeroCasuale.nextFloat();
		
		return latitudine + "," + longitudine;
	}
}
