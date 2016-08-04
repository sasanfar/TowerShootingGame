package ca.concordia.advanceprog.engine;

import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ca.concordia.advanceprog.elements.Critter;
import ca.concordia.advanceprog.runner.Runner;
import ca.concordia.advanceprog.ui.Map;
import ca.concordia.advanceprog.ui.Surface;
import ca.concordia.advanceprog.ui.Tower;
import ca.concordia.advanceprog.ui.TowerStrategy;
import ca.concordia.advanceprog.ui.TowerType;
import ca.concordia.advanceprog.util.LogManager;
import ca.concordia.advanceprog.util.logType;

/**
 * This class is responsible for the game logic including the movements,
 * creation of critters, accounting of the game and so on. In case of
 * restarting, this class would be initialized again and its noteworthy to know
 * that we make the constructor of this class private and we are going to
 * initialize this class using initialEngine which is a static method returning
 * new instance of class
 * 
 * @author Shahriar Rostami
 *
 */

public class Engine extends Observable implements Observer, Serializable {
	public JPanel Panel;

	public Map MAP;
	public int MONEY = 1000;

	public Surface Surface;

	public List<Critter> critters;
	public static int gameLevelandNumberofCritters = 1;

	public static boolean IS_CREATE_CRITTER = true;
	public transient Graphics2D graphic;
	private static Engine engine;

	private transient BufferStrategy strategy;

	/**
	 * Static method which is responsible for creating an instance of class
	 * Engine and is accessible from outside because it's static method
	 */
	public static Engine initialEngine(Map map, boolean createNew) {
		if (engine == null || createNew)
			engine = new Engine(map);
		return engine;
	}

	/**
	 * private constructor of Engine class that initialize map, map validation,
	 * set the default value for money (1000), create surface based on the new
	 * map. Here we also create two critter for our game and can be dynamic
	 * based on the level of the game. For now we hard coded the creation of
	 * these two critters.
	 */
	private Engine(Map map) {
		MAP = map;
		Surface = new Surface(MAP, this);
		critters = new ArrayList<Critter>();
		for (int i = 0; i < gameLevelandNumberofCritters; i++) {
			Critter critter = Critter.createCritter(MAP, MAP.StartColumnIndex,
					MAP.StartRowIndex, gameLevelandNumberofCritters);
			critters.add(critter);
			critter.addObserver(this);
		}

	}

	/**
	 * This method is from interface Observer and is called using other
	 * observers
	 */
	@Override
	public void update(Observable critterArg, Object towerArg) {
		Critter critter = (Critter) critterArg;
		Tower tower = (Tower) towerArg;
		Critter tempCritter = tower.getTowerStrategy().getCritter();
		if (critter == tempCritter) {
			Date dateobj = new Date();
			if (dateobj.getTime() - tower.lastTimeSpan.getTime() > tower.Shooting) {
				int shootingType = 0;
				if (tower.Type == TowerType.Regular)
					shootingType = 1;
				else if (tower.Type == TowerType.Mass)
					shootingType = 10;
				else {
					shootingType = 2;
					tower.Shooting = 50;
				}

				switch (tower.damageEffect) {
				case Splash:
					critter.setExistingLife((int) (critter.getExistingLife() - shootingType));
					break;
				case Freezing:
					if (critter.freezingTimeSpan != null
							&& dateobj.getTime()
									- critter.freezingTimeSpan.getTime() > 2000) {
						critter.start();
					}

					if (critter.freezingTimeSpan == null
							|| dateobj.getTime()
									- critter.freezingTimeSpan.getTime() > 5000) {
						critter.stop();
						critter.freezingTimeSpan = dateobj;
					}
					critter.setExistingLife((int) (critter.getExistingLife() - shootingType));
					break;
				case Burning:
					if (critter.lastShootingTimeSpan != null) {
						if (dateobj.getTime()
								- critter.lastShootingTimeSpan.getTime() > 1000)
							critter.setExistingLife((int) (critter
									.getExistingLife() - shootingType));
					}
					break;
				}

				tower.lastTimeSpan = dateobj;
			}
		}
	}

	/**
	 * this method is responsible for reseting game settings
	 */
	public void resetMoney() {
		MONEY = 1000;
		setChanged();
		notifyObservers(this);
	}

	/**
	 * This is part of accounting system of the our game which will reduce the
	 * amount of the user deposited. Then it will refresh the text of label for
	 * showing the remaining amount of money
	 * 
	 * @param amount
	 *            represents the amount of money should be reduced from the
	 *            deposit
	 */
	public void withDraw(int amount) {

		if (checkBalance())
			MONEY -= amount;
		setChanged();
		notifyObservers(this);
	}

	/**
	 * This method will act in opposite of withdraw. it is used full when the
	 * player sells the tower for half of the prices it pays for.
	 */
	public void deposit(int amount) {
		MONEY += amount;
		setChanged();
		notifyObservers(this);
	}

	/**
	 * It will be called whenever we want to make sure the player wont exceed
	 * the amount it has for buying towers.
	 * 
	 * @return will return true/false to indicate if the user positive balance
	 *         or not.
	 */
	public boolean checkBalance() {
		if (MONEY >= 100)
			return true;
		else
			return false;
	}

	/**
	 * It is used to set the private value of strategy for internal usages will
	 * assign BufferStrategy to local variable strategy.
	 * 
	 * @param strategy value of this parameter should be correspond to the Surface which itself is extending the canvas
	 */
	public void setStrategy(BufferStrategy strategy) {
		this.strategy = strategy;
		Surface.Strategy = strategy;
	}

	/**
	 * It is used to set the private value of strategy for internal usages will
	 * assign BufferStrategy to local variable strategy. value of this parameter should be correspond to the Surface 
	 * which itself is extending the canvas
	 */
	public BufferStrategy getStrategy() {
		return this.strategy;
	}

	/**
	 * This public method is playing an important role in the game. It has the
	 * game logic inside to be called by the thread frequently.
	 */
	public void playing() {
		if (Runner.userEditing) {
			critters = null;
			refreshSurface();
			strategy.show();
			return;
		}
		if (critters == null) {
			critters = new ArrayList<Critter>();
			for (int i = 0; i < gameLevelandNumberofCritters; i++)
				if (critters.size() < gameLevelandNumberofCritters) {
					Critter critter = Critter.createCritter(MAP,
							MAP.StartColumnIndex, MAP.StartRowIndex,
							gameLevelandNumberofCritters);
					critter.addObserver(this);
					critters.add(critter);

					for (Tower tower : Runner.TOWERS) {
						critter.addObserver(tower);
					}
				}
		}

		for (int i = 0; i < gameLevelandNumberofCritters; i++)
			if (critters.get(i).isToReduceTheMoney()) {
				deposit(200);

				LogManager.getLogInstance().logAdd(
						logType.Wave,
						"Critter is killed at: (" + critters.get(i).x + ","
								+ critters.get(i).y + ") ");

				LogManager.getLogInstance().logAdd(logType.Engine,
						"Money deposited: (" + 200 + ") ");

				critters.get(i).setToReduceTheMoney(false);
			}

		boolean toNewLevel = true;
		for (int i = 0; i < gameLevelandNumberofCritters; i++)
			if (critters.get(i).getExistingLife() > 0) {
				toNewLevel = false;
			}

		/**
		 * if all critters killed, go to next level
		 */
		
		if (toNewLevel) {
			critters = null;
			nextLevel();
			return;
		}

		graphic = refreshSurface();

		for (int i = 0; i < gameLevelandNumberofCritters; i++)
			critters.get(i).paint(graphic);

		strategy.show();
		try {
			Thread.sleep(4);
		} catch (Exception e) {

		}

		for (int index = 0; index < gameLevelandNumberofCritters; index++)
			critters.get(index).moving(5 + index, MAP);

		try {
			for (int index = 0; index < gameLevelandNumberofCritters; index++) {
				critters.get(index).moving(index % 5, MAP);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	/**
	 * depicting latest changes on the screen
	 * 
	 * @return
	 */
	public Graphics2D refreshSurface() {
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		Surface.refreshSurface(g);
		strategy.show();
		return g;
	}

	/**
	 * Transition from one level to another
	 */
	public void nextLevel() {
		JOptionPane.showMessageDialog(null,
				"You Are Victorious! Press start and go to level: "
						+ (gameLevelandNumberofCritters + 1));

		LogManager.getLogInstance().logAdd(
				logType.Wave,
				"Level completed. Next level: ("
						+ (gameLevelandNumberofCritters + 1) + ") ");

		Engine.IS_CREATE_CRITTER = true;
		gameLevelandNumberofCritters++;
		deposit(500);
	}

	/**
	 * This static method is called when the player cannot defend well from
	 * critters and one critter reach the end point
	 */
	public void setGameOver() {
		JOptionPane.showMessageDialog(null, "Game Over");

		LogManager.getLogInstance().logAdd(logType.Engine, "Game over.");

		critters = null;
		for (Tower tower : Runner.TOWERS) {
			MAP.Data[tower.x][tower.y] = Map.BLOCKED;
		}
		Runner.TOWERS.clear();
		Engine.IS_CREATE_CRITTER = true;
		gameLevelandNumberofCritters = 1;

	}

}
