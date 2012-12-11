package vb.obama.util;

/**
 * List of exit codes used in this program. Used to clarify the 
 * numbers.
 * 
 * @version 1.1
 */
public final class ExitCodes {
	// General purpose
	public static final int SUCCESS = 0;
	public static final int NO_CHOICE = 1;
	public static final int NO_INPUT_FILE = 2;
	public static final int FILE_EXCEPTION = 3;
	
	// Parser related
	public static final int PARSER_FAILED = 4;
	
	// Checker related
	public static final int CHECKER_FAILED = 4;
	
	// Codegen related
	public static final int CODEGEN_FAILED = 4;
}
