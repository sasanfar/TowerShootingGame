package ca.concordia.advanceprog.engine;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.Serializable;

import ca.concordia.advanceprog.elements.Critter;

/**
 * This class would be used when we want to handle keyboard interactions from
 * the player We may use map it for map creation it extends KeyAdapter
 */
public class ButtonHandler extends KeyAdapter implements Serializable {
	public ButtonHandler() {
		System.out.println(" Button handler initialised! ");
	}

	/**
	 * When keyboard button is pressed by player
	 * 
	 * @see java.awt.event.KeyAdapter#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent key) {

		switch (key.getKeyCode()) {
		case KeyEvent.VK_UP:
			// Critter..get(0).setUp(true);
			break;
		case KeyEvent.VK_DOWN:
			// Critter.critters.get(0).setDown(true);
			break;
		case KeyEvent.VK_LEFT:
			// Critter.critters.get(0).setLeft(true);
			break;
		case KeyEvent.VK_RIGHT:
			// Critter.critters.get(0).setRight(true);
			break;
		}
	}

	/**
	 * When the keyboard button is released by the player
	 * 
	 * @see java.awt.event.KeyAdapter#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent key) {
		switch (key.getKeyCode()) {
		case KeyEvent.VK_UP:
			// Critter.critters.get(0).setUp(false);
			break;
		case KeyEvent.VK_DOWN:
			// Critter.critters.get(0).setDown(false);
			break;
		case KeyEvent.VK_LEFT:
			// Critter.critters.get(0).setLeft(false);
			break;
		case KeyEvent.VK_RIGHT:

			// Critter.critters.get(0).setRight(false);
			break;
		}
	}

}
