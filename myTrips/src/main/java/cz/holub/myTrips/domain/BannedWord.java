package cz.holub.myTrips.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "BannedWords")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class BannedWord implements Serializable {
	private static final long serialVersionUID = 1689791413906065000L;
	@Id
	@Column(name = "word", nullable= false)
	private String word;

}
