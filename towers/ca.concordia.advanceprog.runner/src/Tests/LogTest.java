package Tests;

import static org.junit.Assert.*;
import ca.concordia.advanceprog.util.*;

import org.junit.Test;
import ca.concordia.advanceprog.util.LogManager;

;
public class LogTest {

	@Test
	public void testEngineLog() {
		LogManager.getLogInstance().logAdd(logType.Engine);
	}

	@Test
	public void testMapLog() {
		LogManager.getLogInstance().logAdd(logType.Map);
	}

	@Test
	public void testTowerLog() {
		LogManager.getLogInstance().logAdd(logType.Tower);
	}

	@Test
	public void testWaveLog() {
		LogManager.getLogInstance().logAdd(logType.Wave);
	}

}
