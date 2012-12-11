package vb.obama.exceptions;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.Tree;

public class CheckerException extends RecognitionException {

	private static final long serialVersionUID = 1L;

	private String message = null;
	
	private Tree tree = null;
	
	public CheckerException(String message, Tree tree) { 
    	super();
    	this.message = message;
    	this.tree = tree;
    }
	
	public CheckerException(String message) { 
    	super();
    	this.message = message;
    }
	
	public String toString() {
		return this.message;
	}
}