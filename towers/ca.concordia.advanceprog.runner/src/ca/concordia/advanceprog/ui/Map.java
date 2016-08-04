package ca.concordia.advanceprog.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.ArrayList;

import ca.concordia.advanceprog.engine.Engine;
import ca.concordia.advanceprog.runner.Runner;

/**
 * The map is the holder of data that shows which cell of that two dimensional
 * array is block, path or tower
 */
public class Map implements Serializable {
	public MapGenerator MapGenerator;

	public static final int CLEAR = 0;
	public static final int BLOCKED = 1;
	public static final int TOWER = 2;

	public int Width;
	public int Height;

	public static final int TILE_SIZE = 20;

	public float StartColumnIndex;
	public float StartRowIndex;

	public int[][] Data;

	/**
	 * Create a new map with some default contents
	 * 
	 * @param width
	 * @param height
	 */
	public Map(int width, int height) {
		Width = width;
		Height = height;
		Data = new int[Width][Height];
		StartColumnIndex = 0.5f;
		StartRowIndex = 0.5f;
		MapGenerator = new MapGenerator(Height, Width);
	}

	public void setMapGenerator(MapGenerator mapGenerator) {
		this.MapGenerator = mapGenerator;

		this.Width = mapGenerator.getWidth();
		this.Height = mapGenerator.getHeight();

		Data = new int[Width][Height];
		// MapGenerator = mapGenerator;
		// MapGenerator.Map = mapGenerator.Map;
		// MapGenerator.Path = mapGenerator.Path;
		for (int i = 0; i < Width; i++)
			for (int j = 0; j < Height; j++)
				if (MapGenerator.Grid[i][j].value == Status.isPath)
					Data[i][j] = 0;
				else
					Data[i][j] = 1;
	}

	public void generateMap() {

		while (MapGenerator.getPath().size() < 50)
			MapGenerator.RandomGenerate(Height, Width);

		for (int i = 0; i < Width; i++)
			for (int j = 0; j < Height; j++)
				if (MapGenerator.Grid[i][j].value == Status.isPath)
					Data[i][j] = 0;
				else
					Data[i][j] = 1;
	}

	/**
	 * This constructor is used by Load button
	 * 
	 * @param width
	 *            is the width map
	 * 
	 * @param height
	 *            is the height of the map
	 * 
	 * @param mapGenerator
	 *            to generate the path based on the loaded file
	 */
	public Map(int width, int height, MapGenerator mapGenerator) {
		this.MapGenerator = mapGenerator;
		Data = new int[width][height];
		// MapGenerator = mapGenerator;
		// MapGenerator.Map = mapGenerator.Map;
		// MapGenerator.Path = mapGenerator.Path;
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				if (MapGenerator.Grid[i][j].value == Status.isPath)
					Data[i][j] = 0;
				else
					Data[i][j] = 1;

		Width = width;
		Height = height;

		StartColumnIndex = 0.5f;
		StartRowIndex = 0.5f;
	}

	public Map(MapGenerator mapGenerator) {
		MapGenerator = mapGenerator;

		Width = mapGenerator.getWidth();
		Height = mapGenerator.getHeight();

		Data = new int[Width][Height];
		// MapGenerator = mapGenerator;
		// MapGenerator.Map = mapGenerator.Map;
		// MapGenerator.Path = mapGenerator.Path;
		for (int i = 0; i < Width; i++)
			for (int j = 0; j < Height; j++)
				if (MapGenerator.Grid[i][j].value == Status.isPath)
					Data[i][j] = 0;
				else
					Data[i][j] = 1;

		StartColumnIndex = 0.5f;
		StartRowIndex = 0.5f;
	}

	/**
	 * Render the map to the graphics context provided. The rendering is just
	 * simple fill rectangles
	 * 
	 * @param graphic
	 *            The graphics context on which to draw the map
	 */
	public void paint(Graphics2D graphic) {
		for (int x = 0; x < Runner.X_DIMENSION; x++) {
			for (int y = 0; y < Runner.Y_DIMENSION; y++) {

				// so if the cell is blocks, draw a light grey block
				// otherwise use a dark gray
				graphic.setColor(Color.darkGray);
				if (Data[y][x] == BLOCKED) {
					graphic.setColor(Color.white);
				}
				if (Data[y][x] == TOWER) {
					graphic.setColor(Color.blue);
				}
				// draw the rectangle with a dark outline
				graphic.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE,
						TILE_SIZE);
				graphic.setColor(graphic.getColor().darker());
				graphic.drawRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE,
						TILE_SIZE);
			}
		}

		for (int x = 0; x < Width; x++) {
			for (int y = 0; y < Height; y++) {

				if (Data[y][x] == TOWER) {
					graphic.setColor(Color.blue);

					for (int i = 0; i < Runner.TOWERS.size(); i++)
						if (Runner.TOWERS.get(i).x == y
								&& Runner.TOWERS.get(i).y == x) {
							Tower tower = Runner.TOWERS.get(i);
							drawCircle(graphic, x * TILE_SIZE, y * TILE_SIZE,
									tower.Range * 20);
						}
				}

			}
		}

		graphic.setColor(Color.orange);

	}

	/**
	 * This method is used in when we want to show a circle around a tower for
	 * the range of shooting
	 * 
	 * @param graphic
	 *            is the handle for depicting objects on it
	 */
	public void drawCircle(Graphics2D graphic, int x, int y, int radius) {
		graphic.drawOval(x - radius + 10, y - radius + 10, radius * 2,
				radius * 2);
	}

	/**
	 * Check if a particular location on the map is blocked. Note that the x and
	 * y parameters are floating point numbers meaning that we can be checking
	 * partially across a grid cell.
	 * 
	 * @param x
	 *            The x position to check for blocking
	 * @param y
	 *            The y position to check for blocking
	 * @return True if the location is blocked
	 */
	public boolean isBlocked(float x, float y) {
		if (isNotMap(x, y))
			return true;
		else
			return Data[(int) y][(int) x] == BLOCKED;
	}

	/**
	 * This method will tell the caller if it is in the area of map
	 * 
	 * @param x
	 *            is the position in the x-Axis
	 * @param y
	 *            is the position in the y-Axis
	 */
	public boolean isNotMap(float x, float y) {
		if (x < 0 || x > Width || y < 0 || y > Height)
			return true;
		else
			return false;
	}

	/**
	 * This is very useful method that map the point (x,y) to the corresponding
	 * cell in the array of [15][15] or any user preference when wanted to
	 * create the map
	 * 
	 * @param x   the value x in the x-Axis
	 * @param y   the value of y-Axis
	 */
	public static String tryMapXYtoCellNumber(int x, int y) {
		for (int i = 0; i < 400; i = i + 20)
			for (int j = 0; j < 400; j = j + 20) {
				if (x >= i && x <= i + 20)
					if (y >= j && y <= j + 20)
						return ((i / 20) + "," + (j / 20));
			}
		return "";

	}

	/**
	 * This method is responsible for checking if the user created map is valid
	 * or not by checking the start point, end point and the path from start to
	 * end
	 * 
	 * @param entry
	 * @param exit
	 * @param height
	 * @param width
	 * @return
	 */
	public boolean UserDefinedMapValidation(int entry, int exit, int height,
			int width) {
		if (this.MapGenerator.getPath() != null) {
			// this.MapGenerator.setPath(null);
			this.MapGenerator.setPath(new ArrayList<Movement>());
			// System.out.println("Map Editing...");
			for (int i = 0; i < height; i++)
				for (int j = 0; j < width; j++)
					if (this.Data[i][j] == Map.CLEAR)
						this.MapGenerator.Grid[i][j].value = Status.notDecided;
					else
						this.MapGenerator.Grid[i][j].value = Status.isBlock;
			MapGenerator validity = this.MapGenerator.mapEditing(entry, exit,
					height, width);
			if (validity == null)
				return false;
			return true;
		} else {
			// System.out.println("Manually Generating...");
			this.MapGenerator = new MapGenerator(height, width);
			for (int i = 0; i < height; i++)
				for (int j = 0; j < width; j++)
					if (this.Data[i][j] == Map.CLEAR)
						this.MapGenerator.Grid[i][j].value = Status.notDecided;
					else
						this.MapGenerator.Grid[i][j].value = Status.isBlock;
			MapGenerator validity = this.MapGenerator.ManualGenerate(entry,
					exit, height, width);
			if (validity == null)
				return false;

			else {
				// complete map;
			}
			return true;
		}
	}
}
