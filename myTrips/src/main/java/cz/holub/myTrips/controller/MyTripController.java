package cz.holub.myTrips.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cz.holub.myTrips.dao.DataDao;
import cz.holub.myTrips.domain.FavouriteTrip;
import cz.holub.myTrips.domain.Trip;
import cz.holub.myTrips.domain.User;
import cz.holub.myTrips.logic.UserLogic;
import cz.holub.myTrips.serviceTools.AuthenticatedUser;
import cz.holub.myTrips.serviceTools.Login;
import cz.holub.myTrips.serviceTools.Status;

@RestController
public class MyTripController {

	@Autowired
	DataDao dataDao;

	@Autowired
	UserLogic userLogic;
	
	AuthenticatedUser authenticatedUser;
	
	@RequestMapping(value = "/trips", method = RequestMethod.GET, headers = "Accept=application/json")
	public List<Trip> getTrips() {
		List<Trip> listOfTrips = null;
		try {
			listOfTrips = (List<Trip>)dataDao.getTripList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfTrips;
	}
	
	@RequestMapping(value = "/trips", method = RequestMethod.POST, headers = "Accept=application/json")
	public Status addTrip(@RequestBody Trip trip) {
		if (authenticatedUser == null) {
			return new Status(Status.STATUS_EROR, "Pro vytvoreni vyletu musi byt uzivatel prihlasen", null);
		}
		Status res;
		try {
			res= dataDao.addTrip(trip, authenticatedUser);
			
		} catch (Exception e) {
			e.printStackTrace();
			res= new Status();
			res.setCode(Status.STATUS_EROR);
			res.setMessage("Selhalo pridani zaznamu. Vyvolana vyjimka.");
		}
		return res;
	}

	@RequestMapping(value = "/trips", params= {"keywords"}, method = RequestMethod.GET, headers = "Accept=application/json")
	public List<Trip> searchTrips(@RequestParam(value = "keywords")List<String> keyWords) {
		List<Trip> listOfTrips = null;
		try {
			listOfTrips  = dataDao.getTripListByKeyWords(keyWords);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfTrips;
	}


	@RequestMapping(value = "/trip/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
	public Trip gettripById(@PathVariable String id) {
		try {
			return dataDao.getTripById(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping(value = "/trip/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
	public Status updateTrip(@RequestBody Trip trip) {
		if (authenticatedUser == null) {
			return new Status(Status.STATUS_EROR, "Pro aktualizaci vyletu musi byt uzivatel prihlasen", null);
		}
		Status res;
		try {
			res= dataDao.updateTrip(trip, authenticatedUser);
			if (res.getCode() == Status.STATUS_SUCCESFULL) {
				res.setMessage("Zaznam byl aktualizovan " +  res.getMessage());
			} else {
				res.setMessage("Selhala aktualizace zaznamu " +  res.getMessage());				
			}
		} catch (Exception e) {
			e.printStackTrace();
			res= new Status();
			res.setCode(Status.STATUS_EROR);
			res.setMessage("Selhala aktualizace zaznamu. Vyvolana vyjimka.");
		}
		return res;
	}

	@RequestMapping(value = "/trip/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
	public Status deletetrip(@PathVariable("id") String id) {
		if (authenticatedUser == null) {
			return new Status(Status.STATUS_EROR, "Pro vymaz vyletu musi byt uzivatel prihlasen", null);
		}
		Status res= new Status();
		res.setTripId(id);
		try {
			res= dataDao.deleteTrip(id, authenticatedUser);
		} catch (Exception e) {
			e.printStackTrace();
			res.setCode(Status.STATUS_EROR);
			res.setMessage("Selhal vymaz zaznamu. Vyvolana vyjimka.");

		}
		return res;
	}
	
	@RequestMapping(value = "/users", method = RequestMethod.POST, headers = "Accept=application/json")
	public Status addUser(@RequestBody User user) {
		Status res;
		try {
			res= dataDao.addUser(user);
			
		} catch (Exception e) {
			e.printStackTrace();
			res= new Status();
			res.setCode(Status.STATUS_EROR);
			res.setMessage("Selhalo pridani uživatele. Vyvolana vyjimka.");
		}
		return res;
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST, headers = "Accept=application/json")
	public Status authenticateUser(@RequestBody Login login) {
		Status res= new Status();
		try {
			if (userLogic.authenticateUser(login.getUserName(), login.getPassword())) {
				if (authenticatedUser == null) {
					authenticatedUser= new AuthenticatedUser();
				}
				authenticatedUser.setUserName(login.getUserName());
				res.setCode(Status.STATUS_SUCCESFULL);
				res.setMessage("Uživatel " + login.getUserName() + " byl autentizován");
			} else {
				authenticatedUser= null;
				res.setCode(Status.STATUS_EROR);
				res.setMessage("Uživatel "+ login.getUserName() + " nebyl autentizován");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	@RequestMapping(value = "/favouriteTrips", method = RequestMethod.GET, headers = "Accept=application/json")
	public List<FavouriteTrip> getFavouriteTrips() {
		List<FavouriteTrip> listOfFavouriteTrips = null;
		try {
			listOfFavouriteTrips = (List<FavouriteTrip>)dataDao.getFavouriteTripList(authenticatedUser);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfFavouriteTrips;
	}
	

	@RequestMapping(value = "/favouriteTrips", method = RequestMethod.POST, headers = "Accept=application/json")
	public Status addFavouriteTrip(@RequestBody FavouriteTrip favouriteTrip) {
		Status res;
		try {
			res= dataDao.addFavouriteTrip(favouriteTrip, authenticatedUser);
			
		} catch (Exception e) {
			e.printStackTrace();
			res= new Status();
			res.setCode(Status.STATUS_EROR);
			res.setMessage("Selhalo pridani zaznamu. Vyvolana vyjimka.");
		}
		return res;
	}
}
