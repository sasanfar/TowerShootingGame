package ca.concordia.advanceprog.ui;

import java.util.ArrayList;

import java.io.*;
import java.util.List;
import java.util.Random;

import ca.concordia.advanceprog.runner.Runner;

/**
 * Creates a randomized map and a path from the starting point to the end point
 * in it.
 * 
 * @author Sasan
 *
 */
public class MapGenerator implements Serializable {

	public static Cell Grid[][];
	public static List<Movement> Path;

	private int Height, Width, StartingPoint, EndPoint;
	final public static String EndOfPath = "$$##@@%%&";

	public MapGenerator(int height, int width) {
		Grid = new Cell[height][width];
		Path = new ArrayList<Movement>();
		Height = height;
		Width = width;
		StartingPoint = 0;
		EndPoint = 0;
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++)
				Grid[i][j] = new Cell();

		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++)
				Grid[i][j].value = Status.notDecided;

	}

	/**
	 * getter of StartingPoint
	 * 
	 * @return Starting Point
	 */
	public int getStartinPoint() {
		return this.StartingPoint;
	}

	/**
	 * getter of EndPoint
	 * 
	 * @return EndPoint
	 */
	public int getEndPoint() {
		return this.EndPoint;
	}

	/**
	 * get the Width 
	 */
	public int getWidth() {
		return this.Width;
	}

	public int getHeight() {
		return this.Height;
	}

	/**
	 * After loading map based on the file path in will fill it with new cell
	 * for the path
	 * 
	 * @param file
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public MapGenerator(String file, int height, int width)
			throws NumberFormatException, IOException {
		Grid = new Cell[height][width];
		Path = new ArrayList<Movement>();
		StartingPoint = 0;
		EndPoint = 0;
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++)
				Grid[i][j] = new Cell();

		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++)
				Grid[i][j].value = Status.notDecided;
		LoadMap(file);
	}

	/**
	 * Save map with based on the given path
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void SaveMap(String file) throws IOException {
		File f = new File(file);
		if (f.exists())
			f.delete();
		FileOutputStream is = new FileOutputStream(f);
		OutputStreamWriter osw = new OutputStreamWriter(is);
		Writer writer = new BufferedWriter(osw);
		writer.write(Height + "\r\n");
		writer.write(Width + "\r\n");
		writer.write(this.StartingPoint + "\r\n");
		writer.write(this.EndPoint + "\r\n");
		for (int i = 0; i < MapGenerator.Path.size(); i++)
			writer.write(MapGenerator.Path.get(i).toString() + "\r\n");

		writer.write(EndOfPath + System.getProperty("line.separator"));
		writer.close();
		System.out.println("File saved!");
	}

	/**
	 * This method will read the file to create a new map based on the inputs
	 * 
	 * @param file
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public MapGenerator LoadMap(String file) throws NumberFormatException,
			IOException {
		System.out.println("Loading...");
		BufferedReader br = new BufferedReader(new FileReader(file));
		try {
			String line = br.readLine();
			this.Height = Integer.parseInt(line);
			line = br.readLine();
			this.Width = Integer.parseInt(line);
			line = br.readLine();
			this.StartingPoint = Integer.parseInt(line);
			line = br.readLine();
			this.EndPoint = Integer.parseInt(line);
			line = br.readLine();
			while (line != null) {
				switch (line) {
				case "Up":
					this.Path.add(Movement.Up);
					break;
				case "Down":
					this.Path.add(Movement.Down);
					break;
				case "Right":
					this.Path.add(Movement.Right);
					break;
				case "Left":
					this.Path.add(Movement.Left);
					break;
				default:
					break;
				}
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		int curI = this.StartingPoint, curJ = 0;
		this.Grid[curI][curJ].value = Status.isPath;
		for (int i = 0; i < this.Path.size(); i++) {
			Movement dir = this.Path.get(i);
			switch (dir) {
			case Up:
				// Go Up
				curI--;
				break;
			case Down:
				// Go Down
				curI++;
				break;
			case Left:
				// Go Left
				curJ--;
				break;
			case Right:
				// Go Right
				curJ++;
				break;
			default:
				break;
			}
			if (curI < Height && curI >= 0 && curJ < Width && curJ >= 0)
				this.Grid[curI][curJ].value = Status.isPath;
		}
		for (int i = 0; i < Height; i++)
			for (int j = 0; j < Width; j++)
				if (this.Grid[i][j].value != Status.isPath)
					this.Grid[i][j].value = Status.isBlock;

		// Printing
		System.out.println("Map Loaded:");
		for (int i = 0; i < Height; i++) {
			System.out.print("");
			for (int j = 0; j < Width; j++)
				if (MapGenerator.Grid[i][j].value == Status.isPath)
					System.out.print("");
				else
					System.out.print("");
			System.out.println();
		}

		for (int i = 0; i < this.Path.size(); i++)
			System.out.print(this.Path.get(i) + " ");

		return this;

	}

	/**
	 * will check if the path has reached a dead-end
	 * 
	 * @param i
	 * @param j
	 * @return true if it has found any dead-end
	 */
	public boolean Deadend(int i, int j) {
		try {
			if ((i >= 0 && i <= Height - 2 && Grid[i + 1][j].value == Status.notDecided)
					|| (i >= 1 && i < Height && Grid[i - 1][j].value == Status.notDecided)
					|| (j >= 0 && j <= Width - 2 && Grid[i][j + 1].value == Status.notDecided)
					|| (j >= 1 && j < Width && Grid[i][j - 1].value == Status.notDecided))
				return false;
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Deprecate method that we used to search the path for adjacency with
	 * another path when we are going to make the map based on user creation of
	 * map
	 * 
	 * @param i
	 * @param j
	 * @param Dir
	 * @return
	 */
	public boolean DestinationCellAdjIsZero(int i, int j, Movement Dir) {
		int adj = 0;
		switch (Dir) {
		case Up:
			if (i > 0 && j > 0 && Grid[i - 1][j - 1].value == Status.isPath) // left
																				// of
																				// destination
																				// cell
				adj++;
			if (i > 0 && j < Width - 1
					&& Grid[i - 1][j + 1].value == Status.isPath) // right of
																	// destination
																	// cell
				adj++;
			if (i > 1 && Grid[i - 2][j].value == Status.isPath) // top of
																// destination
																// cell
				adj++;
			break;
		case Down:
			if (i < Height - 1 && j > 0
					&& Grid[i + 1][j - 1].value == Status.isPath) // left of
																	// destination
																	// cell
				adj++;
			if (i < Height - 1 && j < Width - 1
					&& Grid[i + 1][j + 1].value == Status.isPath) // right of
																	// destination
																	// cell
				adj++;
			if (i < Height - 2 && Grid[i + 2][j].value == Status.isPath) // bottom
																			// of
																			// destination
																			// cell
				adj++;
			break;
		case Left:
			if (i < Height - 1 && j > 0
					&& Grid[i + 1][j - 1].value == Status.isPath) // bottom of
																	// destination
																	// cell
				adj++;
			if (i > 0 && j > 0 && Grid[i - 1][j - 1].value == Status.isPath) // top
																				// of
																				// destination
																				// cell
				adj++;
			if (j > 1 && Grid[i][j - 2].value == Status.isPath) // left of
																// destination
																// cell
				adj++;
			break;
		case Right:
			if (i < Height - 1 && j < Width - 1
					&& Grid[i + 1][j + 1].value == Status.isPath) // bottom of
																	// destination
																	// cell
				adj++;
			if (i > 0 && j < Width - 1
					&& Grid[i - 1][j + 1].value == Status.isPath) // top of
																	// destination
																	// cell
				adj++;
			if (j < Width - 2 && Grid[i][j + 2].value == Status.isPath) // right
																		// of
																		// destination
																		// cell
				adj++;
			break;
		default:
			break;
		}
		if (adj == 0)
			return true;
		return false;
	}

	/**
	 * Check the new movement is possible or not
	 * 
	 * @param i
	 *            is the value on the X-Axis
	 * @param j
	 *            is the value on Y-Axis
	 * @param Dir
	 *            the next movement
	 * @return true if it's possible and false if it's not possible
	 */
	public boolean MoveIsPossible(int i, int j, Movement Dir) {
		try {
			if (((i > 0 && Grid[i - 1][j].value == Status.notDecided && Dir == Movement.Up)
					|| (i < Height - 1
							&& Grid[i + 1][j].value == Status.notDecided && Dir == Movement.Down)
					|| (j > 0 && Grid[i][j - 1].value == Status.notDecided && Dir == Movement.Left) || (j < Width - 1
					&& Grid[i][j + 1].value == Status.notDecided && Dir == Movement.Right))
			// && DestinationCellAdjIsZero(i, j, Dir)
			)
				return true;
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * Generating a new map based on random numbers that starts with point(0,0)
	 * and finished with the right edge.
	 * 
	 * @return MapGenerator the generated map
	 */
	public MapGenerator RandomGenerate(int height, int width) {

		// Initializing
		Random rnd = new Random();
		int in = 0;
		int out = rnd.nextInt(height);
		StartingPoint = in;
		EndPoint = out;
		Runner.entry = StartingPoint;
		Runner.exit = EndPoint;
		// int in = scn.nextInt(height);
		// int out = scn.nextInt(height);

		int cur_i = 0, cur_j = 0, Dir;
		Movement Direction = null;

		Grid[cur_i][cur_j].value = Status.isPath;

		// Random Generation
		while (!(cur_i == out && cur_j == width - 1)) {
			if (!Deadend(cur_i, cur_j)) {
				Dir = rnd.nextInt(4);
				switch (Dir) {
				case 0:
					Direction = Movement.Up;
					break;
				case 1:
					Direction = Movement.Down;
					break;
				case 2:
					Direction = Movement.Left;
					break;
				case 3:
					Direction = Movement.Right;
					break;
				default:
					break;
				}
				if (MoveIsPossible(cur_i, cur_j, Direction)) {
					// Update(cur_i, cur_j, Direction);
					switch (Direction) {
					case Up:
						// Go Up
						cur_i--;
						break;
					case Down:
						// Go Down
						cur_i++;
						break;
					case Left:
						// Go Left
						cur_j--;
						break;
					case Right:
						// Go Right
						cur_j++;
						break;
					default:
						break;
					}
					Grid[cur_i][cur_j].value = Status.isPath;
					Path.add(Direction);
				}
			} else {
				// Go back a move
				Grid[cur_i][cur_j].value = Status.isBlock;
				Movement LastMove = Path.get(Path.size() - 1);
				if (LastMove == null)
					RandomGenerate(height, width);
				Path.remove(Path.size() - 1);
				switch (LastMove) {
				case Up:
					// Go back down
					cur_i++;
					break;
				case Down:
					// Go back up
					cur_i--;
					break;
				case Left:
					// Go back right
					cur_j++;
					break;
				case Right:
					// Go back left
					cur_j--;
					break;
				default:
					break;
				}
			}
		}

		// Finalizing
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++)
				if (Grid[i][j].value == Status.notDecided)
					Grid[i][j].value = Status.isBlock;

		// Printing
		System.out.println("Map Created:");
		for (int i = 0; i < height; i++) {
			System.out.print("|");
			for (int j = 0; j < width; j++)
				if (Grid[i][j].value == Status.isPath)
					System.out.print("X|");
				else
					System.out.print(" |");
			System.out.println();
		}

		for (int i = 0; i < Path.size(); i++)
			System.out.print(Path.get(i) + " ");
		System.out.println("");
		return this;
	}

	/**
	 * Checks user defined map for validation
	 * 
	 * @param entry
	 * @param exit
	 * @param height
	 * @param width
	 * @return
	 */
	public MapGenerator ManualGenerate(int entry, int exit, int height,
			int width) {
		StartingPoint = entry;
		EndPoint = exit;
		int cur_i = entry;
		int cur_j = 0;
		Grid[cur_i][cur_j].value = Status.isPath;

		// DFS
		while (!(cur_i == exit && cur_j == width - 1)) {
			if (!Deadend(cur_i, cur_j)) {
				if (MoveIsPossible(cur_i, cur_j, Movement.Right)) {
					cur_j++;
					Path.add(Movement.Right);
				} else if (MoveIsPossible(cur_i, cur_j, Movement.Up)) {
					cur_i--;
					Path.add(Movement.Up);
				} else if (MoveIsPossible(cur_i, cur_j, Movement.Down)) {
					cur_i++;
					Path.add(Movement.Down);
				} else if (MoveIsPossible(cur_i, cur_j, Movement.Left)) {
					cur_j--;
					Path.add(Movement.Left);
				}
				Grid[cur_i][cur_j].value = Status.isPath;
			} else if (!Path.isEmpty()) {
				// Go back a move
				Grid[cur_i][cur_j].value = Status.isBlock;
				Movement LastMove = Path.get(Path.size() - 1);
				Path.remove(Path.size() - 1);
				switch (LastMove) {
				case Up:
					// Go back down
					cur_i++;
					break;
				case Down:
					// Go back up
					cur_i--;
					break;
				case Left:
					// Go back right
					cur_j++;
					break;
				case Right:
					// Go back left
					cur_j--;
					break;
				default:
					break;
				}
			}
			if (Path.isEmpty() && Deadend(cur_i, cur_j)) {
				System.out
						.println("There isn't a path between entry point and end point!");
				return null;
			}
		}
		// Finalizing
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++)
				if (Grid[i][j].value == Status.notDecided)
					Grid[i][j].value = Status.isBlock;

		// Printing
		System.out.println("Map Created:");
		for (int i = 0; i < height; i++) {
			System.out.print("|");
			for (int j = 0; j < width; j++)
				if (Grid[i][j].value == Status.isPath)
					System.out.print("X|");
				else
					System.out.print(" |");
			System.out.println();
		}

		for (int i = 0; i < Path.size(); i++)
			System.out.print(Path.get(i) + " ");
		System.out.println("");
		return this;
	}

	public MapGenerator mapEditing(int entry, int exit, int height, int width) {
		StartingPoint = entry;
		EndPoint = exit;
		int cur_i = entry;
		int cur_j = 0;
		Grid[cur_i][cur_j].value = Status.isPath;

		// DFS
		while (!(cur_i == exit && cur_j == width - 1)) {
			if (!Deadend(cur_i, cur_j)) {
				if (MoveIsPossible(cur_i, cur_j, Movement.Right)) {
					cur_j++;
					this.Path.add(Movement.Right);
				} else if (MoveIsPossible(cur_i, cur_j, Movement.Up)) {
					cur_i--;
					this.Path.add(Movement.Up);
				} else if (MoveIsPossible(cur_i, cur_j, Movement.Down)) {
					cur_i++;
					this.Path.add(Movement.Down);
				} else if (MoveIsPossible(cur_i, cur_j, Movement.Left)) {
					cur_j--;
					this.Path.add(Movement.Left);
				}
				Grid[cur_i][cur_j].value = Status.isPath;
			} else if (!this.Path.isEmpty()) {
				// Go back a move
				Grid[cur_i][cur_j].value = Status.isBlock;
				Movement LastMove = this.Path.get(this.Path.size() - 1);
				this.Path.remove(this.Path.size() - 1);
				switch (LastMove) {
				case Up:
					// Go back down
					cur_i++;
					break;
				case Down:
					// Go back up
					cur_i--;
					break;
				case Left:
					// Go back right
					cur_j++;
					break;
				case Right:
					// Go back left
					cur_j--;
					break;
				default:
					break;
				}
			}
			if (this.Path.isEmpty() && Deadend(cur_i, cur_j)) {
				System.out
						.println("There isn't a path between entry point and end point!");
				return null;
			}
		}
		// Finalizing
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++)
				if (Grid[i][j].value == Status.notDecided)
					Grid[i][j].value = Status.isBlock;

		// Printing
		System.out.println("Map Created:");
		for (int i = 0; i < height; i++) {
			System.out.print("|");
			for (int j = 0; j < width; j++)
				if (Grid[i][j].value == Status.isPath)
					System.out.print("X|");
				else
					System.out.print(" |");
			System.out.println();
		}

		for (int i = 0; i < Path.size(); i++)
			System.out.print(Path.get(i) + " ");
		System.out.println("");
		return this;
	}

	public void setWidth(int newWidth) {
		// TODO Auto-generated method stub
		this.Width = newWidth;
	}

	public void setHeight(int newHeight) {
		// TODO Auto-generated method stub
		this.Height = newHeight;
	}

	public void setStartingPoint(int newStart) {
		// TODO Auto-generated method stub
		this.StartingPoint = newStart;
	}

	public void setEndPoint(int newEnd) {
		// TODO Auto-generated method stub
		this.EndPoint = newEnd;
	}

	public static List<Movement> getPath() {
		return Path;
	}

	public static void setPath(List<Movement> path) {
		Path = path;
	}
}
