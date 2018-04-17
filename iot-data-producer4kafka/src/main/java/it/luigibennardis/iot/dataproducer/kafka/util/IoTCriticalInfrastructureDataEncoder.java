package it.luigibennardis.iot.dataproducer.kafka.util;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.luigibennardis.iot.dataproducer.kafka.vo.IoTVehicleData;
import it.luigibennardis.iot.dataproducer.kafka.vo.IoTCriticalInfrastructureData;
import kafka.serializer.Encoder;
import kafka.utils.VerifiableProperties;

/**
 * Class to convert IoTData java object to JSON String
 * 
 * @author abaghel
 *
 */
public class IoTCriticalInfrastructureDataEncoder implements Encoder<IoTCriticalInfrastructureData> {
	
	private static final Logger logger = Logger.getLogger(IoTCriticalInfrastructureDataEncoder.class);	
	private static ObjectMapper objectMapper = new ObjectMapper();		
	public IoTCriticalInfrastructureDataEncoder(VerifiableProperties verifiableProperties) {

    }
	public byte[] toBytes(IoTCriticalInfrastructureData iotEvent) {
		try {
			String msg = objectMapper.writeValueAsString(iotEvent);
			logger.info(msg);
			return msg.getBytes();
		} catch (JsonProcessingException e) {
			logger.error("Error in Serialization", e);
		}
		return null;
	}
}
