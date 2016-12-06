package cz.holub.myTrips.logic;

import org.springframework.beans.factory.annotation.Autowired;

import cz.holub.myTrips.dao.DataDao;
import cz.holub.myTrips.domain.Tag;


public class TagLogic {
	@Autowired
	DataDao dataDao;

	/**
	 * Ovìøí zda obsah tagu neobsahuje zakázaná slova
	 * @param tag
	 * @return true - obsahuje zakázaná slova, false - neobsahuje.
	 */
	public boolean hasTagBannedContent(Tag tag) {
		boolean res= false;
		String tagStr= tag.getTag();
		String wordToCheck[]= tagStr.split(" ");
		for (String word: wordToCheck) {
			if (dataDao.isBannedWord(word)) {
				res= true;
				break; // nemá smysl pokraèovat našli jsme zakázané slovo
			}
		}
		return res;
	}
}
