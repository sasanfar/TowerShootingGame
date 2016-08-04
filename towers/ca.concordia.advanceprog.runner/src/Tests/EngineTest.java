package Tests;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.concordia.advanceprog.engine.Engine;
import ca.concordia.advanceprog.runner.Runner;
import ca.concordia.advanceprog.ui.Map;
import ca.concordia.advanceprog.ui.MapGenerator;
import ca.concordia.advanceprog.ui.Tower;

/**
 * @author Nadir
 *
 */

public class EngineTest {

	private Engine engine;
	MapGenerator MG = new MapGenerator(15, 15);
	Map map= new Map(15,15);
	Tower tower= new Tower(3, 3, 1, 100, 200);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		map.setMapGenerator(MG);
		engine = Engine.initialEngine(map,true);
		engine.MONEY = 1000;
	}

	@Test
	public void initialMoney() {
		assertEquals(engine.MONEY, 1000);
	}
	
	@Test
	public void checkMapAssingment() {
		assertArrayEquals(engine.MAP.Data, map.Data);
	}
	
	@Test
	public void checkInitWave() {
		assertEquals(engine.gameLevelandNumberofCritters,1);
	}
	
	@Test
	public void moneyDeposit() {
		
	}

	@Test
	/**
	 * return true if money is greater then 100
	 */
	public void checkBalanceIfGreaterThenHundred() {
		engine.MONEY = 500;
		Boolean result = engine.checkBalance();
		assertEquals(true, result);
	}
	
	@Test
	/**
	 * return false if money is less then 100
	 */
	public void checkBalanceIfLessThenHundred() {
		engine.MONEY = 80;
		Boolean result = engine.checkBalance();
		assertEquals(false, result);
	}
	
	@Test
	/**
	 * Test if IS_CREATE CRITTER is true when the next level starts
	 */
	public void nextLevel() {
		boolean expectedOutPut = true;
		engine.nextLevel();
		//System.out.println(engine.gameLevelandNumberofCritters);
		assertEquals(expectedOutPut, Engine.IS_CREATE_CRITTER);
	}
	
	/**
	 * Test if the money is increasing after being victorious in a wave
	 */
	
	@Test
	public void nextLevelDepositCheck() {
		int expectedOutput = engine.MONEY; // 1000 by default
		engine.nextLevel();
		int actualResult = engine.MONEY; // should be 1500 now
		assertEquals(1500, actualResult);
	}
}
