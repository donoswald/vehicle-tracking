package trivadis.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class TrackingRecord {
	//KMs
	private long distance;
	
	// ALREADY FORMATTED TO dd.MM.YYYY hh:mm , the time from GPS tracker (UTC)
	@JsonFormat
    (shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy hh:mm:ss")
	private Date eventTime;
	
	
	//location
	private double lat;
	private double lon;
	//motor running in milliseconds
	private long motorRunningTime;
	//Oil temperature
	private double oilTemp;
	private String plateNumber;
	//remaining fuel in liters
	private double remainingFuel;
	//Vehicle Identification Number, ( primary key)
	private String vin;
	//Water temperature
	private double waterTemp;
	//full weight of vehicle and goods in KGs
	private long weight;
	/**
	 * Constructor
	 * @param eventTime
	 * @param vin
	 */
	public TrackingRecord(Date eventTime, String vin) {
		this.eventTime = eventTime;
		this.vin = vin;
	}
	public TrackingRecord() {
		
	}
	public long getDistance() {
		return distance;
	}
	public Date getEventTime() {
		return eventTime;
	}
	public double getLat() {
		return lat;
	}
	public double getLon() {
		return lon;
	}
	public long getMotorRunningTime() {
		return motorRunningTime;
	}
	public double getOilTemp() {
		return oilTemp;
	}
	public String getPlateNumber() {
		return plateNumber;
	}
	public double getRemainingFuel() {
		return remainingFuel;
	}
	public String getVin() {
		return vin;
	}
	public double getWaterTemp() {
		return waterTemp;
	}
	public long getWeight() {
		return weight;
	}
	public void setDistance(long distance) {
		this.distance = distance;
	}
	public void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public void setMotorRunningTime(long motorRunningTime) {
		this.motorRunningTime = motorRunningTime;
	}
	public void setOilTemp(double oilTemp) {
		this.oilTemp = oilTemp;
	}
	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}
	public void setRemainingFuel(double remainingFuel) {
		this.remainingFuel = remainingFuel;
	}
	public void setVin(String vin) {
		this.vin = vin;
	}
	public void setWaterTemp(double waterTemp) {
		this.waterTemp = waterTemp;
	}
	public void setWeight(long weight) {
		this.weight = weight;
	}
	
		
}
