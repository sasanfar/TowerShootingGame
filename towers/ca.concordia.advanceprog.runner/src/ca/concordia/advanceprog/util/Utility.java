package ca.concordia.advanceprog.util;

import java.util.Random;

/**
 * We can put general purpose methods here, such as collection helpers and this
 * randInt that I need for the initial version All Utility methods are most of
 * the time static
 * 
 * @author Shahriar Rostami
 *
 */
public class Utility {
	public static int randInt(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}
}
