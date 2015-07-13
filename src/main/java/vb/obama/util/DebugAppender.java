package vb.obama.util;

import java.io.OutputStreamWriter;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

import com.google.common.base.Strings;

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
		this.appender = new ConsoleAppender();
		this.appender.setWriter(new OutputStreamWriter(System.out));
		this.appender.setLayout(new PatternLayout("%-5p [%t]: %m%n"));
	}
	
	/**
	 * Set the Symbol table. Table may be null to clear the table.
	 * @param table Symbol table
	 */
	public void setTable(SymbolTable table) {
		this.table = table;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addFilter(Filter arg0) {
		this.appender.addFilter(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearFilters() {
		this.appender.clearFilters();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		this.appender.close();
	}

	/**
	 * Output a logging event to the logger. Sets the layout of the output to 
	 * indent the text based on the current level of the symbol table. If the
	 * symbol table is null or the current level is -1, nog indentation will
	 * happen.
	 */
	@Override
	public void doAppend(LoggingEvent arg0) {
		int count = this.table != null ? this.table.getCurrentLevel() + 1 : 0;
		this.setLayout(new PatternLayout("%-5p " + Strings.repeat("  ", count) + "[%t]: %m%n"));
		this.appender.doAppend(arg0);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ErrorHandler getErrorHandler() {
		return this.appender.getErrorHandler();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Filter getFilter() {
		return this.appender.getFilter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Layout getLayout() {
		return this.appender.getLayout();
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
	public boolean requiresLayout() {
		return this.appender.requiresLayout();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setErrorHandler(ErrorHandler arg0) {
		this.appender.setErrorHandler(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLayout(Layout arg0) {
		this.appender.setLayout(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setName(String arg0) {
		this.appender.setName(arg0);
	}
}
