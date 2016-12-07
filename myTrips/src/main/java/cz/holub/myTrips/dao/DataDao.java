package cz.holub.myTrips.dao;

import java.util.List;

import cz.holub.myTrips.domain.Trip;
import cz.holub.myTrips.domain.User;
import cz.holub.myTrips.serviceTools.Status;

public interface DataDao {
	/**
	 * P�id� z�znam do datab�ze.
	 * P�ed vlastn�m p�id�n�m vyvol� jeho validaci. 
	 * Pokud validace neprob�hne �sp�n�, z�znam se do datab�ze nevlo�� a 
	 * vr�t� se status s chybov�m stavem a textov�m popisem chyby.
	 * @param trip z�znam pro vlo�en�
	 * @return Status s informac� zda vlo�en� prob�hlo v po��dku nebo ne
	 * @throws Exception 
	 */
	public Status addTrip(Trip trip) throws Exception;

	/**
	 * Aktualizuje z�znam v datab�zi.
	 * P�ed aktualizac� provede validaci zda je z�znam v po��dku a pokud ne tak jej neulo��.
	 * @param trip z�znam pro aktualizaci
	 * @return Status s informac� zda aktualizace prob�hla v po��dku nebo ne
	 * @throws Exception
	 */
	public Status updateTrip(Trip trip) throws Exception;

	/**
	 * Z�sk� trip z db podle jeho id
	 * @param id
	 * @return Trip pokud byl nalezen jinak null
	 * @throws Exception
	 */
	public Trip getTripById(String id) throws Exception;

	/**
	 * vrac� seznam v�ech trip� v datab�zi
	 * @return
	 * @throws Exception
	 */
	public List<Trip> getTripList() throws Exception;

	/**
	 * Dohled� z�znamy na z�klad� kl��ov�ch slov 
	 * slova jsou hled�ny pomoc� klauzule like (oboustrann�)
	 * Pokud je jeden z�znam nalezen v�cekr�t, je ve v�sledn�m poli pouze jednou
	 * @param keyWords seznam kl��ov�ch slov k dohled�n�
	 * @return nalezen� seznam bez duplicit
	 * @throws Exception
	 */
	public List<Trip> getTripListByKeyWords(List<String> keyWords) throws Exception;

	/**
	 * sma�e zvolen� trip z DB
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public boolean deleteTrip(String id) throws Exception;

	/**
	 * Ov��� zda existuje ID Tripu v datab�zi nebo ne
	 * @param id
	 * @return
	 */
	public boolean existsTripIdInDb(String id);
	
	/**
	 * Ov��� zda je slovo v seznamu zak�zan�ch slov
	 * @param word
	 * @return
	 */
	public boolean isBannedWord(String word);
	
	
	/**
	 * Ov��� zda u�ivatel existuje v datab�zi
	 * @param user
	 * @return
	 */
	public boolean existsUserInDB(User user);
	
	/**
	 * P�id� u�ivatele do datab�ze.
	 * P�ed p�id�n�m ov��� zda u�ivatel existuje. Pokud ano vr�t� status s chybou, pokud ne zahashuje heslo a z�znam ulo�� do db
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public Status addUser(User user) throws Exception;

	/**
	 * Z�sk� u�ivatele z datab�ze pokud existuje
	 * @param userName
	 * @return Objekt User pokud byl nalezen jinak null
	 * @throws Exception
	 */
	public User getUser(String userName) throws Exception;
}
