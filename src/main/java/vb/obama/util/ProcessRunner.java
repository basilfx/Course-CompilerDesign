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
	 * Create a process and execute a command in a specific working directory.
	 * 
	 * @param command Command to execute
	 * @param workingDirectory Directory to change to before execution
	 * @return Console stdout output
	 * @throws IOException
	 */
	public static String runProcess(String[] command, File workingDirectory) throws IOException {
		// Build process
	    Process process = Runtime.getRuntime().exec(command, null, workingDirectory);
	    StringBuilder buffer = new StringBuilder();
	    String input;
	    
	    // Input reader
	    BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
	
	    // Read the output from the command
	    while ((input = stdInput.readLine()) != null) {
	        buffer.append(input);
	    }
	    
	    // Done
	    return buffer.toString();
	}
}