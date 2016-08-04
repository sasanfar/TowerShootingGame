package ca.concordia.advanceprog.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Date;

import ca.concordia.advanceprog.elements.Critter;
import ca.concordia.advanceprog.engine.Engine;
import ca.concordia.advanceprog.runner.Runner;

/**
 * Holds information about the tower such as Level and current paid amount for
 * this tower
 * 
 * @author Shahriar
 *
 */
public class Tower implements Observer, Serializable {
	public int x;
	public int y;
	public int Level;
	public int CurrentAmount;
	public int Range = 2;
	public TowerType Type;
	private TowerStrategy strategy;
	public float Shooting;
	public List<Critter> crittersInRange = new ArrayList<Critter>();
	public Date lastTimeSpan;
	public DamageEffect damageEffect;
	private AbstractTowerStrategy towerStrategy;

	/**
	 * Constructor that initialize new values to the public fields
	 * 
	 * @param x
	 * @param y
	 * @param level
	 * @param currentAmount
	 */
	
	public Tower(int x, int y, int level, int currentAmount, int shooting) {
		this.x = x;
		this.y = y;
		this.Level = level;
		this.CurrentAmount = currentAmount;
		this.Type = TowerType.Regular;
		this.setStrategy(TowerStrategy.Closest);
		this.Shooting = shooting;
		this.lastTimeSpan = new Date();
		this.damageEffect = DamageEffect.Splash;
	}

	/**
	 * called when the game needs to go to the next level
	 * 
	 * @return
	 */
	public boolean improveLevel() {
		if (this.Level < 5) {
			this.Shooting -= 50;
			this.Level++;
			this.CurrentAmount += 50;
			if (this.Level >= 3 && this.Level <= 4)
				this.Range++;
			else if (this.Level == 5) {
				this.Range++;
			}
			return true;
		}
		return false;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		Critter critter = (Critter) arg0;
		if (!crittersInRange.contains(critter))
			crittersInRange.add(critter);
	}

	/**
	 * it will check if the critter is in the area of tower that can shoot the
	 * critter
	 * 
	 * @param graphic
	 * @param xp
	 * @param yp
	 * @return true if it is in the area of tower or towers
	 */
	public Tower checkIfIsinTheAreaOfTower(Graphics2D graphic, Map map, int xp,
			int yp) {
		boolean isIn = false;
		for (int x = 0; x < Runner.X_DIMENSION; x++) {
			for (int y = 0; y < Runner.Y_DIMENSION; y++) {

				if (map.Data[y][x] == Map.TOWER) {
					int centerX = x;
					int centerY = y;

					int calculatedRange = Range;
					if (this.damageEffect == DamageEffect.Burning) {
						calculatedRange += 2;
					}
					if (Math.abs((int) centerX - xp / 20)
							+ Math.abs((int) centerY - yp / 20) <= calculatedRange)
						isIn = true;
					else
						isIn = false;

					if (isIn) {
						for (int i = 0; i < Runner.TOWERS.size(); i++)
							if (Runner.TOWERS.get(i).x == centerY
									&& Runner.TOWERS.get(i).y == centerX) {
								Tower tower = Runner.TOWERS.get(i);
								graphic.setColor(Color.yellow);
								drawCircle(graphic, tower.y * 20, tower.x * 20,
										tower.Range * 20,
										tower.Type == Type.Regular ? 1
												: tower.Type == Type.Mass ? 10
														: 5);
								return this;
							}
					}
				}
			}
		}
		if (isIn)
			return this;
		else
			return null;
	}

	/**
	 * This method is used in when we want to show a circle around a tower for
	 * the range of shooting
	 * 
	 * @param graphic
	 *            is the handle for depicting objects on it
	 */
	public void drawCircle(Graphics2D graphic, int x, int y, int radius,
			int stroke) {
		// graphic.setStroke(new BasicStroke(stroke));
		graphic.drawOval(x - radius + 10, y - radius + 10, radius * 2,
				radius * 2);
		// graphic.setStroke(new BasicStroke(1));
	}

	public TowerStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(TowerStrategy strategy) {
		this.strategy = strategy;
		switch (strategy) {
		case Closest:
			towerStrategy = new ClosestTowerStrategy(this);
			break;
		case Farthest:
			towerStrategy = new FarthestTowerStrategy(this);
		case Healthiest:
			towerStrategy = new HealthiestTowerStrategy(this);
		case Sickest:
			towerStrategy = new SickestTowerStrategy(this);
		default:
			break;
		}
	}

	public AbstractTowerStrategy getTowerStrategy() {
		return towerStrategy;
	}

	public void setTowerStrategy(AbstractTowerStrategy towerStrategy) {
		this.towerStrategy = towerStrategy;
	}

}
