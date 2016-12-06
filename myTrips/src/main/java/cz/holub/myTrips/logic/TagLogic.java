package cz.holub.myTrips.logic;

import org.springframework.beans.factory.annotation.Autowired;

import cz.holub.myTrips.dao.DataDao;
import cz.holub.myTrips.domain.Tag;


public class TagLogic {
	@Autowired
	DataDao dataDao;

	/**
	 * Ov��� zda obsah tagu neobsahuje zak�zan� slova
	 * @param tag
	 * @return true - obsahuje zak�zan� slova, false - neobsahuje.
	 */
	public boolean hasTagBannedContent(Tag tag) {
		boolean res= false;
		String tagStr= tag.getTag();
		String wordToCheck[]= tagStr.split(" ");
		for (String word: wordToCheck) {
			if (dataDao.isBannedWord(word)) {
				res= true;
				break; // nem� smysl pokra�ovat na�li jsme zak�zan� slovo
			}
		}
		return res;
	}
}
