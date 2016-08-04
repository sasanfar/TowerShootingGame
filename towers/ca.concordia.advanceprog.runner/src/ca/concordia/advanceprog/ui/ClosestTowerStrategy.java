package ca.concordia.advanceprog.ui;

import ca.concordia.advanceprog.elements.Critter;

public class ClosestTowerStrategy extends AbstractTowerStrategy {

	private Tower tower;
	
	/**
	 * Closest tower strategy will fire the critter which is closest 
	 * to the tower, min values are noted and the one with the most minimum is shot
	 */

	public ClosestTowerStrategy(Tower tower) {
		this.tower = tower;
	}

	@Override
	public Critter getCritter() {
		Critter tempCritter = null;
		int minDX = 1000;
		int minDY = 1000;
		for (int i = 0; i < tower.crittersInRange.size(); i++)
			if (Math.abs((tower.crittersInRange.get(i).getX()) - tower.x) < minDX
					|| Math.abs((tower.crittersInRange.get(i).getY()) - tower.y) < minDY) {
				minDX = Math.abs((tower.crittersInRange.get(i).getX())
						- tower.x);
				minDY = Math.abs((tower.crittersInRange.get(i).getY())
						- tower.y);
				tempCritter = tower.crittersInRange.get(i);
			}
		return tempCritter;
	}

}
