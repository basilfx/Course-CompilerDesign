package vb.obama.exceptions;

import org.antlr.runtime.RecognitionException;

public class CodegenException extends RecognitionException {

	private static final long serialVersionUID = 1L;

	private String message = null;
	
	public CodegenException(String message) { 
    	super();
    	this.message = message;
    }
	
	public String toString() {
		return this.message;
	}
}