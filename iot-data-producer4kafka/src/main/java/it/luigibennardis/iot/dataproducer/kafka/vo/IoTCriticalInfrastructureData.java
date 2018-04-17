package it.luigibennardis.iot.dataproducer.kafka.vo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Class to represent the IoT vehicle data.
 * 
 * @author abaghel
 *
 */
public class IoTCriticalInfrastructureData implements Serializable{
	
	
	private static final long serialVersionUID = 5727873694904400665L;
	private double vehiclesnumber;
	private double totalweight;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="MST")
	private Date timestamp;
	
	public IoTCriticalInfrastructureData(){
		
	}
	
	public IoTCriticalInfrastructureData(double vehiclesnumber, double totalweight,Date timestamp ) {
		super();
		
		this.timestamp = timestamp;
		this.vehiclesnumber = vehiclesnumber;
		this.totalweight = totalweight;
	}

	 

	public Date getTimestamp() {
		return timestamp;
	}

	public double getVehiclesnumber() {
		return vehiclesnumber;
	}

	public double getTotalweight() {
		return totalweight;
	}

}
