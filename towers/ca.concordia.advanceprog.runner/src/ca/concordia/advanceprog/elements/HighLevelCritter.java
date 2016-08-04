package ca.concordia.advanceprog.elements;

import ca.concordia.advanceprog.ui.Map;

/**
 * Representation of Critters which is created in level 5
 * 
 * @author Shahriar
 *
 */
public class HighLevelCritter extends Critter {

	/**
	 * constructor which called the super class Critter
	 * 
	 * @param map
	 * @param x
	 * @param y
	 */
	public HighLevelCritter(Map map, float x, float y) {
		super(map, x, y);
		setExistingLife(200);
	}

}
