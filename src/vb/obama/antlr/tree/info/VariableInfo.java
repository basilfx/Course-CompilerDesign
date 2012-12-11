package vb.obama.antlr.tree.info;

/**
 * Typed node for a variable
 * @version 1.0
 */
public class VariableInfo extends Info {
	/**
	 * Index of the local variable on the stack in a method. Used during codegen
	 */
	public int asmIndex = 0;
}
