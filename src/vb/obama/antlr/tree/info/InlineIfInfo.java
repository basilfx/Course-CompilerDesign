package vb.obama.antlr.tree.info;

import org.objectweb.asm.Label;

/**
 * Typed node for a inline if-statement
 * @version 1.0
 */
public class InlineIfInfo extends Info {
	/**
	 * Reference to expression to evaluate if compare expression is false. Used 
	 * during codegen 
	 */
	public Label other;
	
	/**
	 * Reference to the end of the inline-if. Used during codegen
	 */
	public Label end;
}
