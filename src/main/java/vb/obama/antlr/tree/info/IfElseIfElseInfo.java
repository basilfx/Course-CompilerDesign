package vb.obama.antlr.tree.info;

import org.objectweb.asm.Label;

/**
 * Typed node for a if-statement
 * @version 1.0
 */
public class IfElseIfElseInfo extends Info {
	/**
	 * Label to the end of all if-else-if-els statement. Used during codegen
	 */
	public Label end;
	
	/**
	 * Label to the next if-else or else, if any. Used during codegen
	 */
	public Label next;
}
