/**
 * All dependencies handles by Maven either for internal dependencies between engine and runner or for using 3rd party libraries
 * By using maven and it's versioning/parent|child relation we can achieve automatic building/ continuous integration
 * You can easily build project without eclipse by running "Maven Install" in your terminal where you find pom.xml  
 */
package ca.concordia.advanceprog.runner;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import ca.concordia.advanceprog.elements.Critter;
import ca.concordia.advanceprog.engine.ButtonHandler;
import ca.concordia.advanceprog.engine.Engine;
import ca.concordia.advanceprog.ui.DamageEffect;
import ca.concordia.advanceprog.ui.Map;
import ca.concordia.advanceprog.ui.MapGenerator;
import ca.concordia.advanceprog.ui.Movement;
import ca.concordia.advanceprog.ui.Status;
import ca.concordia.advanceprog.ui.Tower;
import ca.concordia.advanceprog.ui.TowerStrategy;
import ca.concordia.advanceprog.ui.TowerType;
import ca.concordia.advanceprog.util.LogManager;
import ca.concordia.advanceprog.util.logType;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * This is the entry class of the game which has main method that Java needs to
 * execute the game. it consist of buttons that are responsible for controlling
 * the game during execution. Although it gets the main panel instance from the
 * surface and depict the menu on top of that.
 * 
 * @author Shahriar Rostami
 *
 */
public class Runner extends Canvas implements Observer {
	public static List<Tower> TOWERS = new ArrayList<>();
	private volatile static boolean paused = false;

	private static Map map;
	public static Engine engine;
	private static JButton startPauseButton;
	private static Object lock = new Object();

	private static int gameSpeed = 10;
	private static MapGenerator mapGenerator;
	public static JLabel MoneyLabel;
	public static int X_DIMENSION = 15;
	public static int Y_DIMENSION = 15;

	private static boolean newGame = false;
	private static boolean newEmptyGame = false;
	public static boolean userEditing = false;
	private static boolean StillBuilding = false;
	private static boolean SelectedEntry = false; // 1st selection
	private static boolean SelectedExit = false; // 2nd selection
	public static int entry = 0, exit = 0;
	private static boolean createMap = true;
	private static boolean loadedMap = false;
	private static String loadedMapFile = null;

	public static JPanel Panel;
	public static JPanel logPanel;
	public static JFrame Frame;
	public static BufferStrategy StartStrategy;
	public static BufferStrategy Strategy;
	private static JButton newMapButton;
	private static JButton createUserMap;
	private static JButton loadMapButton;
	private static JButton saveMapButton;
	private static JButton acceptChanges;
	private JButton Strategy1 = new JButton("Closest");
	private JButton Strategy2 = new JButton("Farthest");
	private JButton Strategy3 = new JButton("Sickest");
	private JButton Strategy4 = new JButton("Healthiest");
	private JButton strategyShow = new JButton();
	private JButton typeShow = new JButton();
	private JButton currentTowerStatusButton = new JButton();
	private JButton currentTowerAmount = new JButton();
	private JButton sellButton = new JButton();

	JButton regularTypeButton = new JButton("Regular");
	JButton massTypeButton = new JButton("Mass Shooting");
	JButton riffleTypeButton = new JButton("Riffle Shooting");

	private static JComboBox comboBox = null;
	private static JComboBox towerBox = null;
	private static JTextArea textArea;
	static String[] towerBoxText = new String[TOWERS.size()];

	/**
	 * main method of the program it has not yet finished because we are going
	 * to obtain game dimension from String[] args
	 * 
	 * @param args
	 *            is intended to get custom argument from user when running the
	 *            program
	 */
	public static void main(String[] args) {
	java.awt.EventQueue.invokeLater(new Runnable() {
	@Override
	public void run() {
	new Runner();
	}
	});
	}

	/**
	 * This is the constructor of our runner class and in this method we create
	 * an instance of Game Engine
	 */
	public Runner() {
	createNewGame();
	createMainSurface();
	engine.setStrategy(Strategy);
	createButtons();
	createLogPanel();

	startPauseButton.addActionListener(pauseResume);
	handleMouseActions();
	counter.start();
	}

	/**
	 * This method is responsible for creating new instance of game
	 */
	private void createNewGame() {
	LogManager.getLogInstance().addObserver(this);
	String levelToPlay = JOptionPane.showInputDialog(Panel,
	"The level you want play:", "1");
	Engine.gameLevelandNumberofCritters = Integer.parseInt(levelToPlay);
	if (createMap) {
	createMap();
	map.generateMap();
	createMap = true;
	}
	engineInitializingObserving();
	if (MoneyLabel != null)
	engine.resetMoney();
	}

	/**
	 * Create a new empty map based on the user request for create new user
	 * defined map
	 */
	private void createEmptyMap() {
	Engine.IS_CREATE_CRITTER = false;
	map = new Map(X_DIMENSION, Y_DIMENSION);
	for (int i = 0; i < X_DIMENSION; i++)
	for (int j = 0; j < Y_DIMENSION; j++) {
	map.Data[i][j] = Map.BLOCKED;
	}
	engineInitializingObserving();
	}

	/**
	 * just private method that is called by createNewGame()
	 */
	private static void createMap() {
	map = new Map(X_DIMENSION, Y_DIMENSION);
	}

	/**
	 * Add observers to the engine class which in this case is Runner itself
	 */
	private void engineInitializingObserving() {
	engine = Engine.initialEngine(map, true);
	engine.addObserver(this);
	engine.setStrategy(Strategy);
	}

	/**
	 * This method intends to update the text of moneyLabel to show the
	 * remaining amount of money player has.
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
	if (arg0 instanceof Engine)
	MoneyLabel.setText("Balance: " + Integer.toString(engine.MONEY)
	+ "$");
	if (arg0 instanceof LogManager) {
	if (comboBox.getSelectedItem().toString().equals("All"))
	textArea.append(LogManager.getLogInstance().Logs.get(LogManager
	.getLogInstance().Logs.size() - 1).message);
	else if (comboBox.getSelectedItem().toString().equals("Tower")) {

	if (towerBox.getSelectedItem().toString().equals("All")) {
	if (LogManager.getLogInstance().Logs.get(LogManager
	.getLogInstance().Logs.size() - 1).logType
	.toString().equals("Tower"))
	textArea.append(LogManager.getLogInstance().Logs
	.get(LogManager.getLogInstance().Logs.size() - 1).message);
	} else {
	String str = towerBox.getSelectedItem().toString();
	str = str.substring(str.indexOf("("), str.indexOf(")"));
	if (LogManager.getLogInstance().Logs.get(LogManager
	.getLogInstance().Logs.size() - 1).logType
	.toString().equals("Tower")) {
	if (str.equals("All")) {
	textArea.append(LogManager.getLogInstance().Logs
	.get(LogManager.getLogInstance().Logs
	.size() - 1).message);
	} else if (LogManager.getLogInstance().Logs
	.get(LogManager.getLogInstance().Logs.size() - 1).message
	.contains(str))
	textArea.append(LogManager.getLogInstance().Logs
	.get(LogManager.getLogInstance().Logs
	.size() - 1).message);

	}
	}
	} else if (LogManager.getLogInstance().Logs.get(LogManager
	.getLogInstance().Logs.size() - 1).logType.toString()
	.equals(comboBox.getSelectedItem()))
	textArea.append(LogManager.getLogInstance().Logs.get(LogManager
	.getLogInstance().Logs.size() - 1).message);

	}
	}

	/**
	 * This method is responsible for painting the game environment
	 */
	private void createMainSurface() {
	int WIDTH = map.Width * (Runner.X_DIMENSION + 20);
	int HEIGHT = map.Height * (Runner.Y_DIMENSION + 40);

	Frame = new JFrame("Tower Game");

	Frame.setLayout(new BorderLayout());

	Panel = new JPanel();
	Panel.setPreferredSize(new Dimension(20, 50));
	Panel.setBackground(Color.black);
	setBackground(Color.black);
	Frame.add(Panel, BorderLayout.NORTH);

	logPanel = new JPanel();

	logPanel.setPreferredSize(new Dimension(180, 250));
	logPanel.setBorder(new TitledBorder(new EtchedBorder(), "Log Area"));
	logPanel.setBackground(Color.GRAY);
	Frame.add(logPanel, BorderLayout.SOUTH);

	setBounds(-100, 0, 900, 500);
	Frame.add(this, BorderLayout.CENTER);
	Frame.setSize(WIDTH, HEIGHT);
	Frame.setResizable(false);
	Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	Frame.setLocationRelativeTo(null);
	Frame.setVisible(true);
	addKeyListener(new ButtonHandler());
	requestFocus();

	createBufferStrategy(2);
	StartStrategy = getBufferStrategy();
	Strategy = getBufferStrategy();

	// handleMouseActions(Frame);
	}

	@SuppressWarnings("unchecked")
	public static void createLogPanel() {
	String[] comboText = { "All", "Tower", "Engine", "Wave", "Map" };
	// textArea.setSize(400, 400);
	comboBox = new JComboBox(comboText);
	towerBox = new JComboBox();
	towerBox.setVisible(false);
	towerBox.addItem("All");

	textArea = new JTextArea(10, 40);
	// textArea.setLineWrap(true);
	textArea.setEditable(false);
	textArea.setVisible(true);

	JScrollPane scroll = new JScrollPane(textArea);
	scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	// scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

	/**
	 * We place combo box to select which log to be dispalyed
	 */
	
	logPanel.add(comboBox);
	logPanel.add(towerBox);
	logPanel.add(scroll);

	comboBox.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent event) {
	//
	// Get the source of the component, which is our combo
	// box.
	//
	comboBox = (JComboBox) event.getSource();

	Object selected = comboBox.getSelectedItem();

	if (selected.toString().equals("All")) {
	textArea.setText("");
	towerBox.setVisible(false);
	for (int i = 0; i < LogManager.getLogInstance().Logs.size(); i++) {
	textArea.append(LogManager.getLogInstance().Logs.get(i).message);
	}

	} else if (selected.toString().equals("Tower")) {
	textArea.setText("");
	towerBox.setVisible(true);
	towerBox.setSelectedItem("All");
	// for (int i = 0; i <
	// LogManager.getLogInstance().Logs.size(); i++)
	// if (LogManager.getLogInstance().Logs.get(i).logType
	// .toString().equals(selected.toString())) {
	// textArea.append(LogManager.getLogInstance().Logs
	// .get(i).message);
	// }
	} else {
	textArea.setText("");
	towerBox.setVisible(false);
	for (int i = 0; i < LogManager.getLogInstance().Logs.size(); i++)
	if (LogManager.getLogInstance().Logs.get(i).logType
	.toString().equals(selected.toString())) {
	textArea.append(LogManager.getLogInstance().Logs
	.get(i).message);
	}
	}
	}
	});

	towerBox.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	if (comboBox.getSelectedItem().toString().equals("Tower")) {
	towerBox = (JComboBox) e.getSource();
	Object selectedItem = towerBox.getSelectedItem();
	if (selectedItem == null)
	selectedItem = "All";
	if (selectedItem.toString().equals("All")) {
	for (int i = 0; i < LogManager.getLogInstance().Logs
	.size(); i++)
	if (LogManager.getLogInstance().Logs.get(i).logType
	.toString().equals("Tower")) {
	textArea.append(LogManager.getLogInstance().Logs
	.get(i).message);
	}
	} else {
	for (int i = 0; i < TOWERS.size(); i++) {
	String str = towerBoxText[i];
	str = str.substring(str.indexOf("("),
	str.indexOf(")"));
	if (selectedItem.toString().contains(str)) {
	textArea.setText("");
	for (int j = 0; j < LogManager.getLogInstance().Logs
	.size(); j++) {
	if (LogManager.getLogInstance().Logs.get(j).message
	.contains(str)
	&& LogManager.getLogInstance().Logs
	.get(j).logType.toString()
	.equals("Tower"))
	textArea.append(LogManager
	.getLogInstance().Logs.get(j).message);
	}
	}
	}
	}
	}
	}
	});
	}

	/**
	 * This methods is responsible for creating buttons and put them inside the
	 * panel that we receive from engine which is received from surface in a
	 * sequence of gets.
	 */
	public static void createButtons() {
	startPauseButton = new JButton("Pause");
	newMapButton = new JButton("New Game");
	createUserMap = new JButton("Create User Map");
	loadMapButton = new JButton("Load Map");
	saveMapButton = new JButton("Save Map");
	acceptChanges = new JButton("Accept MAp");
	MoneyLabel = new JLabel("Balance: " + Integer.toString(engine.MONEY)
	+ "$", SwingConstants.RIGHT);
	MoneyLabel.setForeground(Color.white);

	Panel.add(startPauseButton);
	Panel.add(newMapButton);
	Panel.add(createUserMap);
	Panel.add(loadMapButton);
	Panel.add(saveMapButton);
	Panel.add(MoneyLabel, BorderLayout.WEST);

	Panel.add(acceptChanges);
	acceptChanges.setVisible(false);

	newMapButton.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	newGame = true;
	}
	});

	saveMapButton.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	JFileChooser chooser = new JFileChooser();
	// chooser.setCurrentDirectory(new java.io.File("."));
	chooser.setDialogTitle("select folder");
	chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	chooser.setAcceptAllFileFilterUsed(false);

	int returnVal = chooser.showSaveDialog(Panel);
	if (returnVal == JFileChooser.APPROVE_OPTION) {
	String str = chooser.getSelectedFile().getName();
	if (str.endsWith("mp")) {
	try {
	engine.MAP.MapGenerator.SaveMap(str);

	LogManager.getLogInstance().logAdd(logType.Map,
	"Map saved to file:(" + str + ") ");

	File f = new File(str);
	FileWriter writer = new FileWriter(f, true);
	writer.write(LogManager.getLogInstance().Logs
	.get(LogManager.getLogInstance().Logs
	.size() - 1).message
	+ System.getProperty("line.separator"));
	System.out.print(LogManager.getLogInstance().Logs
	.get(LogManager.getLogInstance().Logs
	.size() - 1).message
	+ System.getProperty("line.separator"));
	try {
	writer.flush();
	} catch (IOException e1) {
	// TODO Auto-generated catch block
	e1.printStackTrace();
	}
	} catch (IOException e1) {
	e1.printStackTrace();
	}
	} else {
	FileOutputStream fout;
	try {
	fout = new FileOutputStream(str);
	ObjectOutputStream oos = new ObjectOutputStream(
	fout);
	oos.writeObject(engine);
	oos.flush();
	} catch (FileNotFoundException e3) {
	// TODO Auto-generated catch block
	e3.printStackTrace();
	} catch (IOException e1) {
	// TODO Auto-generated catch block
	e1.printStackTrace();
	}
	}
	}
	}
	});

	loadMapButton.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {

	JFileChooser chooser = new JFileChooser();
	FileNameExtensionFilter filter = new FileNameExtensionFilter(
	"mp Maps", "mp");
	chooser.setFileFilter(filter);
	int returnVal = chooser.showOpenDialog(Panel);
	if (returnVal == JFileChooser.APPROVE_OPTION) {
	System.out.println("Loading...");
	String str = chooser.getSelectedFile().getName();
	if (str.endsWith("mp")) {
	try {
	userEditing = true;
	acceptChanges.setVisible(true);
	loadedMap = true;
	loadedMapFile = str;
	int height, width;
	BufferedReader br = new BufferedReader(
	new FileReader(str));
	try {
	String line = br.readLine();
	height = Integer.parseInt(line);
	// mapGenerator.setHeight(Integer.parseInt(line));
	line = br.readLine();
	width = Integer.parseInt(line);
	// mapGenerator.setWidth
	// (Integer.parseInt(line));
	mapGenerator = new MapGenerator(height, width);
	SelectedEntry = true;
	SelectedExit = true;
	line = br.readLine();
	int newStart = Integer.parseInt(line);
	mapGenerator.setStartingPoint(newStart);
	entry = newStart;
	Runner.X_DIMENSION = height;
	Runner.Y_DIMENSION = width;
	line = br.readLine();
	int newEnd = Integer.parseInt(line);
	mapGenerator.setEndPoint(newEnd);
	exit = newEnd;
	line = br.readLine();
	boolean inWhile1 = true;
	while (line != null && inWhile1) {
	if (!line.equals(MapGenerator.EndOfPath)) {
	switch (line) {
	case "Up":
	mapGenerator.getPath().add(
	Movement.Up);
	break;
	case "Down":
	mapGenerator.getPath().add(
	Movement.Down);
	break;
	case "Right":
	mapGenerator.getPath().add(
	Movement.Right);
	break;
	case "Left":
	mapGenerator.getPath().add(
	Movement.Left);
	break;
	default:
	break;
	}
	line = br.readLine();
	} else if (line
	.equals(MapGenerator.EndOfPath))
	inWhile1 = false;
	}
	line = br.readLine();
	while (line != null) {
	LogManager.getLogInstance().logAdd(
	line + "\n");
	line = br.readLine();
	}

	} finally {
	br.close();
	}
	int curI = mapGenerator.getStartinPoint(), curJ = 0;
	mapGenerator.Grid[curI][curJ].value = Status.isPath;
	for (int i = 0; i < mapGenerator.getPath().size(); i++) {
	Movement dir = mapGenerator.getPath().get(i);
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
	if (curI < mapGenerator.getHeight()
	&& curI >= 0
	&& curJ < mapGenerator.getWidth()
	&& curJ >= 0)
	mapGenerator.Grid[curI][curJ].value = Status.isPath;
	}
	for (int i = 0; i < mapGenerator.getHeight(); i++)
	for (int j = 0; j < mapGenerator.getWidth(); j++)
	if (mapGenerator.Grid[i][j].value != Status.isPath)
	mapGenerator.Grid[i][j].value = Status.isBlock;

	// Printing
	System.out.println("Map Loaded:");
	for (int i = 0; i < mapGenerator.getHeight(); i++) {
	System.out.print("|");
	for (int j = 0; j < mapGenerator.getWidth(); j++)
	if (MapGenerator.Grid[i][j].value == Status.isPath)
	System.out.print("X|");
	else
	System.out.print(" |");
	System.out.println();
	}

	for (int i = 0; i < mapGenerator.getPath().size(); i++)
	System.out.print(mapGenerator.getPath().get(i)
	+ " ");
	System.out.println("");
	// mapGenerator.LoadMap(str);
	map.setMapGenerator(mapGenerator);
	// Engine.MAP_VALIDATION = new MapValidation(map);

	engine.critters = null;
	StillBuilding = true;
	// Window w = SwingUtilities
	// .getWindowAncestor(engine.Panel);
	// w.setVisible(false);

	// new Runner();

	LogManager.getLogInstance().logAdd(logType.Map,
	"Map loaded: (" + str + ")");
	} catch (IOException e1) {
	e1.printStackTrace();
	}
	} else {

	InputStream file;
	try {
	file = new FileInputStream(str);
	InputStream buffer = new BufferedInputStream(file);
	ObjectInput input = new ObjectInputStream(buffer);
	engine = (Engine) input.readObject();
	engine.setStrategy(StartStrategy);
	} catch (FileNotFoundException e1) {
	e1.printStackTrace();
	} catch (IOException e1) {
	e1.printStackTrace();
	} catch (ClassNotFoundException e1) {
	e1.printStackTrace();
	}
	}
	}
	}
	});

	/**
	 * Creation of map as user enters the dimensions of the map and the path
	 */
	
	createUserMap.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	String X_Y = JOptionPane.showInputDialog(Panel,
	"Put in your dimension:", "15,15");
	newEmptyGame = true;
	String[] parts = X_Y.split(",");
	int x = Integer.parseInt(parts[1]);
	int y = Integer.parseInt(parts[0]);
	// TODO Auto-generated method stub
	X_DIMENSION = x;
	Y_DIMENSION = y;
	userEditing = true;
	acceptChanges.setVisible(true);
	StillBuilding = true;
	SelectedEntry = false; // 1st selection
	SelectedExit = false; // 2nd selection
	}
	});

	acceptChanges.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent arg0) {
	// TODO Auto-generated method stub
	if (SelectedEntry && SelectedExit) {
	if (map.UserDefinedMapValidation(entry, exit, map.Height,
	map.Width)) {
	// show appropriate message
	userEditing = false;
	StillBuilding = false;
	acceptChanges.setVisible(false);
	LogManager.getLogInstance().logAdd(logType.Map,
	"Map creation finished");
	if (loadedMap) {
	loadedMap = false;
	File f = new File(loadedMapFile);
	FileWriter writer = null;
	try {
	writer = new FileWriter(f, true);
	} catch (IOException e1) {
	// TODO Auto-generated catch block
	e1.printStackTrace();
	}
	try {
	writer.write(LogManager.getLogInstance().Logs
	.get(LogManager.getLogInstance().Logs
	.size() - 1).message
	+ System.getProperty("line.separator"));
	System.out.print(LogManager.getLogInstance().Logs
	.get(LogManager.getLogInstance().Logs
	.size() - 1).message
	+ System.getProperty("line.separator"));
	} catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
	}
	try {
	writer.flush();
	} catch (IOException e1) {
	// TODO Auto-generated catch block
	e1.printStackTrace();
	}
	}
	Engine.IS_CREATE_CRITTER = true;
	} else {
	StillBuilding = true;
	}
	}
	}
	});
	}

	/**
	 * The counter variable is assigned using anonymous method which overrides
	 * run method from Thread class
	 */
	private Thread counter = new Thread(new Runnable() {
	@Override
	public void run() {
	while (true) {
	work();
	}
	}
	});

	/**
	 * Work() intends to handle ongoing play from the beginning of game to the
	 * end of the game
	 */
	private void work() {
	allowPause();
	if (newGame) {
	createNewGame();
	textArea.setText("");
	newGame = false;
	}
	if (newEmptyGame) {
	createEmptyMap();
	textArea.setText("");
	newEmptyGame = false;
	}
	engine.playing();
	sleep();
	}

	/**
	 * allowPause is called in short time boxes just to check if the
	 * Pause/Resume button pressed or not
	 */
	private void allowPause() {
	synchronized (lock) {
	while (paused) {
	try {
	lock.wait();
	} catch (InterruptedException e) {
	}
	}
	}
	}

	/**
	 * Is called when pause / resume action performed.
	 */
	private ActionListener pauseResume = new ActionListener() {
	@Override
	public void actionPerformed(java.awt.event.ActionEvent e) {
	// gameSpeed = (int) (gameSpeed > 0 ? gameSpeed - 0.5 : gameSpeed);
	paused = !paused;
	startPauseButton.setText(paused ? "Resume" : "Pause");
	synchronized (lock) {
	lock.notifyAll();
	}
	}
	};

	/**
	 * This method handles sleeps during execution of thread
	 */
	private void sleep() {
	try {
	Thread.sleep(gameSpeed);
	} catch (InterruptedException e) {

	}
	}

	/*
	 * An unused method to be called when the game is finished (Game Over or
	 * Victory)
	 */
	private void done() {
	startPauseButton.setText("Start");
	paused = true;
	}

	/**
	 * This method is responsible to react to user request based on mouse clicks
	 * such as toggling between Tower and Scenary(path)
	 */
	private void handleMouseActions() {
	addMouseListener(new MouseAdapter() {
	private Color background;

	@Override
	public void mousePressed(MouseEvent e) {
	if (userEditing) {
	String clickedPoint = Map.tryMapXYtoCellNumber(
	e.getX() - 100, e.getY() - 100);
	String[] parts = clickedPoint.split(",");
	int x, y;

	try {
	x = Integer.parseInt(parts[1]);
	y = Integer.parseInt(parts[0]);
	} catch (Exception e2) {
	return;
	}

	if (StillBuilding && !SelectedEntry && !SelectedExit) {
	// select entry point;
	if (y == 0) {
	entry = x;
	map.Data[x][y] = Map.CLEAR;
	SelectedEntry = true;
	LogManager.getLogInstance().logAdd(
	logType.Map,
	"Map starting point: (" + x + "," + y
	+ ") ");
	} else {
	// show message to select from the first column
	System.out.println("Select from the 1st column!");
	}
	} else if (StillBuilding && SelectedEntry && !SelectedExit) {
	// select exit point;
	if (y == map.Width - 1) {
	exit = x;
	map.Data[x][y] = Map.CLEAR;
	SelectedExit = true;
	LogManager.getLogInstance().logAdd(logType.Map,
	"Map exit point: (" + x + "," + y + ") ");
	} else {
	// show message to select from the last column
	System.out.println("Select from the last column!");
	}
	} else if (StillBuilding && SelectedEntry && SelectedExit) {
	if (map.Data[x][y] == Map.BLOCKED) {
	map.Data[x][y] = Map.CLEAR;

	LogManager.getLogInstance()
	.logAdd(logType.Map,
	"Path point put on: (" + x + ","
	+ y + ") ");

	if (loadedMap) {
	File f = new File(loadedMapFile);
	FileWriter writer = null;
	try {
	writer = new FileWriter(f, true);
	} catch (IOException e2) {
	// TODO Auto-generated catch block
	e2.printStackTrace();
	}
	try {
	writer.write(LogManager.getLogInstance().Logs
	.get(LogManager.getLogInstance().Logs
	.size() - 1).message
	+ System.getProperty("line.separator"));
	System.out
	.print(LogManager.getLogInstance().Logs.get(LogManager
	.getLogInstance().Logs
	.size() - 1).message
	+ System.getProperty("line.separator"));
	} catch (IOException e1) {

	// TODO Auto-generated catch block
	e1.printStackTrace();
	}
	try {
	writer.flush();
	} catch (IOException e1) {
	// TODO Auto-generated catch block
	e1.printStackTrace();
	}
	}
	} else {
	if (x != entry || x != exit) {
	map.Data[x][y] = Map.BLOCKED;
	LogManager.getLogInstance().logAdd(logType.Map,
	"Block put on: (" + x + "," + y + ") ");
	if (loadedMap) {
	File f = new File(loadedMapFile);
	FileWriter writer = null;
	try {
	writer = new FileWriter(f, true);
	} catch (IOException e2) {
	// TODO Auto-generated catch block
	e2.printStackTrace();
	}
	try {
	writer.write(LogManager
	.getLogInstance().Logs
	.get(LogManager
	.getLogInstance().Logs
	.size() - 1).message
	+ System.getProperty("line.separator"));
	System.out
	.print(LogManager
	.getLogInstance().Logs.get(LogManager
	.getLogInstance().Logs
	.size() - 1).message
	+ System.getProperty("line.separator"));
	} catch (IOException e1) {
	// TODO Auto-generated catch block
	e1.printStackTrace();
	}
	try {
	writer.flush();
	} catch (IOException e1) {
	// TODO Auto-generated catch block
	e1.printStackTrace();
	}
	}
	}
	// else if (x == entry && y == 0) {
	// SelectedEntry = false;
	// System.out.println("Select new entry point");
	// } else if (x == exit && y == map.Width - 1) {
	// SelectedExit = false;
	// System.out.println("Select new exit point");
	// }
	}
	}

	} else {
	String clickedPoint = Map.tryMapXYtoCellNumber(
	e.getX() - 100, e.getY() - 100);
	String[] parts = clickedPoint.split(",");
	int x = Integer.parseInt(parts[1]);
	int y = Integer.parseInt(parts[0]);
	if (map.Data[x][y] == Map.BLOCKED) {
	if (engine.checkBalance()) {

	final JFrame parent = new JFrame();

	JButton Splash = new JButton("Splash");
	JButton Freeze = new JButton("Freeze");
	JButton Burning = new JButton("Burning");

	JPanel damageEffect = new JPanel();
	currentTowerAmount.setEnabled(false);

	strategyShow.setEnabled(false);

	damageEffect.add(Splash);
	damageEffect.add(Freeze);
	damageEffect.add(Burning);
	parent.add(damageEffect, BorderLayout.WEST);
	parent.getContentPane().setLayout(
	new GridLayout(1, 3));

	parent.pack();
	parent.setLocationRelativeTo(null);
	parent.setVisible(true);

	parent.repaint();
	final Tower newTower = new Tower(x, y, 1, 100, 200);
	
	/**
	 * Splash kind of firing by the tower is generated
	 */

	Splash.addActionListener(new java.awt.event.ActionListener() {
	@Override
	public void actionPerformed(
	java.awt.event.ActionEvent evt) {
	newTower.damageEffect = DamageEffect.Splash;
	parent.setVisible(false);
	LogManager.getLogInstance().logAdd(
	logType.Tower,
	"Tower effect "
	+ newTower.damageEffect
	.toString()
	+ " placed on: ("
	+ newTower.x + ","
	+ newTower.y + ") ");
	LogManager.getLogInstance().logAdd(
	logType.Engine,
	"Money withdrawn " + "(" + 100
	+ ") ");
	}
	});

	/**
	 * Freeze kind of firing by the tower is generated
	 */
	
	Freeze.addActionListener(new java.awt.event.ActionListener() {
	@Override
	public void actionPerformed(
	java.awt.event.ActionEvent evt) {
	newTower.damageEffect = DamageEffect.Freezing;
	parent.setVisible(false);
	LogManager.getLogInstance().logAdd(
	logType.Tower,
	"Tower effect "
	+ newTower.damageEffect
	.toString()
	+ " placed on: ("
	+ newTower.x + ","
	+ newTower.y + ") ");
	LogManager.getLogInstance().logAdd(
	logType.Engine,
	"Money withdrawn " + "(" + 100
	+ ") ");
	}
	});

	/**
	 * Burning kind of firing by the tower is generated
	 */
	
	Burning.addActionListener(new java.awt.event.ActionListener() {
	@Override
	public void actionPerformed(
	java.awt.event.ActionEvent evt) {
	newTower.damageEffect = DamageEffect.Burning;
	parent.setVisible(false);
	LogManager.getLogInstance().logAdd(
	logType.Tower,
	"Tower effect "
	+ newTower.damageEffect
	.toString()
	+ " placed on: ("
	+ newTower.x + ","
	+ newTower.y + ") ");
	LogManager.getLogInstance().logAdd(
	logType.Engine,
	"Money withdrawn " + "(" + 100
	+ ") ");
	}
	});

	/**
	 * Tower money and gameplay handled
	 */
	
	TOWERS.add(newTower);

	individualTowerComboListMaker();

	for (Critter critter : engine.critters) {
	critter.addObserver(newTower);
	}
	map.Data[x][y] = Map.TOWER;
	engine.withDraw(100);

	} else
	JOptionPane.showMessageDialog(null,
	"No More Money!");
	} else if (map.Data[x][y] == Map.TOWER) {
	if (e.isAltDown()) {
	showCharacteristics(x, y);
	} else {

	int amountToDeposit = GetTower(x, y).CurrentAmount / 2;
	map.Data[x][y] = Map.BLOCKED;
	engine.deposit(amountToDeposit);
	LogManager.getLogInstance().logAdd(logType.Tower,
	"Tower removed at: (" + x + "," + y + ") ");
	LogManager.getLogInstance().logAdd(
	logType.Engine,
	"Money deposited: (" + amountToDeposit
	+ ") ");
	Iterator<Tower> i = TOWERS.iterator();
	while (i.hasNext()) {
	Tower tower = i.next();
	if (tower.x == x && tower.y == y)
	i.remove();
	}
	}
	}
	}
	}
	
	/**
	 * Tower strategy to be selected when user places a tower on the map
	 */

	private void showCharacteristics(int x, int y) {
	final Tower tower = GetTower(x, y);
	final JFrame parent = new JFrame();

	final JPanel informationPanel = new JPanel();
	final JPanel strategyPanel = new JPanel();
	final JPanel actionPanel = new JPanel();
	final JPanel shootingPanel = new JPanel();

	currentTowerAmount.setEnabled(false);

	strategyShow.setEnabled(false);
	typeShow.setEnabled(false);

	informationPanel.add(typeShow);

	informationPanel.add(currentTowerAmount);

	parent.add(informationPanel, BorderLayout.NORTH);

	actionPanel.add(currentTowerStatusButton);
	actionPanel.add(sellButton);
	parent.add(actionPanel, BorderLayout.CENTER);

	strategyPanel.add(Strategy1);
	strategyPanel.add(Strategy2);
	strategyPanel.add(Strategy3);
	strategyPanel.add(Strategy4);
	strategyPanel.add(strategyShow);
	parent.add(strategyPanel, BorderLayout.SOUTH);

	shootingPanel.add(regularTypeButton);
	shootingPanel.add(massTypeButton);
	shootingPanel.add(riffleTypeButton);
	parent.add(shootingPanel, BorderLayout.WEST);

	updateValues(tower);

	parent.getContentPane().setLayout(new GridLayout(3, 3));

	parent.pack();
	parent.setLocationRelativeTo(null);
	parent.setVisible(true);

	parent.repaint();

	currentTowerStatusButton
	.addActionListener(new java.awt.event.ActionListener() {
	@Override
	public void actionPerformed(
	java.awt.event.ActionEvent evt) {
	if (tower.improveLevel()) {
	engine.withDraw(50);

	LogManager.getLogInstance().logAdd(
	logType.Tower,
	"Tower at (" + tower.x + ","
	+ tower.y
	+ ") improved to level: ("
	+ tower.Level + ") ");
	LogManager.getLogInstance().logAdd(
	logType.Engine,
	"Money withdrawn: (" + 50 + ") ");

	updateValues(tower);
	}
	}
	});

	sellButton
	.addActionListener(new java.awt.event.ActionListener() {
	@Override
	public void actionPerformed(
	java.awt.event.ActionEvent evt) {
	map.Data[tower.x][tower.y] = Map.BLOCKED;

	engine.deposit(tower.CurrentAmount / 2);
	LogManager.getLogInstance().logAdd(
	logType.Tower,
	"Tower removed at: (" + tower.x + ","
	+ tower.y + ") ");
	LogManager.getLogInstance().logAdd(
	logType.Engine,
	"Money deposited: ("
	+ tower.CurrentAmount / 2
	+ ") ");
	parent.setVisible(false);
	}
	});
	Strategy1
	.addActionListener(new java.awt.event.ActionListener() {
	@Override
	public void actionPerformed(
	java.awt.event.ActionEvent evt) {
	tower.setStrategy(TowerStrategy.Closest);
	updateValues(tower);
	LogManager.getLogInstance().logAdd(
	logType.Tower,
	"Tower at: ("
	+ tower.x
	+ ","
	+ tower.y
	+ ") strategy changed to "
	+ tower.getStrategy()
	.toString());

	}
	});
	Strategy2
	.addActionListener(new java.awt.event.ActionListener() {
	@Override
	public void actionPerformed(
	java.awt.event.ActionEvent evt) {
	tower.setStrategy(TowerStrategy.Farthest);
	updateValues(tower);
	LogManager.getLogInstance().logAdd(
	logType.Tower,
	"Tower at: ("
	+ tower.x
	+ ","
	+ tower.y
	+ ") strategy changed to "
	+ tower.getStrategy()
	.toString());

	}
	});
	Strategy3
	.addActionListener(new java.awt.event.ActionListener() {
	@Override
	public void actionPerformed(
	java.awt.event.ActionEvent evt) {
	tower.setStrategy(TowerStrategy.Sickest);
	updateValues(tower);
	LogManager.getLogInstance().logAdd(
	logType.Tower,
	"Tower at: ("
	+ tower.x
	+ ","
	+ tower.y
	+ ") strategy changed to "
	+ tower.getStrategy()
	.toString());

	}
	});
	Strategy4
	.addActionListener(new java.awt.event.ActionListener() {
	@Override
	public void actionPerformed(
	java.awt.event.ActionEvent evt) {
	tower.setStrategy(TowerStrategy.Healthiest);
	updateValues(tower);
	LogManager.getLogInstance().logAdd(
	logType.Tower,
	"Tower at: ("
	+ tower.x
	+ ","
	+ tower.y
	+ ") strategy changed to "
	+ tower.getStrategy()
	.toString());

	}
	});

	regularTypeButton
	.addActionListener(new java.awt.event.ActionListener() {
	@Override
	public void actionPerformed(
	java.awt.event.ActionEvent evt) {
	tower.Type = TowerType.Regular;
	LogManager.getLogInstance().logAdd(
	logType.Tower,
	"Tower type " + tower.Type.toString()
	+ " placed on: (" + tower.x
	+ "," + tower.y + ") ");
	parent.setVisible(false);
	}
	});

	massTypeButton
	.addActionListener(new java.awt.event.ActionListener() {
	@Override
	public void actionPerformed(
	java.awt.event.ActionEvent evt) {
	tower.Type = TowerType.Mass;
	engine.withDraw(200);
	LogManager.getLogInstance().logAdd(
	logType.Tower,
	"Tower type " + tower.Type.toString()
	+ " placed on: (" + tower.x
	+ "," + tower.y + ") ");
	LogManager.getLogInstance().logAdd(
	logType.Engine,
	"Money withdrawn " + "(" + 200 + ") ");
	parent.setVisible(false);
	}
	});

	riffleTypeButton
	.addActionListener(new java.awt.event.ActionListener() {
	@Override
	public void actionPerformed(
	java.awt.event.ActionEvent evt) {
	tower.Type = TowerType.Riffle;
	engine.withDraw(100);
	LogManager.getLogInstance().logAdd(
	logType.Tower,
	"Tower type " + tower.Type.toString()
	+ " placed on: (" + tower.x
	+ "," + tower.y + ") ");

	LogManager.getLogInstance().logAdd(
	logType.Engine,
	"Money withdrawn " + "(" + 100 + ") ");
	parent.setVisible(false);
	}
	});
	}

	private void updateValues(Tower tower) {
	currentTowerStatusButton
	.setText("Increase the power of tower with 50$ (current is: ["
	+ tower.Level + "])");

	sellButton.setText("Sell this tower ($"
	+ (tower.CurrentAmount / 2) + ")");

	currentTowerAmount
	.setText("Amount you paid for this tower so far is: "
	+ tower.CurrentAmount);

	// if (tower.Level < 5)
	// currentShooting.setText("Current Shooting rate is:  "
	// + tower.Shooting + "  Update to  " + (tower.Shooting
	// +50));
	// else
	// currentShooting.setText("Current Shoothin rate is: "
	// + tower.Shooting + " Cannot Update.");

	strategyShow.setText("Strategy: "
	+ tower.getStrategy().toString());
	typeShow.setText("Type of Tower: " + tower.Type.toString());

	}

	@Override
	public void mouseReleased(MouseEvent e) {
	setBackground(background);
	}
	});
	}

	private void individualTowerComboListMaker() {
	towerBoxText = new String[TOWERS.size()];
	for (int i = 0; i < TOWERS.size(); i++) {
	String tmp = "(" + TOWERS.get(i).x + "," + TOWERS.get(i).y + ")";
	towerBoxText[i] = tmp;
	}
	towerBox.removeAllItems();
	towerBox.addItem("All");
	for (int i = 0; i < TOWERS.size(); i++)
	towerBox.addItem("Tower at " + towerBoxText[i]);
	}

	public static Tower GetTower(int x, int y) {
	Iterator<Tower> i = TOWERS.iterator();
	while (i.hasNext()) {
	Tower tower = i.next();
	if (tower.x == x && tower.y == y)
	return tower;
	}
	return null;
	}

}