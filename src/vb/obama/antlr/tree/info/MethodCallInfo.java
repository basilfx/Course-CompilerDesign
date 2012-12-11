package vb.obama.antlr.tree.info;

import java.util.List;

import vb.obama.antlr.tree.TypedNode;

import com.google.common.collect.Lists;

/**
 * Typed node for a method call
 * @version
 */
public class MethodCallInfo extends Info {
	/**
	 * Name of the method call
	 */
	public String method;
	
	/**
	 * Return type of a method call
	 */
	public Class<?> returnType;
	
	/**
	 * Class which holds this method, if non-local
	 */
	public Class<?> owner;
	
	/**
	 * List of parameter types
	 */
	public List<Class<?>> parameters;
	
	/**
	 * List of the parameter nodes, for raw access to method parameter information
	 */
	public List<TypedNode> parameterNodes;
	
	/**
	 * Initializes the lists holding the parameters.
	 */
	public MethodCallInfo() {
		this.parameters = Lists.newArrayList();
		this.parameterNodes = Lists.newArrayList();
	}
}
