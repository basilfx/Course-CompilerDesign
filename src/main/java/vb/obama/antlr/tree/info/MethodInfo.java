package vb.obama.antlr.tree.info;

import java.util.List;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import vb.obama.util.ReflectionUtils;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import static com.google.common.base.Preconditions.*;

/**
 * Typed node for a method
 */
public class MethodInfo extends Info {
	/**
	 * Name of the method
	 */
	public String name;
	
	/**
	 * Method modifiers
	 * @see java.lang.reflect.Modifier
	 */
	public int modifiers;
	
	/**
	 * Denote if this method is a construct
	 */
	public boolean isConstructor;
	
	/**
	 * Return type of this method
	 */
	public Class<?> returnType;
	
	/**
	 * Method throwings
	 */
	public Class<?> throwing;
	
	/**
	 * List of parameters for this method
	 */
	public List<Class<?>> parameters;
	
	/**
	 * Reference to the generator for expression and statements in this method.
	 * Used during codegen.
	 */
	public GeneratorAdapter generator;
	
	/**
	 * Constructor 
	 * 
	 * @require name != null
	 * @require returnType != null
	 */
	public MethodInfo(String name, Class<?> returnType) {
		super();
		this.name = checkNotNull(name);
		this.returnType = checkNotNull(returnType);
		this.parameters = Lists.newArrayList();
	}
	
	/**
	 * Returns the name of this node
	 * 
	 * @return name of the node
	 * @ensure result != null
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of this node
	 * 
	 * @require name != null
	 * @ensure getName() == name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the modifiers of this node
	 * 
	 * @return modifier of the node
	 * @ensure result != null
	 */
	public int getModifiers() {
		return this.modifiers;
	}
	
	/**
	 * Sets the modifier of this node
	 * 
	 * @require modifiers != null
	 * @ensure getModifier() == modifiers
	 */
	public void setModifiers(int modifiers) {
		this.modifiers = modifiers;
	}
	
	/**
	 * Returns the parameters of this node
	 * 
	 * @return parameters of the node
	 * @ensure result != null
	 */
	public List<Class<?>> getParameters() {
		return parameters;
	}
	
	/**
	 * Adds a parameter to this node
	 * 
	 * @require parameter != null
	 * @ensure getParameters.get(parameter) == parameter
	 */
	public void addParameters(Class<?> parameter) {
		this.parameters.add(parameter);
	}
	
	/**
	 * Builds a ASM method from the available information in this class. Is only
	 * a helper method for quick access to the parameters.
	 * 
	 * @returns Method describing the defined method
	 */
	public Method toASM() {
		String name = this.isConstructor ? "<init>" : this.name;
		
		return new Method(
			name, 
			Type.getType(this.returnType), 
			ReflectionUtils.toTypeArray(this.parameters)
		);
	}
}
