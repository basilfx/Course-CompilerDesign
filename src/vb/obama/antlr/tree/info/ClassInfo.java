package vb.obama.antlr.tree.info;

import java.util.List;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.GeneratorAdapter;

import com.google.common.collect.Lists;

/**
 * Class information
 * @version 1.1
 */
public class ClassInfo extends Info {
	/**
	 * Name of the class
	 */
	public String name;
	
	/**
	 * Name of the input file
	 */
	public String file;
	
	/**
	 * Class modifiers
	 * @see java.lang.reflect.Modifer
	 */
	public int modifiers;
	
	/**
	 * Name of class which this class extends
	 */
	public Class<?> extending;
	
	/**
	 * Names of implementing interfaces
	 */
	public List<Class<?>> implementing;
	
	/**
	 * Constructor
	 * Initializes the list holding the interfaces
	 */
	public ClassInfo() {
		this.implementing = Lists.newArrayList();
	}
	
	/**
	 * Reference to the outer classwriter, as needed by the codegen
	 */
	public ClassWriter classWriter;
	
	/**
	 * Reference to the generator of the static initializer, as needed
	 * by the codegen
	 */
	public GeneratorAdapter generator;
}
