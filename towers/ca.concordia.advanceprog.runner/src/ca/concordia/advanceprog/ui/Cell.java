package ca.concordia.advanceprog.ui;

import java.io.Serializable;

/**
 * The MapGenerator class is using to make cells of the map based on this
 * structure. It is kind of DataClass that holds the status of cell
 */
public class Cell implements Serializable {
	public Status value;

	/**
	 * Constructor of the class initialized the value with Status.notDecided
	 */
	public Cell() {
		value = Status.notDecided;
	}
}