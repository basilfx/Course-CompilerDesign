package vb.obama.antlr.tree.info;

import java.util.List;

import org.objectweb.asm.Label;

import com.google.common.collect.Lists;

/**
 * Typed node for a switch-statement
 * @version 1.0
 */
public class SwitchInfo extends Info {
	/**
	 * List of literals. Used during checking to make sure no double literals
	 * exist.
	 */
	public List<Integer> literals;
	
	/**
	 * Label to jump to after a case has executed
	 */
	public Label end;
	
	/**
	 * Initializes the list with literals that make up a case
	 */
	public SwitchInfo() {
		super();
		this.literals = Lists.newArrayList();
	}
}
