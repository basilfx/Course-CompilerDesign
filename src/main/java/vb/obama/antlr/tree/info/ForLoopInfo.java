package vb.obama.antlr.tree.info;

import org.objectweb.asm.Label;

/**
 * Typed node for a For Loop
 * @version 1.0
 */
public class ForLoopInfo extends Info {
	/**
	 * Label which references to the start of the code. Used during codegen
	 */
	public Label start = null;
	
	/**
	 * Label which references to the end of the for-statement. Used during 
	 * codegen
	 */
	public Label end = null;
	
	/**
	 * Label which references to the compare expression. Used during codegen
	 */
	public Label compare = null;
	
	/**
	 * Label which references to the increment expression. Used during codegen
	 */
	public Label increment = null;
}
