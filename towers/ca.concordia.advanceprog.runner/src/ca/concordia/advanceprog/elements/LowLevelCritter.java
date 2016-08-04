package ca.concordia.advanceprog.elements;

import ca.concordia.advanceprog.ui.Map;

/**
 * Representation of Critters which is created in level 1
 * 
 * @author Shahriar
 *
 */
public class LowLevelCritter extends Critter {

	/**
	 * constructor which called the super class Critter
	 * 
	 * @param map
	 * @param x
	 * @param y
	 */
	public LowLevelCritter(Map map, float x, float y) {
		super(map, x, y);
		setExistingLife(100);
	}

}
