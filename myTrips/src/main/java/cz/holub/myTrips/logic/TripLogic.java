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
import cz.holub.myTrips.serviceTools.Status;

public class TripLogic {
	static final int INITIAL_ID_LEN=8;
	static final double EQUATORIAL_EARTH_RADIUS = 6378.1370D;
	static final double D2R = (Math.PI / 180D);

	@Autowired
	DataDao dataDao;
	
	@Autowired
	TagLogic tagLogic;

	/**
	 * Vygeneruje n�hodnou sekvenci znak� a ��sel  
	 * @param idLen d�lka sekvence
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
	 * Metoda pro z�sk�n� unik�tn�ho ID z�znamu.
	 * Nech� vygenerovat id a n�sledn� ov��� zda neexistuje v datab�zi. 
	 * Pokud ano tak vygeneruje dal��.
	 * TODO: p�idat po��tadlo ne�sp�n�ch generov�n� a po dosa�en� n�jak�ho po�tu zvednout d�lku generovan�ho textu
	 * @return
	 */
	public String generateUniqueTripId() {
		String id = null;
		do {
			id = generateTripId(INITIAL_ID_LEN);
		} while (dataDao.existsTripIdInDb(id));
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
				break; // nem� smysl pokra�ovat na�li jsme zak�zan� slovo
			}
		}
		return res;

	}

	/**
	 * P�iprav� trip na vlo�en� do datab�ze
	 * - vygeneruje id<BR> 
	 * - nastav� d�lku na 0 (v�po�et d�lky se provede asynchronn� po zaps�n� vlastn�ho z�znamu do db)<BR> 
	 * - vyvol� kontrolu zda nemaj� zak�zan� obsah (zak�zan� vyma�e)<BR>
	 * - o��sluje zbyl� tagy <BR> 
	 * - o��sluje gps sou�adnice<BR> 
	 * @param trip
	 * @return true - pokud je v�e v po��dku a z�znam m��e b�t vlo�en do datab�ze,<BR> 
	 * false - pokud z�znam nen� v po��dku a nem� se ukl�dat do DB
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
	 * P�iprav� z�znam na aktualizaci.
	 * Nejprve provede kontrolu, zda z�znam nem� zak�zan� obsah (pokud ano, vr�t� chybn� status a z�znam neulo��).
	 * Pokud je z�znam v po��dku tak v ��pad� nutnosti doo��sluje tagy a pozice a z�znam ulo��.
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
	 * Kontrola p�ed ulo�en�m do datab�ze.
	 * Pokud m� zak�zan� obsah Jm�no nebo Popis tak vrac� chybu,
	 * pokud m� zak�zan� obsah tag, tak se sma�e ze seznamu tag� ale chyba se nevyvol�.
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
		//Zkontrolujeme zda Tagy nemaj� zak�zan� obsah.
		//Pokud ano tak je sma�eme ze seznamu tag�, pokud ne, tak je o��slujeme
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
	 * Statick� metoda pro v�po�et vzd�lennosti mezi dv�ma body
	 * @param point1 
	 * @param point2
	 * @return vzd�lennost mezi 
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
	 * V�po�et d�lky cel�ho tripu.
	 * Vlastn� v�po�et se prov�d� v samostatn�m threadu
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
