package vb.obama;

import java.io.File;
import java.io.IOException;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.rules.TemporaryFolder;
import vb.obama.antlr.ObamaChecker;
import vb.obama.antlr.ObamaCodegen;
import vb.obama.antlr.ObamaLexer;
import vb.obama.antlr.ObamaParser;
import vb.obama.antlr.tree.TypedNode;
import vb.obama.antlr.tree.TypedNodeAdapter;
import vb.obama.compiler.SymbolTable;
import vb.obama.util.DebugAppender;
import vb.obama.util.LoggerSetup;
import vb.obama.util.ProcessRunner;

import org.junit.After;
import org.junit.Before;

/**
 * Abstract class for the tests of the code samples. Contains a helper methods
 * which allows us to easily execute a test.
 * 
 * @version 1.0
 */
abstract class AbstractTest {
	
	/**
	 * Reference to logger (used to output information to stdout/stderr).
	 */
	private static final Logger logger = LogManager.getLogger(AbstractTest.class);
	
	/**
	 * Reference to appender.
	 */
	private static final DebugAppender appender = new DebugAppender();

	/**
	 * Placeholder to temporary working directory.
	 */
	public TemporaryFolder tempFolder = new TemporaryFolder();

	/**
	 * Debug the parser if true.
	 */
	protected boolean debugParser = false;
	protected boolean debugChecker = false;
	protected boolean debugCodegen = false;
	
	public AbstractTest() {
		appender.start();
		LoggerSetup.setup(appender);
	}

	/**
	 * Handle creation of temporary test output folder.
	 */
	@Before
	public void before() {
		try {
			this.tempFolder.create();
		} catch (IOException exception) {
			logger.error("Unable to create temporary folder for test output", exception);
		}
	}

	/**
	 * Handle cleanup of temporary test output folder.
	 */
	@After
	public void after() {
		this.tempFolder.delete();
	}

	/**
	 * Execute one test from file.
	 *
	 * @param file Path to file
	 * @return Number of syntax errors, or -1 if an RecognitionException occurred
	 * @throws IOException
	 */
	private int executeFile(String file, boolean doChecker, boolean doCodegen, String path) throws RecognitionException, IOException {
		int errors = 0;

		appender.setTable(null);
		logger.info(String.format("*** Testing file '%s' ***", file));

		try {
			// Symbol table
			SymbolTable table = new SymbolTable();
			appender.setTable(table);

			// Lexer
			ObamaLexer lexer = new ObamaLexer(new ANTLRInputStream(this.getClass().getResourceAsStream(file)));
			CommonTokenStream tokens = new CommonTokenStream(lexer);

			// Parser
			ObamaParser parser = this.debugParser ? new ObamaParser(tokens) : new ObamaParser(tokens);
			parser.setTreeAdaptor(new TypedNodeAdapter());
			ObamaParser.program_return parserResult = parser.program();

			TypedNode tree = parserResult.getTree();
			errors = errors + parser.getNumberOfSyntaxErrors();

			// Checker
			if (doChecker) {
				CommonTreeNodeStream nodes = new CommonTreeNodeStream(tree);
				ObamaChecker checker = this.debugChecker ? new ObamaChecker(nodes) : new ObamaChecker(nodes);
				checker.setSymbolTable(table);
				checker.setInputFile(file);
				checker.setTreeAdaptor(new TypedNodeAdapter());
				checker.program();

				// Codegen
				if (doCodegen) {
					CommonTreeNodeStream codegenNodes = new CommonTreeNodeStream(tree);
					ObamaCodegen codegen = this.debugCodegen ? new ObamaCodegen(codegenNodes) : new ObamaCodegen(codegenNodes);
					codegen.setTreeAdaptor(new TypedNodeAdapter());
					codegen.program();

					// Generate code
					codegen.getHelper().toClasses(path);
				}
			}
		} catch (IOException exception) {
			appender.setTable(null);
			logger.info(String.format("*** Test failed: test not found ***"));
			throw exception;
		} catch (RecognitionException exception) {
			appender.setTable(null);
			logger.info(String.format("*** Test finished: exception: %s ***", exception));
			throw exception;
		}
		
		appender.setTable(null);
		logger.info(String.format("*** Test finished: %d errors ***", errors));
		return errors;
	}
	
	protected int executeFileParser(String file) throws RecognitionException, IOException {
		return this.executeFile(file, false, false, null);
	}
	
	protected int executeFileChecker(String file) throws RecognitionException, IOException {
		return this.executeFile(file, true, false, null);
	}
	
	protected int executeFileCodegen(String file) throws RecognitionException, IOException {
		return this.executeFile(file, true, true, this.tempFolder.getRoot().getAbsolutePath());
	}

	/**
	 * Execute a Java application and return its stdin. Requires java to be on the
	 * execution path.
	 *
	 * @param className Name of class to execute
	 * @return Console stdout output
	 * @throws IOException
	 */
	public String runJavaFile(String className) throws IOException {
		String[] arguments = {"java", className};
		return ProcessRunner.runProcess(arguments, this.tempFolder.getRoot());
	}
}
