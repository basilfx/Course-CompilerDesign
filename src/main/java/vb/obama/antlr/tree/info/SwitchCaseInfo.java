package vb.obama.antlr.tree.info;

import org.objectweb.asm.Label;

/**
 * Typed node info for a switch-case
 * @version 1.0
 */
public class SwitchCaseInfo extends Info {
	/**
	 * Integer value of this case, evaluated in the checker
	 */
	public int value;
	
	/**
	 * Start label of this case. Used during codegen
	 */
	public Label label;
}
