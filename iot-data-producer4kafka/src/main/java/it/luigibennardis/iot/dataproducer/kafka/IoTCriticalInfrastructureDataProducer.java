package it.luigibennardis.iot.dataproducer.kafka;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
 

import org.apache.log4j.Logger;

import it.luigibennardis.iot.dataproducer.kafka.util.PropertyFileConfigurationReader;
 
import it.luigibennardis.iot.dataproducer.kafka.vo.IoTCriticalInfrastructureData;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;


public class IoTCriticalInfrastructureDataProducer {
	
	private static final Logger logger = Logger.getLogger(IoTCriticalInfrastructureDataProducer.class);

	public static void main(String[] args) throws Exception {
		
		//LETTURA FILE DI CONFIGURAZIONE
		Properties prop = PropertyFileConfigurationReader.readPropertyFile();		
		String zookeeper = prop.getProperty("it.luigibennardis.iot.dataproducer.kafka.zookeeper");
		String brokerList = prop.getProperty("it.luigibennardis.iot.dataproducer.kafka.brokerlist");
		String topic = prop.getProperty("it.luigibennardis.iot.dataproducer.kafka.criticalInfraTopic");
		
		logger.info("Using Zookeeper=" + zookeeper + " ,Broker-list=" + brokerList + " and topic " + topic);

		
		Properties properties = new Properties();
		properties.put("zookeeper.connect", zookeeper);
		properties.put("metadata.broker.list", brokerList);
		properties.put("request.required.acks", "1");
		properties.put("serializer.class", "it.luigibennardis.iot.dataproducer.kafka.util.IoTCriticalInfrastructureDataEncoder");
		
		//GENERATORE EVENTI IOT
		Producer<String, IoTCriticalInfrastructureData> producer = new Producer<String, IoTCriticalInfrastructureData>(new ProducerConfig(properties));
		IoTCriticalInfrastructureDataProducer iotProducer = new IoTCriticalInfrastructureDataProducer();
		iotProducer.generateIoTEvent(producer,topic);		
	}


	
	private void generateIoTEvent(Producer<String, IoTCriticalInfrastructureData> producer, String topic) throws InterruptedException {
		
		
		Random casualNumber = new Random();
		
		logger.info("Sending events to topic: " +  topic);
		
		while (true) {
			List<IoTCriticalInfrastructureData> eventList = new ArrayList<IoTCriticalInfrastructureData>();
			for (int i = 0; i < 100; i++) { 
				//***GENERAZIONE DI 100 EVENTI CASUALI
				
				//***TIMESTAMP DI OGNI EVENTO
				Date timestamp = new Date();
				
				//***NUMERO DI EVENTI GENERATI NELL'INTERVALLO 130 - 180 (PASSAGGIO DI VEICOLI)
				double numberOfVehicles = casualNumber.nextInt(180 - 130) + 130;  
				//***PESO STIMATO IN TONNELLATE 
				double totalWeight = numberOfVehicles * 2.5 ; 
				
				IoTCriticalInfrastructureData event = new IoTCriticalInfrastructureData(numberOfVehicles, totalWeight, timestamp);
					eventList.add(event);
				
			}
			
			
			Collections.shuffle(eventList);
			for (IoTCriticalInfrastructureData event : eventList) {
				KeyedMessage<String, IoTCriticalInfrastructureData> data = new KeyedMessage<String, IoTCriticalInfrastructureData>(topic, event);
				producer.send(data);
				Thread.sleep(casualNumber.nextInt(3000 - 1000) + 1000);//random delay of 1 to 3 seconds
			}
		}
	}
	
	
}
