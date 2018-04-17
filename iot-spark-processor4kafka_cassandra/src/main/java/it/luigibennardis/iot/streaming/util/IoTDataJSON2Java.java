package it.luigibennardis.iot.streaming.util;

 

 

import com.fasterxml.jackson.databind.ObjectMapper;

import it.luigibennardis.iot.streaming.vo.IoTData;
import kafka.serializer.Decoder;
import kafka.utils.VerifiableProperties;

/**
 * DESERIALIZZA UN JASNO IN UN OGGETTO JAVA
 * 
 * @author 
 *
 */
public class IoTDataJSON2Java implements Decoder<IoTData> {
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	public IoTDataJSON2Java(VerifiableProperties verifiableProperties) {

    }
	public IoTData fromBytes(byte[] bytes) {
		try {
			return objectMapper.readValue(bytes, IoTData.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
