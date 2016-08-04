package ca.concordia.advanceprog.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.io.Serializable;

import javax.swing.JFrame;
import javax.swing.JPanel;

import ca.concordia.advanceprog.engine.Engine;

/**
 * This is the main element of the game, Surface tends to hold and draw each and
 * every UI elements we have in the game It should be complete over the time
 * Creation of Surface is the common thing between all games and you can find
 * many similar things like this which actually this one is also influenced by a
 * piece of code I found somewhere else.
 * 
 * @author Shahriar Rostami
 *
 */
public class Surface implements Serializable {
	/** The buffered strategy used for accelerated rendering */
	public transient BufferStrategy Strategy;

	public float prevRow;
	public float prevCol;

	public Boolean RowChanged = false;
	public Boolean PreviousRowLeftBlock = false;
	public Boolean PreviousRowRightBlock = false;

	public Map map;
	public JPanel Panel;
	public static JFrame Frame;

	/**
	 * Create the simple game - this also starts the game loop
	 */
	public Surface(Map map, Engine engine) {
		this.map = map;
		Strategy = engine.getStrategy();
	}

	public void refreshSurface(Graphics2D g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, 500, 500);

		g.translate(100, 100);
		map.paint(g);

	}

}
