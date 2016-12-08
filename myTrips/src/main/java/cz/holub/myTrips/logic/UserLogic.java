package cz.holub.myTrips.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import cz.holub.myTrips.dao.DataDao;
import cz.holub.myTrips.domain.User;

public class UserLogic {
	@Autowired
	DataDao dataDao;
	
	/**
	 * Ov��� zda u�ivatelsk� jm�ho a heslo odpov�daj� kombinaci ulo�en� v DB.
	 * @param userName
	 * @param password
	 * @return
	 */
	public boolean authenticateUser(String userName, String password) {
		User user;
		try {
			user = dataDao.getUser(userName);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if (user == null) {
			return false;
		}
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		passwordEncoder.matches(password, user.getPassword());
		return true;
	}
	
	/**
	 * Vytvo�en� hashe z plaintext hesla pomoc� BCryptPasswordEncoder
	 * takto vytvo�en� hashe lze pou��t i p�i ov��ov�n� pomoc� spring security.
	 * S�l je sou��st� hashe
	 * @param plainTextPassword
	 * @return
	 */
	public String encryptPassword(String plainTextPassword) {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(plainTextPassword);
		return hashedPassword;
	}
}
