package Tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.concordia.advanceprog.ui.Cell;
import ca.concordia.advanceprog.ui.Status;

public class CellTest {
	private Cell cell;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		cell = new Cell();
	}

	@Test
	public void cellDefaultValue() {
		assertEquals(cell.value, Status.notDecided);
	}
}
