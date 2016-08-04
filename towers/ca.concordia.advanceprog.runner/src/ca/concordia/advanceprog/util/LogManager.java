package ca.concordia.advanceprog.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;

/**
 * Creation of the log is being handle here, 4 types of log will be generated;
 * engine log, map log, wave log and tower log
 * user can select the type of log to display from a combo box
 */


public class LogManager extends Observable {
	static LogManager logManager;
	public static List<LogManager> Logs = new ArrayList<>();
	public logType logType;
	public String message = null;
	Date time;

	private LogManager() {

	}

	public static LogManager getLogInstance() {
		if (logManager != null)
			return logManager;
		logManager = new LogManager();
		return logManager;
	}

	public void logAdd(logType newLogType) {
		LogManager newLog = new LogManager();
		logType = newLogType;
		time = new Date();
		switch (logType) {
		case Engine: {
			message = "Engine changed at " + time.toString();
			System.out.print(message);
		}
			break;
		case Map: {
			message = "Map changed at " + time.toString();
			System.out.print(message);
		}
			break;
		case Tower: {
			message = "Tower changed at " + time.toString();
			System.out.print(message);
		}
			break;
		case Wave: {
			message = "Wave changed at " + time.toString();
			System.out.print(message);
		}
			break;
		default:
			break;
		}
		newLog.message = message;
		newLog.time = time;
		newLog.logType = newLogType;
		Logs.add(newLog);
		setChanged();
		notifyObservers();
	}

	public void logAdd(logType newLogType, String newMessage) {
		LogManager newLog = new LogManager();
		logType = newLogType;
		time = new Date();
		message = logType.toString() + " " + newMessage + " " + time.toString()
				+ "\n";
		System.out.print(message);

		newLog.message = message;
		newLog.time = time;
		newLog.logType = newLogType;
		Logs.add(newLog);
		setChanged();
		notifyObservers();
	}

	public void logAdd(String message) {
		time = new Date();
		LogManager newLog = new LogManager();
		if (message.startsWith("Tower"))
			newLog.logType = logType.Tower;
		if (message.startsWith("Engine"))
			newLog.logType = logType.Engine;
		if (message.startsWith("Map"))
			newLog.logType = logType.Map;
		if (message.startsWith("Wave"))
			newLog.logType = logType.Wave;
		newLog.message = message;
		newLog.time = time;
		Logs.add(newLog);
		setChanged();
		notifyObservers();
	}
}
