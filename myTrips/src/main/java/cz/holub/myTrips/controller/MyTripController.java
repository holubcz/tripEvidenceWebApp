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
import cz.holub.myTrips.domain.Trip;
import cz.holub.myTrips.serviceTools.Status;

@RestController
public class MyTripController {

	@Autowired
	DataDao dataDao;

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
	
	@RequestMapping(value = "/trip/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
	public Trip gettripById(@PathVariable String id) {
		try {
			return dataDao.getTripById(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping(value = "/trips", method = RequestMethod.POST, headers = "Accept=application/json")
	public Status addTrip(@RequestBody Trip trip) {
		Status res;
		try {
			res= dataDao.addTrip(trip);
			
		} catch (Exception e) {
			e.printStackTrace();
			res= new Status();
			res.setCode(Status.STATUS_EROR);
			res.setMessage("Selhalo pridani zaznamu. Vyvolana vyjimka.");
		}
		return res;
	}

	@RequestMapping(value = "/trip/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
	public Status updateTrip(@RequestBody Trip trip) {
		Status res;
		try {
			res= dataDao.updateTrip(trip);
			if (res.getCode() == Status.STATUS_SUCCESFULL) {
				res.setMessage("Zaznam byl aktualizovan" +  res.getMessage());
			} else {
				res.setMessage("Selhala aktualizace zaznamu" +  res.getMessage());				
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
		Status res= new Status();
		res.setTripId(id);
		try {
			if (dataDao.deleteTrip(id)) {
				res.setCode(Status.STATUS_SUCCESFULL);
				res.setMessage("Zaznam uspesne vymazan");
			} else {
				res.setCode(Status.STATUS_SUCCESFULL);
				res.setMessage("Zaznam nebyl nalezen");				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			res.setCode(Status.STATUS_EROR);
			res.setMessage("Selhal vymaz zaznamu. Vyvolana vyjimka.");

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

}
