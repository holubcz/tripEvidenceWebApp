package cz.holub.myTrips.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "GPSPoints")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class GPSPoint implements Serializable{
	private static final long serialVersionUID = -1808529030925954610L;

	@Id
	@Column(name = "tripId", nullable= false, updatable= false)
	private String tripId;

	@Id
	@Column(name = "pointOrder", nullable= false)
	private int pointOrder;
	
	@Column(name = "lat", precision=10, scale=6)
	private BigDecimal lat;

	@Column(name = "lng", precision=10, scale=6)
	private BigDecimal lng;

	public String getTripId() {
		return tripId;
	}

	public void setTripId(String tripId) {
		this.tripId = tripId;
	}

	public int getPointOrder() {
		return pointOrder;
	}

	public void setPointOrder(int pointOrder) {
		this.pointOrder = pointOrder;
	}

	public BigDecimal getLat() {
		return lat;
	}

	public void setLat(BigDecimal lat) {
		this.lat = lat;
	}

	public BigDecimal getLng() {
		return lng;
	}

	public void setLng(BigDecimal lng) {
		this.lng = lng;
	}
	
}
