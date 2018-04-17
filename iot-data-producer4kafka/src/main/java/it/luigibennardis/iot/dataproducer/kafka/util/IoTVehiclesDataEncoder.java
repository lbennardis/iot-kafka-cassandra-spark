package it.luigibennardis.iot.dataproducer.kafka.util;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.luigibennardis.iot.dataproducer.kafka.vo.IoTVehicleData;
import kafka.serializer.Encoder;
import kafka.utils.VerifiableProperties;

/**
 * Converte IoTVehicleData da java object a  String JSON
 * 
 * @author  
 *
 */
public class IoTVehiclesDataEncoder implements Encoder<IoTVehicleData> {
	
	private static final Logger logger = Logger.getLogger(IoTVehiclesDataEncoder.class);	
	private static ObjectMapper objectMapper = new ObjectMapper();		
	public IoTVehiclesDataEncoder(VerifiableProperties verifiableProperties) {

    }
	public byte[] toBytes(IoTVehicleData iotEvent) {
		try {
			String msg = objectMapper.writeValueAsString(iotEvent);
			logger.info(msg);
			return msg.getBytes();
		} catch (JsonProcessingException e) {
			logger.error("ERRORE DI SERIALIZZAZIONE", e);
		}
		return null;
	}
}
