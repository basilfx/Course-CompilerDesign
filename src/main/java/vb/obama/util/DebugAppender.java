package vb.obama.util;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.List;

import com.google.common.base.Strings;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.ErrorHandler;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.layout.PatternSelector;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;

import vb.obama.compiler.SymbolTable;

/**
 * Special type of log appender which indents based on the current symbol table
 * level. Structures the output of the checker.
 *
 * @version 1.0
 */
public class DebugAppender implements Appender {
	/**
	 * Reference to the Symbol table
	 */
	private SymbolTable table;

	/**
	 * Reference to the actual appender
	 */
	private ConsoleAppender appender;

	/**
	 * Construct a new DebugAppender
	 */
	public DebugAppender() {
		PatternLayout layout = PatternLayout.newBuilder()
			.withPatternSelector(new PatternSelector() {
				@Override
				public PatternFormatter[] getFormatters(final LogEvent event) {
					int count = DebugAppender.this.table != null ? DebugAppender.this.table.getCurrentLevel() + 1 : 0;

					final PatternParser parser = PatternLayout.createPatternParser(null);
					final List<PatternFormatter> list = parser.parse("%-5p " + Strings.repeat("  ", count) + "[%t]: %m%n");

					return list.toArray(new PatternFormatter[list.size()]);
				}
			})
			.withCharset(Charset.defaultCharset())
			.withAlwaysWriteExceptions(false)
			.withNoConsoleNoAnsi(false)
			.build();

		this.appender = ConsoleAppender.createDefaultAppenderForLayout(layout);
	}

	/**
	 * Set the Symbol table. Table may be null to clear the table.
	 *
	 * @param table Symbol table
	 */
	public void setTable(final SymbolTable table) {
		this.table = table;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public State getState() {
		return this.appender.getState();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {
		this.appender.initialize();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start() {
		this.appender.start();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop() {
		this.appender.stop();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isStarted() {
		return this.appender.isStarted();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isStopped() {
		return this.appender.isStopped();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void append(LogEvent event) {
		this.appender.append(event);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.appender.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Layout<? extends Serializable> getLayout() {
		return this.appender.getLayout();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean ignoreExceptions() {
		return this.appender.ignoreExceptions();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ErrorHandler getHandler() {
		return this.appender.getHandler();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setHandler(ErrorHandler handler) {
		this.appender.setHandler(handler);
	}
}
