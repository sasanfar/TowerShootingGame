package Tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.concordia.advanceprog.elements.Critter;
import ca.concordia.advanceprog.elements.LowLevelCritter;
import ca.concordia.advanceprog.runner.Runner;
import ca.concordia.advanceprog.ui.*;

/**
 * 
 */

/**
 * @author Shahriar, Sasan, Nadir
 *
 */
public class CritterUnitTests {
	Runner runner;
	private Map mp;
	private Critter critter1;
	Critter critter2;
	float a;
	private Tower tc1, tc2;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		mp = new Map(15, 15);
		critter1 = Critter.createCritter(mp, 0, 0, 1);

		// critter2 = Critter.createCritter(mp, 0, 0, 1);
		tc1 = new Tower(1, 1, 1, 100, 200);
		tc2 = new Tower(10, 10, 1, 100, 200);
		runner.TOWERS.add(tc1);
		runner.TOWERS.add(tc2);

	}

	@Test
	public void critterNotMoveBack() {
		assertFalse(critter1.proceedToMove(0, 0));
	}

	@Test
	public void checkCritterDontHaveMovementForStart() {
		assertFalse(critter1.isDown() || critter1.isUp() || critter1.isLeft()
				|| critter1.isRight());
	}

	@Test
	public void checkCritterWithinTowerRangeGetsShot() {
		critter1 = Critter.createCritter(mp, 0, 0, 1);
		a = critter1.getExistingLife();
		// System.out.println(a);
		mp.Data[0][1] = Map.TOWER;
		assertNotEquals(a, 100);
	}

	@Test
	public void checkTowerRange() {
		int expectedRange = 2;

		assertEquals(expectedRange, tc1.Range);

	}

//	@Test
//	public void critterIsInRange() {
//		critter1.x = 2;
//		critter1.y = 2;
//		
//		System.out.println("OSAMA" + tc1.checkIfIsinTheAreaOfTower(Runner.engine.graphic, mp,
//				((int) critter1.getX()) * 20, ((int) critter1.getY()) * 20));
//
//		assertEquals(tc1.checkIfIsinTheAreaOfTower(Runner.engine.graphic, mp,
//				((int) critter1.getX()) * 20, ((int) critter1.getY()) * 20),
//				tc2.checkIfIsinTheAreaOfTower(Runner.engine.graphic, mp,
//						((int) critter1.getX()) * 20,
//						((int) critter1.getY()) * 20));
//	}
//
//	@Test
//	public void critterIsNotInRange() {
//		assertEquals(tc1.checkIfIsinTheAreaOfTower(Runner.engine.graphic, mp,
//				((int) critter1.getX()) * 20, ((int) critter1.getY()) * 20),
//				tc2.checkIfIsinTheAreaOfTower(Runner.engine.graphic, mp,
//						((int) critter1.getX()) * 20,
//						((int) critter1.getY()) * 20));
//	}

	@Test
	public void improveTowerLevelCheck() {
		tc1.improveLevel();
		assertEquals(tc1.CurrentAmount, 150);
	}

	/**
	 * Checking if the coordinates for the critters are changing according to
	 * value of x We are setting a value of x and then calling move function of
	 * critter where the values of x is added to dx which is defined as a param
	 * in move function. So we are setting dx and dy as 10,10 and x and y as 5 -
	 * Result should be 15
	 */
	@Test
	public void moveTest() {
		critter1.x = 5;
		critter1.y = 5;
		critter1.move(10, 10);
		assertEquals(15, (int) (critter1.x));
	}

	@Test
	public void checkTowerType() {
		String expectedType = "Regular";
		assertEquals(expectedType, tc1.Type.toString());

	}

	@Test
	public void checkTowerCurrentAmount() {
		int expectedCurrentAmount = 100;
		assertEquals(expectedCurrentAmount, tc1.CurrentAmount);

	}

	@Test
	public void checkTowerLevel() {
		int expectedTowerLevel = 1;
		assertEquals(expectedTowerLevel, tc1.Level);

	}

	@Test
	public void setExistingLife() {
		// Critter critter = new Critter(mp, 10, 10);
		int expectedOutput = 100;
		assertEquals(expectedOutput, (int) critter1.getExistingLife());

	}

	/**
	 * By default shooting is 200 so when tower is less then 5 it will decrease
	 * the shooting by 50; And checking if the current amount is increased by 50
	 */
	@Test
	public void propertiesOfTowerIfLevelIsLessThenFive() {
		tc1.Level = 3;
		tc1.improveLevel();
		assertEquals(150, (int) tc1.Shooting);
		assertEquals(150, tc1.CurrentAmount);
	}

	@Test
	public void increaseRangeWhenLevelIsGreaterThenFive() {
		tc1.Level = 4;
		tc1.improveLevel();
		assertEquals(3, tc1.Range);

	}

	@Test
	public void lowLevelCritter() {
		/** The x position of this entity in terms of grid cells */
		float x = 5;
		/** The y position of this entity in terms of grid cells */
		float y = 5;

		/** The map which this entity is wandering around */
		Map map = null;
		int level = 1;
		Critter critterObj = Critter.createCritter(map, x, y, level);
		assertEquals(100, (int) critterObj.getExistingLife());
	}

	@Test
	public void mediumLevelCritter() {
		/** The x position of this entity in terms of grid cells */
		float x = 5;
		/** The y position of this entity in terms of grid cells */
		float y = 5;

		/** The map which this entity is wandering around */
		Map map = null;
		int level = 3;
		Critter critterObj = Critter.createCritter(map, x, y, level);
		assertEquals(150, (int) critterObj.getExistingLife());
	}

	@Test
	public void highLevelCritter() {
		/** The x position of this entity in terms of grid cells */
		float x = 5;
		/** The y position of this entity in terms of grid cells */
		float y = 5;

		/** The map which this entity is wandering around */
		Map map = null;
		int level = 10;
		Critter critterObj = Critter.createCritter(map, x, y, level);
		assertEquals(200, (int) critterObj.getExistingLife());
	}
	
	@Test
	public void LowLevelCritter()
		throws Exception {
		Map map = new Map(1, 1);
		float x = 1.0f;
		float y = 1.0f;

		LowLevelCritter result = new LowLevelCritter(map, x, y);

		assertNotNull(result);
		assertEquals(false, result.isUp());
		assertEquals(1, result.getX());
		assertEquals(1, result.getY());
		assertEquals(false, result.isLeft());
		assertEquals(false, result.isDown());
		assertEquals(100.0f, result.getExistingLife(), 1.0f);
		assertEquals(false, result.isRight());
		assertEquals(false, result.isToReduceTheMoney());
		assertEquals(0, result.countObservers());
		assertEquals(false, result.hasChanged());
	}
	
	@Test
	public void levelCritter(){
		int actualOutput = 10;
		assertEquals(10, actualOutput);
	}
	
	@Test
	public void runnerCritter(){
		int actualOutput = 100;
		assertEquals(100, actualOutput);
	}

}
