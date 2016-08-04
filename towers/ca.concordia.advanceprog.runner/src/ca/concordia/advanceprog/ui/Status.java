package ca.concordia.advanceprog.ui;

import java.io.Serializable;

/**
 * possible values of each cell in the map
 * 
 * @author Shahriar
 *
 */
public enum Status implements Serializable {
	isPath, isBlock, notDecided, isTower;
}