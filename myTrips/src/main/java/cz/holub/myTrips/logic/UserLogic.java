package cz.holub.myTrips.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import cz.holub.myTrips.dao.DataDao;
import cz.holub.myTrips.domain.User;

public class UserLogic {
	@Autowired
	DataDao dataDao;
	
	public boolean authenticateUser(String userName, String password) {
		User user;
		try {
			user = dataDao.getUser(userName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
	
	public String encryptPassword(String plainTextPassword) {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(plainTextPassword);
		return hashedPassword;
	}
}
