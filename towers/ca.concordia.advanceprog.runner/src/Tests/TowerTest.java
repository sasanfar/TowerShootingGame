package Tests;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.concordia.advanceprog.ui.Map;
import ca.concordia.advanceprog.ui.MapGenerator;
import ca.concordia.advanceprog.ui.Status;
import ca.concordia.advanceprog.ui.Tower;
import ca.concordia.advanceprog.ui.TowerStrategy;

/**
 * @author Shahriar, Sasan, Nadir
 *
 */

public class TowerTest {

	private Tower tower;
	private Map map;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		Map m1 = new Map(15, 15);
		tower = new Tower(0, 0, 1, 100, 200);
		m1.Data[1][1] = Map.TOWER;
	}

	@Test
	public void testImproveLevel() {
		tower.improveLevel();
		assertEquals(tower.CurrentAmount, 150);
		assertEquals(tower.Level, 2);
	}

	@Test
	public void testInitialAmount() {
		assertEquals(tower.CurrentAmount, 100);
	}

	@Test
	public void testTimeSpan() {
		assertTrue((new Date().getTime() - tower.lastTimeSpan.getTime()) < 5);
	}

	@Test
	public void testDefaultRange() {
		assertEquals(tower.Range, 2);
	}

	/**
	 * Case for the test if shooting type closest is selected
	 */
	
	@Test
	public void testStrategy() {
		assertEquals(tower.getStrategy(), TowerStrategy.Closest);
	}

	@Test
	public void testCrittersInRange() {
		assertTrue(tower.crittersInRange.size() == 0);
	}
}
