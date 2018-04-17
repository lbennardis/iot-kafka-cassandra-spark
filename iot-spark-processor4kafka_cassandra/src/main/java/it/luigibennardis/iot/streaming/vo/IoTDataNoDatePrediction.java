package it.luigibennardis.iot.streaming.vo;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Class to represent the IoT vehicle data.
 * 
 * @author abaghel
 *
 */
public class IoTDataNoDatePrediction implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String vehicleId;
	private String vehicleType;
	private String routeId;
	private double latitude;
	private double longitude;
	private String timestamp;
	private double speed;
	private double fuelLevel;
	private double prediction;
	
	public IoTDataNoDatePrediction(){
		
	}
	
	public IoTDataNoDatePrediction(String vehicleId, String vehicleType, String routeId, double latitude, double longitude, 
			String timestamp, double speed, double fuelLevel, double prediction) {
		super();
		this.vehicleId = vehicleId;
		this.vehicleType = vehicleType;
		this.routeId = routeId;
		this.longitude = longitude;
		this.latitude = latitude;
		this.timestamp = timestamp;
		this.speed = speed;
		this.fuelLevel = fuelLevel;
		this.prediction = prediction;
	}

	public String getVehicleId() {
		return vehicleId;
	}

	public String getVehicleType() {
		return vehicleType;
	}

	public String getRouteId() {
		return routeId;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public double getSpeed() {
		return speed;
	}

	public double getFuelLevel() {
		return fuelLevel;
	}
	public double getPrediction() {
		return prediction;
	}

}
