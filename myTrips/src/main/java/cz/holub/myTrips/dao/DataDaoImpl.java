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

import cz.holub.myTrips.domain.Trip;
import cz.holub.myTrips.domain.User;
import cz.holub.myTrips.logic.TripLogic;
import cz.holub.myTrips.logic.UserLogic;
import cz.holub.myTrips.serviceTools.Status;

public class DataDaoImpl implements DataDao {
	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	TripLogic tripLogic;
	
	@Autowired
	UserLogic userLogic;

	Session session = null;
	Transaction tx = null;

	@Override
	public Status addTrip(Trip trip) throws Exception {
		Status res = null;
		session = sessionFactory.openSession();
		tx = session.beginTransaction();
		res = tripLogic.prepareTripBeforeInsert(trip);
		if (res.getCode() == 0) {
			session.save(trip);
			tx.commit();
			res.addMessageToStart("Zaznam byl pridan do databaze. ", false);
		} else {
			tx.rollback();
			res.addMessageToStart("Selhalo pridani zaznamu do databaze. ", false);
		}
		session.close();

		tripLogic.calculateTripLenInThread(trip);
		return res;
	}
	
	@Override
	public Trip getTripById(String id) throws Exception {
		session = sessionFactory.openSession();
		Trip trip = (Trip) session.load(Trip.class, id);
		tx = session.getTransaction();
		session.beginTransaction();
		tx.commit();
		return trip;
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public List<Trip> getTripList() throws Exception {
		session = sessionFactory.openSession();
		tx = session.beginTransaction();
		List<Trip> tripList = session.createCriteria(Trip.class).list();
		tx.commit();
		session.close();
		return tripList;
	}

	
	@Override
	public boolean deleteTrip(String id) throws Exception {
		boolean res = false;
		session = sessionFactory.openSession();
		tx = session.getTransaction();
		session.beginTransaction();
		Trip trip = (Trip) session.load(Trip.class, id);
		if (trip != null) {
			res = true;
			session.delete(trip);
		}
		tx.commit();
		return res;
	}

	
	@Override
	public boolean isBannedWord(String word) {
		Query query = session.createQuery("Select 1 from BannedWord t where t.word = :exId");
		query.setString("exId", word);
		boolean exists = (query.uniqueResult() != null);
		return exists;
	}

	
	@Override
	public boolean existsTripIdInDb(String id) {
		session = sessionFactory.openSession();
		Query query = session.createQuery("Select 1 from Trip t where t.id = :exId");
		query.setString("exId", id);
		// tx = session.getTransaction();
		// session.beginTransaction();
		boolean exists = (query.uniqueResult() != null);
		// tx.commit();
		return exists;
	}

	
	@Override
	public Status updateTrip(Trip trip) throws Exception {
		session = sessionFactory.openSession();
		tx = session.getTransaction();
		session.beginTransaction();
		session.saveOrUpdate(trip);
		System.out.println("delka je: " + trip.getLenght());
		tx.commit();
		return new Status(Status.STATUS_SUCCESFULL, "", trip.getId());
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
		session = sessionFactory.openSession();
		User user= (User) session.load(User.class, userName);
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
		session = sessionFactory.openSession();
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
		
		session = sessionFactory.openSession();
		tx = session.beginTransaction();
		session.save(user);
		tx.commit();
		session.close();
		
		return new Status(Status.STATUS_SUCCESFULL, "Uzivatel byl pridan do databaze. ", null);
	}
}
