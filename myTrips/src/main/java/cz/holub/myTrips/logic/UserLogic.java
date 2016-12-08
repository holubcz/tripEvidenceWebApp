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
	 * Ovìøí zda uživatelské jmého a heslo odpovídají kombinaci uložené v DB.
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
	 * Vytvoøení hashe z plaintext hesla pomocí BCryptPasswordEncoder
	 * takto vytvoøené hashe lze použít i pøi ovìøování pomocí spring security.
	 * Sùl je souèástí hashe
	 * @param plainTextPassword
	 * @return
	 */
	public String encryptPassword(String plainTextPassword) {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(plainTextPassword);
		return hashedPassword;
	}
}
