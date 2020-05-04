package vb.obama.util;

import java.nio.charset.Charset;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.layout.PatternLayout;

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
		PatternLayout layout = PatternLayout.newBuilder()
				.withPattern("%-5p [%t]: %m%n")
				.withCharset(Charset.defaultCharset())
				.withAlwaysWriteExceptions(false)
				.withNoConsoleNoAnsi(false)
				.build();

		ConsoleAppender consoleAppender = ConsoleAppender.createDefaultAppenderForLayout(layout);

		consoleAppender.start();

		setup(consoleAppender);
	}

	/**
	 * Construct a new logger with a specific appender.
	 * @param appender Custom log appender
	 */
	public static void setup(Appender appender) {
		// Do not initialize twice
		if (LoggerSetup.initialized) {
			return;
		}

		// Configure logger
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		Configuration config = context.getConfiguration();

		for (Map.Entry<String, Appender> item : config.getRootLogger().getAppenders().entrySet()) {
			config.getRootLogger().removeAppender(item.getKey());
		}

		config.getRootLogger().removeAppender("CONSOLE");
		config.getRootLogger().addAppender(appender, null, null);
		config.getRootLogger().setLevel(Level.INFO);

		context.updateLoggers();

		// Mark initialized
		LoggerSetup.initialized = true;
	}
}
