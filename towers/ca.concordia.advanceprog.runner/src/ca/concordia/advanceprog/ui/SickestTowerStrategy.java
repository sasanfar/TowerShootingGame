package ca.concordia.advanceprog.ui;

import ca.concordia.advanceprog.elements.Critter;

public class SickestTowerStrategy extends AbstractTowerStrategy {

	private Tower tower;

	/**
	 * Sickest tower strategy will fire the critter which has the least health
	 */
	
	public SickestTowerStrategy(Tower tower) {
		this.tower = tower;
	}

	@Override
	public Critter getCritter() {
		Critter tempCritter = null;
		tempCritter = tower.crittersInRange.get(0);
		for (int i = 1; i < tower.crittersInRange.size(); i++)
			if (tower.crittersInRange.get(i).getExistingLife() < tempCritter
					.getExistingLife())
				tempCritter = tower.crittersInRange.get(i);
		return tempCritter;
	}

}
