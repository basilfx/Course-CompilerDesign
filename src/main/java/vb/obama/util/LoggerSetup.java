package vb.obama.util;

import java.io.OutputStreamWriter;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * Helper class to setup loggin in various places of this project.
 * @version 1.0
 */
public class LoggerSetup {
	/**
	 * In tests, the constructor will reinitialize every time. This field
	 * prevents double initialization of the logger
	 */
	private static boolean initialized = false;
	
	/**
	 * Setup a default logger to console.
	 */
	public static void setup() {
		ConsoleAppender consoleAppender = new ConsoleAppender();
		consoleAppender.setWriter(new OutputStreamWriter(System.out));
		consoleAppender.setLayout(new PatternLayout("%-5p [%t]: %m%n"));
		
		setup(consoleAppender);
	}
	
	/**
	 * Construct a new logger with a specific appender.
	 * @param appender Custom log appender
	 */
	public static void setup(Appender appender) {
		// Do not initialize twice
		if (LoggerSetup.initialized) return;
		
		// Configure logger
		Logger.getRootLogger().addAppender(appender);
		Logger.getRootLogger().setLevel(Level.INFO);
		
		// Mark initialized
		LoggerSetup.initialized = true;
	}
}
