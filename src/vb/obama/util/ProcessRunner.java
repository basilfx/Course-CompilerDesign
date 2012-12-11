package vb.obama.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Small wrapper for executing processes
 * @version 1.0
 */
public class ProcessRunner {
	/**
	 * Execute a Java application and return its stdin. Requires java to be on the
	 * execution path.
	 * 
	 * @param className Name of class to execute
	 * @param workingDir Name of directory from which the class will be executed
	 * @return Console stdout output
	 * @throws IOException
	 */
	public static String runJavaFile(String className, String workingDir) throws IOException {
		String[] arguments = {"java", className};
		return ProcessRunner.runProcess(arguments, workingDir);
	}
	
	/**
	 * Create a process and execute a command in a specific working directory.
	 * 
	 * @param command Command to execute
	 * @param workingDir Directory to change to before execution
	 * @return Console stdout output
	 * @throws IOException
	 */
	public static String runProcess(String[] command, String workingDir) throws IOException {
		// Build process
	    Process process = Runtime.getRuntime().exec(command, null, new File(workingDir));
	    StringBuilder buffer = new StringBuilder();
	    String input = null;
	    
	    // Input reader
	    BufferedReader stdInput = new BufferedReader(
	    	new InputStreamReader(process.getInputStream())
	    );
	
	    // Read the output from the command
	    while ((input = stdInput.readLine()) != null) {
	        buffer.append(input);
	    }
	    
	    // Done
	    return buffer.toString();
	}
}