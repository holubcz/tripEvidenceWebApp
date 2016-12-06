package cz.holub.myTrips.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "Tags")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Tag implements Serializable {
	private static final long serialVersionUID = -1504019397884331704L;

	@Id
	@Column(name = "tripId", nullable= false)
	private String tripId;

	@Id
	@Column(name = "tagOrder", nullable= false)
	private int tagOrder;

	@Column(name = "tag")
	private String tag;

//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "tripId", insertable = false, updatable = false)
//	Trip trip;
//
	public String getTripId() {
		return tripId;
	}

	public void setTripId(String tripId) {
		this.tripId = tripId;
	}

	public int getTagOrder() {
		return tagOrder;
	}

	public void setTagOrder(int order) {
		this.tagOrder = order;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

//	public Trip getTrip() {
//		return trip;
//	}
//
//	public void setTrip(Trip trip) {
//		this.trip = trip;
//	}

}
