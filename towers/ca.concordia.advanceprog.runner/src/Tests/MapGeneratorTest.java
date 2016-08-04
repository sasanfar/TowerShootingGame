package Tests;

import java.util.ArrayList;

import org.junit.*;

import ca.concordia.advanceprog.ui.MapGenerator;
import ca.concordia.advanceprog.ui.Movement;
import static org.junit.Assert.*;


public class MapGeneratorTest {
	
	@Test(expected = java.io.FileNotFoundException.class)
	public void MapGenerator()
		throws Exception {
		String file = "";
		int height = 1;
		int width = 1;

		MapGenerator result = new MapGenerator(file, height, width);

		assertNotNull(result);
	}
	
	@Test(expected = java.io.FileNotFoundException.class)
	public void MapGenerator1()
		throws Exception {
		String file = "";
		int height = 1;
		int width = 0;

		MapGenerator result = new MapGenerator(file, height, width);

		assertNotNull(result);
	}

	@Test(expected = java.io.FileNotFoundException.class)
	public void MapGenerator2()
		throws Exception {
		String file = "";
		int height = 0;
		int width = 1;

		MapGenerator result = new MapGenerator(file, height, width);

		assertNotNull(result);
	}
	
	@Test
	public void Deadend()
		throws Exception {
		MapGenerator fixture = new MapGenerator(2, 1);
		fixture.ManualGenerate(1, 1, 1, 1);
		int i = 1;
		int j = 1;

		boolean result = fixture.Deadend(i, j);

		assertEquals(false, result);
	}

	@Test
	public void DeadendMapchanged()
		throws Exception {
		MapGenerator fixture = new MapGenerator(2, 2);
		fixture.ManualGenerate(1, 1, 1, 1);
		int i = 1;
		int j = 1;

		boolean result = fixture.Deadend(i, j);

		assertEquals(false, result);
	}
	
	@Test
	public void MapGenerator3()
		throws Exception {
		int height = 1;
		int width = 1;

		MapGenerator result = new MapGenerator(height, width);

		assertNotNull(result);
		assertEquals(1, result.getHeight());
		assertEquals(1, result.getWidth());
		assertEquals(0, result.getStartinPoint());
		assertEquals(0, result.getEndPoint());
	}
	
	@Test
	public void MapGenerator4()
		throws Exception {
		int height = 1;
		int width = 0;

		MapGenerator result = new MapGenerator(height, width);

		assertNotNull(result);
		assertEquals(1, result.getHeight());
		assertEquals(0, result.getWidth());
		assertEquals(0, result.getStartinPoint());
		assertEquals(0, result.getEndPoint());
	}
	
	@Test
	public void MapGenerator5()
		throws Exception {
		int height = 0;
		int width = 1;

		MapGenerator result = new MapGenerator(height, width);

		assertNotNull(result);
		assertEquals(0, result.getHeight());
		assertEquals(1, result.getWidth());
		assertEquals(0, result.getStartinPoint());
		assertEquals(0, result.getEndPoint());
	}
	
	@Test
	public void SetPath()
		throws Exception {
		ArrayList<Movement> path = new ArrayList<Movement>();
		path.add(Movement.Down);
		path.add(Movement.Left);
		path.add(Movement.Right);
		path.add(Movement.Up);

		MapGenerator.setPath(path);
	}
	
	

}
