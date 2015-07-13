package vb.obama.antlr.tree.info;

/**
 * Field information
 * @version 1.0
 */
public class FieldInfo extends Info {
	/**
	 * The class which holds this field, for example System
	 */
	public Class<?> owner;
	
	/**
	 * The type of this field, for example field 'out' in class 'System' has
	 * type 'PrintStream'
	 */
	public Class<?> type;
	
	/**
	 * Name of the field, for example 'out'
	 */
	public String name;
}
