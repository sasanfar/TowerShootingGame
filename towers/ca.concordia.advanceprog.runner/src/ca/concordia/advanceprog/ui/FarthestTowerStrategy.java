package ca.concordia.advanceprog.ui;

import ca.concordia.advanceprog.elements.Critter;

public class FarthestTowerStrategy extends AbstractTowerStrategy {

	private Tower tower;

	/**
	 * Farthest tower strategy will fire the critter which is farthest 
	 * to the tower, max values are noted and the one with the most maximum is shot
	 */
	
	public FarthestTowerStrategy(Tower tower) {
		this.tower = tower;
	}

	
	@Override
	public Critter getCritter() {
		Critter tempCritter = null;
		int maxDX = 0;
		int maxDY = 0;
		for (int i = 0; i < tower.crittersInRange.size(); i++)
			if (Math.abs(tower.crittersInRange.get(i).getX() - tower.x) > maxDX
					|| Math.abs(tower.crittersInRange.get(i).getY() - tower.y) > maxDY) {
				maxDX = Math.abs((tower.crittersInRange.get(i).getX())
						- tower.x);
				maxDY = Math.abs((tower.crittersInRange.get(i).getY())
						- tower.y);
				tempCritter = tower.crittersInRange.get(i);
			}
		return tempCritter;
	}

}
