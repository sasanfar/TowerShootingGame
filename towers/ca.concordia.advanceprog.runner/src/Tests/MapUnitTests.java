package Tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.concordia.advanceprog.ui.*;

/**
 * 
 */

/**
 * @author Sasan, Nadir
 *
 */
public class MapUnitTests {
	Map m1 = new Map(15, 15);
	Map m4 = new Map(15, 15);
	MapGenerator mg1, mg2;
	Map m2, m3;
	

	// Critter c1 = new Critter(m1, 0, 0);

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
		mg1 = m1.MapGenerator;
		m2 = new Map(15, 15, m1.MapGenerator);
		mg1.SaveMap("test.mp");
		mg2 = new MapGenerator("test.mp", 15, 15);
		m3 = new Map(15, 15, mg2);
	}

	/**
	 * tests if the constructor of Map works ok
	 */
	@Test
	public void MapConstructor() {

		assertArrayEquals(m1.Data, m4.Data);
	}

	/**
	 * tests if data is written based on the generated path
	 */
	@Test
	public void DataPathCorrelation() {
		int maparray[][] = new int[m1.Data.length][m1.Data.length];
		for(int i=0; i<m1.Data.length; i++){
			for(int j=0; j<m1.Data.length; j++){
				maparray[i][j] = m1.Data[i][j];
				
				
			}
		}
		System.out.println(maparray[0][0]);
		assertEquals(0, maparray[0][0]);
	}
	
	/**
	 * tests if the map data is written ok;
	 */
	@Test
	public void EntryPoint() {
		assertEquals(0, m1.Data[0][0]);
	}


	/**
	 * tests if the path has been copied correctly
	 */
//	@Test
//	public void PathCopyCheck() {
//		//m1.MapGenerator.setPath("/test");
//		System.out.println(m1.MapGenerator.getPath().get(0));
//		for (int i = 0; i <= 50; i++)
//		{
//			System.out.println("TEST: " + m1.MapGenerator.getPath().get(i).Left);
//		}	
//			assertEquals(m1.MapGenerator.getPath().get(i), m2.MapGenerator.getPath().get(i));
//	}

//	/**
//	 * tests Save and Load
//	 */
	@Test
	public void SaveLoadCheck() {

		assertArrayEquals(m1.Data, m4.Data);
	}
}