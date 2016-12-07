package cz.holub.myTrips.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "Users")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class User implements Serializable {
	private static final long serialVersionUID = 8393945462705948580L;

	@Id
	@Column(name = "userName", length = 10, nullable= false, updatable= false)
	String userName;

	/**
	 * Heslo je uloženo jako hash, který je podporován spring-security a mìl by se v budoucnu dát použít
	 * pøi pøihlašování do app.
	 * zdroje informací:<BR>
	 * http://stackoverflow.com/questions/8521251/spring-securitypassword-encoding-in-db-and-in-applicationcontext <RB>
	 * http://stackoverflow.com/questions/18156883/spring-security-use-hashed-password-stored-in-database-as-salt-of-password-enco <BR>
	 */
	@Column(name = "password", length = 60) 
	String password;
	
	@Column(name = "email", length = 50)
	String email;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


}
