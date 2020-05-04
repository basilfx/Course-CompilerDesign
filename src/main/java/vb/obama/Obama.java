package vb.obama;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.DOTTreeGenerator;
import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import vb.obama.antlr.ObamaChecker;
import vb.obama.antlr.ObamaCodegen;
import vb.obama.antlr.ObamaLexer;
import vb.obama.antlr.ObamaParser;
import vb.obama.antlr.tree.TypedNode;
import vb.obama.antlr.tree.TypedNodeAdapter;
import vb.obama.compiler.SymbolTable;
import vb.obama.util.ExitCodes;
import vb.obama.util.LoggerSetup;

/**
 * Main class. Parses the options and executes them.
 * 
 * @version 1.2
 */
public class Obama {
	/**
	 * Name of program, used for help message
	 */
	public static final String COMMAND = "obama";
	
	/**
	 * Current version number of compiler
	 */
	public static final int[] VERSION = { 2, 0, 0 };
	
	/**
	 * Indicate debugging on or off;
	 */
	public static boolean debug = false;
	
	/**
	 * Indicate to connect to ANTLR Works remote debugging
	 */
	public static boolean antlrWorks = false;
	
	/**
	 * Message logger
	 */
	private static final Logger logger = LogManager.getLogger(Obama.class.getName());
	
	/**
	 * Main entry point.
	 * 
	 * This is the compiler main entry point. It parses the command line options
	 * and exits with a specific exit code. Program has run successful if return
	 * value is equal to zero.
	 * 
	 * @param args Program arguments
	 * @see vb.obama.util.ExitCodes
	 */
	public static void main(String args[]) {
		int exitCode;
		
		try {
			// Build an option parser
			CommandLineParser parser = new GnuParser();
			CommandLine options = parser.parse(Obama.createOptionsParser(), args);
			
			// Execute options
			exitCode = executeBasicOptions(options);
		} catch (ParseException exception) {
			System.err.print(exception);
			printHelp();
			exitCode = ExitCodes.NO_CHOICE;
		}
		
		// Return with an exit code
		System.exit(exitCode);
	}
	
	/**
	 * Create an object with supported options for parsing. 
	 * @return List of supported options
	 */
	@SuppressWarnings("static-access")
	private static Options createOptionsParser() {
		Options result = new Options();
		
		// General methods
		result.addOption("h", "help", false, "print this message");
		result.addOption("v", "version", false, "print version number");
		result.addOption("d", "debug", false, "enable debug and increase logging");
		result.addOption("a", "antlrworks", false, "enable remote debugging of ANTLR files in ANTLR Works");
		
		// Input file
		result.addOption("f", "file", true, "input file");
		
		// Visualization
		result.addOption(
			OptionBuilder
				.withLongOpt("dot-tree")
				.withDescription("print AST to DOT format (for Graphviz)")
				.create()
		);
		result.addOption(
			OptionBuilder
				.withLongOpt("ast-tree")
				.withDescription("print AST tree to stdout")
				.create()
		);

		// Done
		return result;
	}
	
	/**
	 * Execute basic commands. Basic commands are related to the simple methods 
	 * like version and help. If no such option is executed, try the advanced 
	 * options. Returns an exit code.
	 * 
	 * @param options Command line options.
	 * @return Exit code
	 */
	private static int executeBasicOptions(CommandLine options) {
		// Display help
		if (options.hasOption("help")) {
			Obama.printHelp();
			return ExitCodes.SUCCESS;
		}
		
		// Display version
		if (options.hasOption("version")) {
			Obama.printVersion();
			return ExitCodes.SUCCESS;
		}
		
		// Initialize logging
		LoggerSetup.setup();
		
		// Handle debugging and logging
		if (options.hasOption("debug")){
			Configurator.setRootLevel(Level.DEBUG);
			Obama.debug = true;
			
			// Test message
			logger.debug("Debug mode enabled");
		}
		
		// Connect to ANTLR Works or not
		if (options.hasOption("antlrworks")) {
			Obama.antlrWorks = true;
		}
		
		// Simple options have finished. Try advanced ones
		return Obama.executeAdvancedOptions(options);
	}
	
	/**
	 * Execute advanced commands. Advanced options are related to the compiler. 
	 * Returns an exit code.
	 * 
	 * @see Obama.executeBasicOptions
	 */
	private static int executeAdvancedOptions(CommandLine options) {
		boolean silent = false;
		File file = null;
		List<File> files = null;
		ANTLRInputStream inputStream = null;
		
		// Load file
		if (options.hasOption("file")) {
			file = new File(options.getOptionValue("file"));
			
			if (file.exists()) {
				logger.debug(String.format("Using file '%s' for input", file));
				
				try {
					inputStream = new ANTLRInputStream(new FileInputStream(file));
					logger.debug(String.format("File '%s' opened for input, %d bytes in size", file, inputStream.size()));
				} catch (IOException exception) {
					System.err.println(String.format("Error: Unable to open file '%s' for input.", file));
					return ExitCodes.FILE_EXCEPTION;
				}
			} else {
				System.err.println(String.format("Error: Input file '%s' not found.", file));
				return ExitCodes.NO_INPUT_FILE;
			}
		} else {
			System.err.println("Error: No input file.");
			return ExitCodes.NO_INPUT_FILE;
		}
		
		// Now the actual work
		TypedNode tree = null;
		
		try {
			// Lexer
			ObamaLexer lexer = new ObamaLexer(inputStream);
	        CommonTokenStream tokens = new CommonTokenStream(lexer);
	        
	        // Parser
	        ObamaParser parser = Obama.antlrWorks ? new ObamaParser(tokens) : new ObamaParser(tokens);
	        parser.setTreeAdaptor(new TypedNodeAdapter());
	        ObamaParser.program_return parserResult = parser.program();
			tree = (TypedNode) parserResult.getTree();
		} catch (RecognitionException e) {
			System.err.println("Parser failed: " + e);
			System.exit(ExitCodes.PARSER_FAILED);
		}
		
		try {
			// Checker
			CommonTreeNodeStream checkerNodes = new CommonTreeNodeStream(tree);
			ObamaChecker checker = Obama.antlrWorks ? new ObamaChecker(checkerNodes) : new ObamaChecker(checkerNodes);
			checker.setTreeAdaptor(new TypedNodeAdapter());
			checker.setSymbolTable(new SymbolTable());
			checker.setInputFile(file.getAbsolutePath());
			checker.program();
		} catch (RecognitionException e) {
			System.err.println("Checker failed: " + e);
			System.exit(ExitCodes.CHECKER_FAILED);
		}
		
		try {
			// Codegen
			CommonTreeNodeStream codegenNodes = new CommonTreeNodeStream(tree);
			ObamaCodegen codegen = Obama.antlrWorks ? new ObamaCodegen(codegenNodes) : new ObamaCodegen(codegenNodes);
			codegen.setTreeAdaptor(new TypedNodeAdapter());
			codegen.program();

			// Now write it to file
			files = codegen.getHelper().toClasses(System.getProperty("user.dir"));
		} catch (IOException e) {
			System.err.println("Unable to write to disk");
			System.exit(ExitCodes.CODEGEN_FAILED);
		} catch (RecognitionException e) {
			System.err.println("Codegen failed: " + e);
			System.exit(ExitCodes.CODEGEN_FAILED);
		}
		
		if (options.hasOption("ast-tree")) { 
			// Output AST tree to console
			Obama.printAST(tree);
			silent = true;
        } else if (options.hasOption("dot-tree")) { 
        	// Output DOT string
        	Obama.printDOT(tree);
        	silent = true;
        }
		
		// Done
		if (!silent) {
			if (files != null) {
				for (File out : files) {
					System.out.println(String.format(
						"Generated file '%s' (%d bytes)",
						out.getName(),
						out.length()
					));
				}
			}
			
			System.out.println("Compiling successful");
		}
		
		return ExitCodes.SUCCESS;
	}
	
	/**
	 * Print help to stdout
	 */
	private static void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(Obama.COMMAND, Obama.createOptionsParser());
	}
	
	/**
	 * Print version to stdout
	 */
	private static void printVersion() {
		System.out.println(Obama.versionToString());
	}
	
	/**
	 * Print AST to stdout
	 * @param tree Tree to print
	 */
	private static void printAST(CommonTree tree) {
		logger.debug("Generating AST tree");
        System.out.println(tree.toStringTree());
	}
	
	/**
	 * Print DOT to stdout
	 * @param tree Tree to print
	 */
	private static void printDOT(CommonTree tree) {
		logger.debug("Generating DOT file");
        DOTTreeGenerator gen = new DOTTreeGenerator(); 
        StringTemplate st = gen.toDOT(tree); 
        System.out.println(st);
	}
	
	/**
	 * Return compiler version as string
	 * @return Compiler version
	 */
	private static String versionToString() {
		return String.format("v%d.%d.%d", Obama.VERSION[0], Obama.VERSION[1], Obama.VERSION[2]);
	}
}
