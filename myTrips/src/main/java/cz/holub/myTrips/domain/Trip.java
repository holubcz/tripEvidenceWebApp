package cz.holub.myTrips.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "Trips")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Trip implements Serializable {
	private static final long serialVersionUID = 8542581237369833601L;

	// @GeneratedValue
	@Id
	@Column(name = "id")
	String id;

	@Column(name = "name")
	String name;

	@Column(name = "description")
	String description;

	@Column(name = "lenght", precision=10, scale=6)
	BigDecimal lenght;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(orphanRemoval=true, cascade= CascadeType.ALL, fetch= FetchType.EAGER)
	@JoinColumn(name="tripId", updatable = false) 
	private List<Tag> tags = new ArrayList<Tag>();

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(orphanRemoval=true, cascade= CascadeType.ALL)
	@JoinColumn(name="tripId", updatable = false) 
	private List<GPSPoint> gpsPoints = new ArrayList<GPSPoint>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getLenght() {
		return lenght;
	}

	public void setLenght(BigDecimal len) {
		this.lenght = len;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public List<GPSPoint> getGpsPoints() {
		return gpsPoints;
	}

	public void setGpsPoints(List<GPSPoint> gpsPoints) {
		this.gpsPoints = gpsPoints;
	}

}