package vb.obama.antlr.tree.info;

import org.objectweb.asm.Label;

/**
 * Typed node for a while-statement
 * @version 1.0 
 */
public class WhileInfo extends Info {
	/**
	 * Label which references to the start of the code. Used during codegen.
	 */
	public Label start = null;
	
	/**
	 * Label which references to the end of the while block. Used during codegen
	 */
	public Label end = null;
}
