package cz.holub.myTrips.logic;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

import cz.holub.myTrips.dao.DataDao;
import cz.holub.myTrips.domain.GPSPoint;
import cz.holub.myTrips.domain.Tag;
import cz.holub.myTrips.domain.Trip;

public class TripLogic {
	static final int INITIAL_ID_LEN=8;
	static final double EQUATORIAL_EARTH_RADIUS = 6378.1370D;
	static final double D2R = (Math.PI / 180D);

	@Autowired
	DataDao dataDao;
	
	@Autowired
	TagLogic tagLogic;

	/**
	 * Vygeneruje náhodnou sekvenci znakù a èísel  
	 * @param idLen délka sekvence
	 * @return
	 */
	private String generateTripId(int idLen) {
		char[] chars = "abcdefghijklmnopqrstuvwxyzABSDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
		Random r = new Random(System.currentTimeMillis());
		char[] id = new char[idLen];
		for (int i = 0; i < idLen; i++) {
			id[i] = chars[r.nextInt(chars.length)];
		}
		return new String(id);
	}

	/**
	 * Metoda pro získání unikátního ID záznamu.
	 * Nechá vygenerovat id a následnì ovìøí zda neexistuje v databázi. 
	 * Pokud ano tak vygeneruje další.
	 * TODO: pøidat poèítadlo neúspìšných generování a po dosažení nìjakého poètu zvednout délku generovaného textu
	 * @return
	 */
	public String generateUniqueTripId() {
		String id = null;
		do {
			id = generateTripId(INITIAL_ID_LEN);
		} while (dataDao.existsIdInDb(id));
		return id;
	}
	
	private boolean hasStringBannedContent(String content) {
		boolean res= false;
		if ((content == null) || ("".equals(content))) {
			return res;
		}
		String wordsToCheck[]= content.split(" ");
		for (String word: wordsToCheck) {
			if (dataDao.isBannedWord(word)) {
				res= true;
				break; // nemá smysl pokraèovat našli jsme zakázané slovo
			}
		}
		return res;

	}

	/**
	 * Pøipraví trip na vložení do databáze
	 * - vygeneruje id<BR> 
	 * - nastaví délku na 0 (výpoèet délky se provede asynchronnì po zapsání vlastního záznamu do db)<BR> 
	 * - vyvolá kontrolu zda nemají zakázaný obsah (zakázané vymaže)<BR>
	 * - oèísluje zbylé tagy <BR> 
	 * - oèísluje gps souøadnice<BR> 
	 * @param trip
	 * @return true - pokud je vše v poøádku a záznam mùže být vložen do databáze,<BR> 
	 * false - pokud záznam není v poøádku a nemá se ukládat do DB
	 */
	public Status prepareTripBeforeInsert(Trip trip) {
		Status res= null;
		String tripId= generateUniqueTripId();
		trip.setId(tripId);		
		trip.setLenght(BigDecimal.ZERO);
		
		res= checkBeforeInsertUpdate(trip);
		if (res.getCode() != Status.STATUS_SUCCESFULL) {
			return res;
		}

		if ((trip.getTags() != null) && (trip.getTags().size()>0)) {
			int order= 0;
			List<Tag> tagsWithBannedContent= new ArrayList<Tag>();
			for(Tag tag:trip.getTags()) {
				order++;
				tag.setTripId(tripId);
				tag.setTagOrder(order);
			}
			if (!tagsWithBannedContent.isEmpty()) {
				trip.getTags().removeAll(tagsWithBannedContent);
			}
		}
		
		if ((trip.getGpsPoints() != null) && (trip.getGpsPoints().size()>0)) {
			int order= 0;
			for(GPSPoint gpsPoint:trip.getGpsPoints()) {
				order++;
				gpsPoint.setTripId(tripId);
				gpsPoint.setPointOrder(order);
			}
		}
		return res;
	}
	
	/**
	 * Pøipraví záznam na aktualizaci.
	 * Nejprve provede kontrolu, zda záznam nemá zakázaný obsah (pokud ano, vrátí chybný status a záznam neuloží).
	 * Pokud je záznam v poøádku tak v øípadì nutnosti dooèísluje tagy a pozice a záznam uloží.
	 * @param trip
	 * @return
	 */
	public Status prepareTripBeforeUpdate(Trip trip) {
		Status res= checkBeforeInsertUpdate(trip);
		if (res.getCode() != Status.STATUS_SUCCESFULL) {
			return res;
		}
		
		boolean updateAnyTagNeeded= false;
		int maxCount= 0;
		for (Tag tag: trip.getTags()) {
			if (trip.getId().equals(tag.getTripId())) {
				maxCount= Math.max(maxCount, tag.getTagOrder());
			} else {
				updateAnyTagNeeded= true;
			}
		}
		maxCount++;
		if (updateAnyTagNeeded) {
			for (Tag tag: trip.getTags()) {
				if (!trip.getId().equals(tag.getTripId())) {
					tag.setTripId(trip.getId());
					tag.setTagOrder(maxCount);
					maxCount++;
				}
			}
		}
		
		boolean updateAnyGPSPointNeeded= false;
		maxCount= 0;
		for(GPSPoint gpsPoint: trip.getGpsPoints()) {
			if (trip.getId().equals(gpsPoint.getTripId())) {
				maxCount= Math.max(maxCount, gpsPoint.getPointOrder());
			} else {
				updateAnyTagNeeded= true;
			}
		}
		if (updateAnyGPSPointNeeded) {
			for(GPSPoint gpsPoint: trip.getGpsPoints()) {
				if (trip.getId().equals(gpsPoint.getTripId())) {
					gpsPoint.setTripId(trip.getId());
					gpsPoint.setPointOrder(maxCount);
					maxCount++;
				}
			}
		}
		
		return res;
	}
	
	
	/**
	 * Kontrola pøed uložením do databáze.
	 * Pokud má zakázaný obsah Jméno nebo Popis tak vrací chybu,
	 * pokud má zakázaný obsah tag, tak se smaže ze seznamu tagù ale chyba se nevyvolá.
	 * @param trip
	 * @return
	 */
	public Status checkBeforeInsertUpdate(Trip trip) {
		Status res= null;
		if (hasStringBannedContent(trip.getName())) {
			return new Status(Status.STATUS_EROR, "Name obsahuje zakazany obsah", trip.getId());
		}
		if (hasStringBannedContent(trip.getDescription())) {
			return new Status(Status.STATUS_EROR, "Description obsahuje zakazany obsah", trip.getId());			
		}
		
		res= new Status(Status.STATUS_SUCCESFULL, "", trip.getId());
		//Zkontrolujeme zda Tagy nemají zakázaný obsah.
		//Pokud ano tak je smažeme ze seznamu tagù, pokud ne, tak je oèíslujeme
		if ((trip.getTags() != null) && (trip.getTags().size()>0)) {
			List<Tag> tagsWithBannedContent= new ArrayList<Tag>();
			for(Tag tag:trip.getTags()) {
				if (tagLogic.hasTagBannedContent(tag)) {
					tagsWithBannedContent.add(tag);
					res.addMessage("Tag '" + tag.getTag() + "' obsahuje zakazany obsah a bude vynechan", false);
				} 
			}
			if (!tagsWithBannedContent.isEmpty()) {
				trip.getTags().removeAll(tagsWithBannedContent);
			}
		}
		return res;
	}
	
	/**
	 * Statická metoda pro výpoèet vzdálennosti mezi dvìma body
	 * @param point1 
	 * @param point2
	 * @return vzdálennost mezi 
	 */
	public static double calculateDistanceOfTwoPoints(GPSPoint point1, GPSPoint point2) {
		double lat1 = point1.getLat().doubleValue();
		double lat2 = point2.getLat().doubleValue();
		double long1 = point1.getLng().doubleValue();
		double long2 = point2.getLng().doubleValue();
		double dlong = (long2 - long1) * D2R;
		double dlat = (lat2 - lat1) * D2R;
		double a = Math.pow(Math.sin(dlat / 2D), 2D)
				+ Math.cos(lat1 * D2R) * Math.cos(lat2 * D2R) * Math.pow(Math.sin(dlong / 2D), 2D);
		double c = 2D * Math.atan2(Math.sqrt(a), Math.sqrt(1D - a));
		double d = EQUATORIAL_EARTH_RADIUS * c;

		return d;
	}

	/**
	 * Výpoèet délky celého tripu.
	 * Vlastní výpoèet se provádí v samostatném threadu
	 * @param trip
	 */
	public void calculateTripLenInThread(Trip trip) {
		final DataDao myDataDao = dataDao;
		final Trip myTrip = trip;

		Thread distanceCalculatorThread = new Thread() {
			public void run() {
				System.out.println("Zahajuju vypocet vzdalennosti");
				int myGpsPointsSize = myTrip.getGpsPoints().size();
				if ((myTrip.getGpsPoints() != null) && (myGpsPointsSize > 1)) {
					double lenght = 0;
					for (int i = 0; i < (myGpsPointsSize - 1); i++) {
						lenght+= calculateDistanceOfTwoPoints(myTrip.getGpsPoints().get(i), myTrip.getGpsPoints().get(i+1));
					}
					myTrip.setLenght(new BigDecimal(lenght));
					System.out.println("Vypocet vzdalenosti byl dokonce delka je " + lenght);
				} else {
					myTrip.setLenght(BigDecimal.ZERO);
				}
				try {
					myDataDao.updateTrip(myTrip);
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		};
		try {
			distanceCalculatorThread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
