package Tests;

import static org.junit.Assert.assertNotNull;

import java.awt.image.BufferStrategy;

import org.junit.Test;

import ca.concordia.advanceprog.engine.Engine;
import ca.concordia.advanceprog.ui.Map;
import ca.concordia.advanceprog.ui.Surface;



public class SurfaceTest {

	@Test
	public void testSurface()
		throws Exception {
		Map map = new Map(1, 1);
		Engine engine = Engine.initialEngine(new Map(1, 1), true);
		engine.setStrategy((BufferStrategy) null);

		Surface result = new Surface(map, engine);

		assertNotNull(result);
	}
	
}
