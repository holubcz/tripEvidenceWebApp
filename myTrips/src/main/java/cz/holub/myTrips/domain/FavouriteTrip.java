package cz.holub.myTrips.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "FavouriteTrips")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class FavouriteTrip implements Serializable {
	private static final long serialVersionUID = -8555936234966773396L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable=false, unique= true, insertable= false, updatable= false)
	private Long id;

	@Column(name = "trip", length = 10, nullable = false)
	private String tripId;

	@Column(name = "userId", length = 10, nullable = false)
	private String userId;
/* Získání dat tímto zpùsobem funguje, ale jsou to jen proxy objekty, 
 * které by se museli inicializovat nyní nebudeme øešit.

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trip")
	Trip trip;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	User user;
*/
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
/*
	public Trip getTrip() {
		return trip;
	}

	public void setTrip(Trip trip) {
		this.trip = trip;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
*/
	public String getTripId() {
		return tripId;
	}

	public void setTripId(String tripId) {
		this.tripId = tripId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
