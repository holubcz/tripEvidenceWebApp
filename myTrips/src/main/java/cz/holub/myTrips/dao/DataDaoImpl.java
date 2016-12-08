package cz.holub.myTrips.dao;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

import cz.holub.myTrips.domain.FavouriteTrip;
import cz.holub.myTrips.domain.Trip;
import cz.holub.myTrips.domain.User;
import cz.holub.myTrips.logic.FavouriteTripLogic;
import cz.holub.myTrips.logic.TripLogic;
import cz.holub.myTrips.logic.UserLogic;
import cz.holub.myTrips.serviceTools.AuthenticatedUser;
import cz.holub.myTrips.serviceTools.Status;

public class DataDaoImpl implements DataDao {
	//TODO: P¯i p¯id·nÌ a n·slednÈ editaci doch·zÌ k vyvol·nÌ v˝jimky
	//org.hibernate.NonUniqueObjectException: A different object with the same identifier value was already associated with the session
	//¯eöenÌm by mÏlo b˝t vyvolat:
	//session.evict(object); 
	//nutno ovÏ¯it a otestovat.....
	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	TripLogic tripLogic;
	
	@Autowired
	UserLogic userLogic;
	
	@Autowired
	FavouriteTripLogic favouriteTripLogic;

	Session session = null;
	Transaction tx = null;

	/**
	 * Pomocn· metoda, kter· zajiöùuje aby byla otev¯ena pouze jedna session
	 * @return
	 */
	private Session openSession() {
		if (session == null) {
			session = sessionFactory.openSession();
		} else {
			if (!session.isOpen()) {
				session= sessionFactory.openSession();
			}
		}
		return session;
	}
	
	@Override
	public Status addTrip(Trip trip, AuthenticatedUser authenticatedUser) throws Exception {
		Status res = null;
		session= openSession();
		tx = session.beginTransaction();
		res = tripLogic.prepareTripBeforeInsert(trip, authenticatedUser);
		if (res.getCode() == 0) {
			session.save(trip);
			tx.commit();
			res.addMessageToStart("Zaznam byl pridan. ", false);
		} else {
			tx.rollback();
			res.addMessageToStart("Selhalo pridani zaznamu. ", false);
		}
		tripLogic.calculateTripLenInThread(trip);
		return res;
	}
	
	@Override
	public Trip getTripById(String id) throws Exception {
		session= openSession();		
		tx = session.getTransaction();
		session.beginTransaction();
		Trip trip = (Trip) session.get(Trip.class, id);
		tx.commit();
		return trip;
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public List<Trip> getTripList() throws Exception {
		session= openSession();	
		tx = session.beginTransaction();
		List<Trip> tripList = session.createCriteria(Trip.class).list();
		tx.commit();
		return tripList;
	}

	
	@Override
	public Status deleteTrip(String id, AuthenticatedUser authenticatedUser) throws Exception {
		if (!tripLogic.isOwnerAuthenticatedUser(id, authenticatedUser)) {
			return new Status(Status.STATUS_EROR, "Vylet muze smazat jen jeho vlastnik", id);
		}
		
		boolean res = false;
		session= openSession();	
		tx = session.getTransaction();
		session.beginTransaction();
		Trip trip = (Trip) session.get(Trip.class, id);
		if (trip != null) {
			res = true;
			session.delete(trip);
		}
		tx.commit();
		if (res) {
			return new Status(Status.STATUS_SUCCESFULL, "Zaznam uspesne vymazan", trip.getId());
		} else {
			return new Status(Status.STATUS_SUCCESFULL, "Zaznam nebyl nalezen", trip.getId());			
		}
	}

	
	@Override
	public boolean isBannedWord(String word) {
		session= openSession();
		Query query = session.createQuery("Select 1 from BannedWord t where t.word = :exId");
		query.setString("exId", word);
		boolean exists = (query.uniqueResult() != null);
		return exists;
	}

	
	@Override
	public boolean existsTripIdInDb(String id) {
		session= openSession();
		Query query = session.createQuery("Select 1 from Trip t where t.id = :exId");
		query.setString("exId", id);
		// tx = session.getTransaction();
		// session.beginTransaction();
		boolean exists = (query.uniqueResult() != null);
		// tx.commit();
		return exists;
	}

	
	@Override
	public Status updateTrip(Trip trip, AuthenticatedUser authenticatedUser) throws Exception {
		Status res= tripLogic.checkBeforeInsertUpdate(trip, authenticatedUser);
		if (res.getCode() != Status.STATUS_SUCCESFULL) {
			return res;
		}
		if (!tripLogic.isOwnerAuthenticatedUser(trip.getId(), authenticatedUser)) {
			return new Status(Status.STATUS_EROR, "Vlastnika vyletu nelze zmenit", trip.getId());
		}
		res= tripLogic.prepareTripBeforeUpdate(trip, authenticatedUser);
		if (res.getCode() == Status.STATUS_EROR) {
			return res;
		}
		session = openSession();
		tx = session.getTransaction();
		session.beginTransaction();
		session.saveOrUpdate(trip);
		System.out.println("delka je: " + trip.getLenght());
		tx.commit();
		tripLogic.calculateTripLenInThread(trip);
		return new Status(Status.STATUS_SUCCESFULL, "", trip.getId());

	}

	@Override
	public void updateTripSimple(Trip trip) throws Exception {
		session = openSession();
		tx = session.getTransaction();
		session.beginTransaction();
		session.saveOrUpdate(trip);
		System.out.println("delka je: " + trip.getLenght());
		tx.commit();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Trip> getTripListByKeyWords(List<String> keyWords) throws Exception {	
		Set<Trip> resSet= new LinkedHashSet<Trip>();
		session = sessionFactory.openSession();
		tx = session.getTransaction();
		session.beginTransaction();

		for (String keyWord : keyWords) {
			// dle nazvu
			Query queryByName = session.createQuery("Select t from Trip t where t.name like '%" + keyWord + "%'");
			List<Trip> listByName = queryByName.list();
			resSet.addAll(listByName);
			// dle popisku
			Query queryByDescription = session.createQuery("Select t from Trip t  where t.description like '%" + keyWord + "%'");
			List<Trip> listByDescription = queryByDescription.list();
			resSet.addAll(listByDescription);
			// dle tags
			Query queryByTag = session.createQuery("Select t from Trip t JOIN t.tags tg where tg.tag like '%" + keyWord + "%'");
			List<Trip> listByTag = queryByTag.list();
			resSet.addAll(listByTag);
		}
		tx.commit();
		return new ArrayList<Trip>(resSet);
	}

	@Override
	public User getUser(String userName) throws Exception {
		session = openSession();
		User user= (User) session.get(User.class, userName);
		tx = session.getTransaction();
		session.beginTransaction();
		tx.commit();
		return user;
	}

	@Override
	public boolean existsUserInDB(User user) {
		if ((user == null) || (user.getUserName() == null) || ("".equals(user.getUserName().trim()))) {
			return false;
		}
		session = openSession();
		Query query = session.createQuery("Select 1 from User t where t.userName = :exUsername");
		query.setString("exUsername", user.getUserName());
		boolean exists = (query.uniqueResult() != null);
		return exists;
	}
	
	@Override
	public Status addUser(User user) throws Exception {
		if ((user == null) || (user.getUserName() == null) || ("".equals(user.getUserName().trim()))) {
			return new Status(Status.STATUS_EROR, "Neplatny uzivatel", null);
		}
		if (existsUserInDB(user)) {
			return new Status(Status.STATUS_EROR, "Uzivatel jiz v databazi existuje", null);
		}
		String encryptedPassword= userLogic.encryptPassword(user.getPassword());
		user.setPassword(encryptedPassword);
		
		session = openSession();
		tx = session.beginTransaction();
		session.save(user);
		tx.commit();
		
		return new Status(Status.STATUS_SUCCESFULL, "Uzivatel byl pridan do databaze. ", null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<FavouriteTrip> getFavouriteTripList(AuthenticatedUser authenticatedUser) throws Exception {
		if ((authenticatedUser == null) || (authenticatedUser.getUserName() == null) || ("".equals(authenticatedUser.getUserName()))) {
			return null;
		}
		session = openSession();
		tx = session.beginTransaction();
		Query query = session.createQuery("Select t from FavouriteTrip t where t.userId = :exUsername");
		query.setString("exUsername", authenticatedUser.getUserName());
		List<FavouriteTrip> favouriteTripsList = query.list();
		tx.commit();
		return favouriteTripsList;
	}

	@Override
	public Status addFavouriteTrip(FavouriteTrip favouriteTrip, AuthenticatedUser authenticatedUser) throws Exception {
		Status res= favouriteTripLogic.checkBeforeInsert(favouriteTrip, authenticatedUser);
		if (res.getCode() == Status.STATUS_SUCCESFULL) {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			session.save(favouriteTrip);
			tx.commit();
			res.addMessageToStart("Zaznam byl pridan do databaze. ", false);
		}
		return res;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String getOriginalTripOwner(String tripId) {
		String res= null;
		session = openSession();
		tx = session.beginTransaction();
		Query query = session.createQuery("Select t.userId from Trip t where t.id = :exTripId");
		query.setString("exTripId", tripId);
		List<String> resList = query.list();
		if ((resList != null) && (resList.size() > 0)) {
			res= resList.get(0);
		}
		tx.commit();
		return res;
	}
}
