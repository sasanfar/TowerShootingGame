package ca.concordia.advanceprog.elements;

import ca.concordia.advanceprog.ui.Map;

public class MediumLevelCritter extends Critter {

	/**
	 * constructor which called the super class Critter
	 * 
	 * @param map
	 * @param x
	 * @param y
	 */
	public MediumLevelCritter(Map map, float x, float y) {
		super(map, x, y);
		setExistingLife(150);
	}

}
