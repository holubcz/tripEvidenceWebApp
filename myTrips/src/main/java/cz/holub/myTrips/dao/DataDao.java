package cz.holub.myTrips.dao;

import java.util.List;

import cz.holub.myTrips.domain.Trip;
import cz.holub.myTrips.domain.User;
import cz.holub.myTrips.serviceTools.Status;

public interface DataDao {
	/**
	 * Pøidá záznam do databáze.
	 * Pøed vlastním pøidáním vyvolá jeho validaci. 
	 * Pokud validace neprobìhne úspìšnì, záznam se do databáze nevloží a 
	 * vrátí se status s chybovým stavem a textovým popisem chyby.
	 * @param trip záznam pro vložení
	 * @return Status s informací zda vložení probìhlo v poøádku nebo ne
	 * @throws Exception 
	 */
	public Status addTrip(Trip trip) throws Exception;

	/**
	 * Aktualizuje záznam v databázi.
	 * Pøed aktualizací provede validaci zda je záznam v poøádku a pokud ne tak jej neuloží.
	 * @param trip záznam pro aktualizaci
	 * @return Status s informací zda aktualizace probìhla v poøádku nebo ne
	 * @throws Exception
	 */
	public Status updateTrip(Trip trip) throws Exception;

	/**
	 * Získá trip z db podle jeho id
	 * @param id
	 * @return Trip pokud byl nalezen jinak null
	 * @throws Exception
	 */
	public Trip getTripById(String id) throws Exception;

	/**
	 * vrací seznam všech tripù v databázi
	 * @return
	 * @throws Exception
	 */
	public List<Trip> getTripList() throws Exception;

	/**
	 * Dohledá záznamy na základì klíèových slov 
	 * slova jsou hledány pomocí klauzule like (oboustranné)
	 * Pokud je jeden záznam nalezen vícekrát, je ve výsledném poli pouze jednou
	 * @param keyWords seznam klíèových slov k dohledání
	 * @return nalezený seznam bez duplicit
	 * @throws Exception
	 */
	public List<Trip> getTripListByKeyWords(List<String> keyWords) throws Exception;

	/**
	 * smaže zvolený trip z DB
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public boolean deleteTrip(String id) throws Exception;

	/**
	 * Ovìøí zda existuje ID Tripu v databázi nebo ne
	 * @param id
	 * @return
	 */
	public boolean existsTripIdInDb(String id);
	
	/**
	 * Ovìøí zda je slovo v seznamu zakázaných slov
	 * @param word
	 * @return
	 */
	public boolean isBannedWord(String word);
	
	
	/**
	 * Ovìøí zda uživatel existuje v databázi
	 * @param user
	 * @return
	 */
	public boolean existsUserInDB(User user);
	
	/**
	 * Pøidá uživatele do databáze.
	 * Pøed pøidáním ovìøí zda uživatel existuje. Pokud ano vrátí status s chybou, pokud ne zahashuje heslo a záznam uloží do db
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public Status addUser(User user) throws Exception;

	/**
	 * Získá uživatele z databáze pokud existuje
	 * @param userName
	 * @return Objekt User pokud byl nalezen jinak null
	 * @throws Exception
	 */
	public User getUser(String userName) throws Exception;
}
