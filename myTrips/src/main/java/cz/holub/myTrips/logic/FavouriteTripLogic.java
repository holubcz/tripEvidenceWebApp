package cz.holub.myTrips.logic;

import org.springframework.beans.factory.annotation.Autowired;

import cz.holub.myTrips.dao.DataDao;
import cz.holub.myTrips.domain.FavouriteTrip;
import cz.holub.myTrips.serviceTools.AuthenticatedUser;
import cz.holub.myTrips.serviceTools.Status;

public class FavouriteTripLogic {
	@Autowired
	DataDao dataDao;

	/**
	 * Kontrola p�ed ulo�en�m z�znamu
	 * zaji��uje aby p�i p�id�n� byl p�ihl�en u�ivatel a nedo�lo k p�id�n� obl�ben�ho tripu jin�mu u�ivateli ne� kter� je p�ihl�en.
	 * @param favouriteTrip
	 * @param authenticatedUser
	 * @return
	 */
	public Status checkBeforeInsert(FavouriteTrip favouriteTrip, AuthenticatedUser authenticatedUser) {
		if ((authenticatedUser == null) || (authenticatedUser.getUserName() == null) || ("".equals(authenticatedUser.getUserName()))) {
			return new Status(Status.STATUS_EROR, "Uzivatel neni prihlasen", null);
		}
		
		if (favouriteTrip.getUserId() == null) {
			/*
			 * Vyta�en� userName p�es objekt z DB je zbyte�n�. Existence u�ivatele se ov��uje u� p�i autenizaci.
			 * 
			User user= null;
			try {
				user = dataDao.getUser(authenticatedUser.getUserName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (user == null) {
				return new Status(Status.STATUS_EROR, "Selhalo nalezeni prihlaseneho uzivatele", null);
			}
			favouriteTrip.setUserId(user.getUserName());
			*/
			favouriteTrip.setUserId(authenticatedUser.getUserName());
		} else {
			if (!authenticatedUser.getUserName().equals(favouriteTrip.getUserId())) {
				return new Status(Status.STATUS_EROR, "Nelze pridat oblibeny vylet cizimu uzivateli", null);
			}
		}
		return new Status(Status.STATUS_SUCCESFULL, "", null);			
	}

}
