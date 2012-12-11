package vb.obama.compiler;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import vb.obama.antlr.tree.NodeType;
import vb.obama.antlr.tree.TypedNode;
import vb.obama.antlr.tree.info.MethodCallInfo;
import vb.obama.antlr.tree.info.VariableInfo;
import vb.obama.exceptions.CheckerException;
import vb.obama.exceptions.CodegenException;
import vb.obama.util.ReflectionUtils;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * This class contains the required built-in methods for the basic expression
 * language. Since these methods are not part of the programmers program, this
 * method calls will be generated when needed.
 * 
 * @version 1.0
 */
public class BuiltIns {
	private static final String[] BUILTINS = {"print", "read", "init", "cast"};
	
	public static void checkBuiltIn(String name, TypedNode node, MethodCallInfo info, List<Class<?>> parameters, List<TypedNode> parameterNodes) throws CheckerException {
		node.setNodeType(NodeType.METHOD_CALL_BUILTIN);
		
		// Check method name
		if (!BuiltIns.isBuiltIn(name)) {
			throw new CheckerException(String.format(
				"Method '%s' is not a built-in method",
				name
			));
		}
		
		// Check for arguments
		if (parameters.size() == 0) {
			throw new CheckerException(String.format(
				"Built-in method '%s' expects at least one argument",
				name
			));
		}
		
		// Check 
		if (name.equals("cast")) {
			checkCast(node, parameters);
		} else if (name.equals("read")) {
			BuiltIns.checkRead(parameterNodes);
			Class<?> type = parameters.size() == 1 ? parameters.get(0) : void.class;
			node.setReturnType(type);
		} else if (name.equals("print")) {
			Class<?> type = parameters.size() == 1 ? parameters.get(0) : void.class;
			node.setReturnType(type);
		} else if (name.equals("init")) {
			BuiltIns.checkInit(node, parameterNodes);
		} else {
			node.setReturnType(void.class);
		}
	}
	
	/**
	 * Verify the integrity of a init function.
	 * @param node
	 * @param parameters
	 */
	public static void checkInit(TypedNode node, List<TypedNode> parameters) {
		TypedNode child = (TypedNode) parameters.get(0);
		
		if (child.getNodeType() == NodeType.NEW) {
			node.setReturnType(child.getReturnType());
		}
	}
	
	/**
	 * A read operation can only write to local variables, so check if each 
	 * parameter is a variable. Throws a CheckerException if this is not the
	 * case.
	 * 
	 * @param nodes List of nodes
	 * @throws CheckerException
	 */
	public static void checkRead(List<TypedNode> nodes) throws CheckerException {
		for (TypedNode node : nodes) {
			if (node.getNodeType() != NodeType.VARIABLE) {
				throw new CheckerException("Built-in read can only write to defined variables");
			}
		}
	}
	
	/**
	 * Check a cast from one type to another type. Sets the return type to the new
	 * type. A cast can only happen from one class to another.
	 * 
	 * @param node Method call node
	 * @param parameters List of types
	 * @throws CheckerException
	 */
	public static void checkCast(TypedNode node, List<Class<?>> parameters) throws CheckerException {
		if (parameters.size() != 2) {
			throw new CheckerException("Cast expects exact two parameters (from, to)");
		}
		
		node.setReturnType(parameters.get(1));
	}
	
	/**
	 * Verify if a give method is a valid built-in method.
	 * @param methodName Name of the method
	 * @return true if the method is valid.
	 * @requires !Strings.isNullOrEmpty(methodName)
	 */
	public static boolean isBuiltIn(String methodName) {
		checkArgument(!Strings.isNullOrEmpty(methodName));
		return Lists.newArrayList(BuiltIns.BUILTINS).contains(methodName);
	}
	
	/**
	 * Generate the inline code for a built-in function. By default, a built-in
	 * method will leave the expression result (parameter) on the stack if and
	 * only if the parameter count is one or the result is not used (i.e. not in
	 * a expression or assignment).
	 * 
	 * @param generator The GeneratorMethod from the surrounding method
	 * @param info Method information
	 * @param inExpression Indicate if this call is executed in an expression
	 */
	public static void generate(GeneratorAdapter generator, MethodCallInfo info, boolean inExpression) throws CodegenException {
		checkNotNull(generator);
		checkNotNull(info);
		
		// Number of parameters
		int count = info.parameters.size();
		
		if (info.method.equals("print") && count == 1) {
			BuiltIns.generatePrintSingle(generator, info.parameters.get(0), inExpression);
		} else if (info.method.equals("print") && count > 1) {
			BuiltIns.generatePrintMultiple(generator, info.parameters);
		} else if (info.method.equals("read") && count == 1) {
			BuiltIns.generateReadSingle(generator, info.parameterNodes.get(0), inExpression);
		} else if (info.method.equals("read") && count > 1) {
			BuiltIns.generateReadMultiple(generator, info.parameterNodes);
		} else if (info.method.equals("init") && count > 0) {
			BuiltIns.generateConstructorInit(generator, info);
		} else if (info.method.equals("cast") && count == 2) {
			BuiltIns.generateCast(generator, info);
		}
	}
	
	/**
	 * 
	 */
	public static void generateConstructorInit(GeneratorAdapter generator, MethodCallInfo info) {
		Class<?> owner = info.parameters.get(0);
		List<Class<?>> types = Lists.newArrayList();
		
		for (int i = 1; i < info.parameters.size(); i++) {
			types.add(info.parameters.get(i));
		}
		
		generator.invokeConstructor(
			Type.getType(owner),
			new Method(
				"<init>",
				Type.getType(void.class),
				ReflectionUtils.toTypeArray(types)
			)
		);
	}
	
	public static void generateCast(GeneratorAdapter generator, MethodCallInfo info) {
		Class<?> from = info.parameters.get(0);
		Class<?> to = info.parameters.get(1);
		
		generator.cast(Type.getType(from), Type.getType(to));
	}
	
	/**
	 * Print a single variable to the console and leaves the variable on the stack
	 * if and only if inExpression is true.
	 * 
	 * @param generator The GeneratorMethod from the surrounding method
	 * @param type Type of variable
	 * @param inExpression Indicate if this call is executed in an expression
	 */
	public static void generatePrintSingle(GeneratorAdapter generator, Class<?> type, boolean inExpression) throws CodegenException {
		// Duplicate parameter on stack
		generator.dup();
		
		// Load System.out on stack
		generator.getStatic(
			Type.getType(System.class),
			"out",
			Type.getType(System.out.getClass())
		);
		
		// Swap the values
		generator.swap(
			Type.getType(type),
			Type.getType(System.out.getClass())
		);
		
		// Invoke System.out.println
		generator.invokeVirtual(
			Type.getType(System.out.getClass()),
			new Method(
				"println",
				Type.getType(void.class),
				new Type[] { Type.getType(type) }
			)
		);
	}
	
	/**
	 * Prints a list of variables to the console and pops of the variable
	 * 
	 * @param generator The GeneratorMethod from the surrounding method
	 * @param types List of variables to print
	 */
	public static void generatePrintMultiple(GeneratorAdapter generator, List<Class<?>> types) throws CodegenException {
		for (Class<?> type : types) {
			// Load System.out on stack
			generator.getStatic(
				Type.getType(System.class),
				"out",
				Type.getType(System.out.getClass())
			);
			
			// Swap the values
			generator.swap(
				Type.getType(type),
				Type.getType(System.out.getClass())
			);
			
			// Invoke System.out.println
			generator.invokeVirtual(
				Type.getType(System.out.getClass()),
				new Method(
					"println",
					Type.getType(void.class),
					new Type[] { Type.getType(type) }
				)
			);
		}
	}
	
	/**
	 * Generate a read multiple inline code
	 * 
	 * @param generator Method generator
	 * @param nodes List of nodes
	 * @throws CodegenException
	 */
	public static void generateReadMultiple(GeneratorAdapter generator, List<TypedNode> nodes) throws CodegenException {
		// First, initialize all variables to zero (in case of read error)
		for (TypedNode node : nodes) {
			if (node.getNodeType() != NodeType.VARIABLE)
				throw new CodegenException("Cannot write to non-variables"); 
			
			VariableInfo info = (VariableInfo) node.getInfo();
			generator.pop();
			generator.push(0);
			generator.storeLocal(info.asmIndex);
		}
		
		// Now, read an integer for every parameter
		for (TypedNode node : nodes) {
			BuiltIns.generateReadTemplate(generator, node);
		}
	}
	
	/**
	 * Generate a read single instruction
	 * 
	 * @param generator Method generator
	 * @param node The node
	 * @param inExpression
	 * @throws CodegenException
	 */
	public static void generateReadSingle(GeneratorAdapter generator, TypedNode node, boolean inExpression) throws CodegenException {
		// Set default to zero (in case of an read error)
		VariableInfo info = (VariableInfo) node.getInfo();
		generator.pop();
		generator.push(0);
		generator.storeLocal(info.asmIndex);
		
		// And generate a template
		BuiltIns.generateReadTemplate(generator, node);
		
		// Normally, non-used results are automatically popped, but this
		// does not count for built-ins, because they duplicate the 
		// parameters explicitly.
		generator.loadLocal(info.asmIndex);
	}
	
	/**
	 * Template to generate a read statement.
	 * 
	 * @param generator Method generator
	 * @param node Variable node
	 * @throws CodegenException
	 */
	private static void generateReadTemplate(GeneratorAdapter generator, TypedNode node) throws CodegenException {
		// Load a default value
		VariableInfo info = (VariableInfo) node.getInfo();
		
		// Generate labels for the exceptions
		Label end = new Label();
		Label start = new Label();
		
		// Mark start of try/catch block
		generator.mark(start);

		// New BufferedReader
        generator.newInstance(Type.getType(BufferedReader.class));
        generator.dup();
        
        // New InputStreamReader
        generator.newInstance(Type.getType(InputStreamReader.class));
        generator.dup();
        
        // Construct the objects
        generator.getStatic(
            Type.getType(System.class), 
            "in", 
            Type.getType(InputStream.class)
        );
        
        generator.invokeConstructor(
            Type.getType(InputStreamReader.class), 
            new Method(
                "<init>", 
                Type.getType(void.class), 
                new Type[] { Type.getType(InputStream.class) }
            )
        );
        generator.invokeConstructor(
            Type.getType(BufferedReader.class), 
            new Method(
                "<init>", 
                Type.getType(void.class), 
                new Type[] { Type.getType(Reader.class) }
            )
        );
        
        // Read one line
        generator.invokeVirtual(
            Type.getType(BufferedReader.class),
            new Method(
                "readLine", 
                Type.getType(String.class), 
                new Type[] { }
            )
        );
        
        // Convert it to a integer
        generator.invokeStatic(
            Type.getType(Integer.class),
            new Method(
                "parseInt",
                Type.getType(int.class), 
                new Type[] { Type.getType(String.class) }
            )
        );
		
        // Save result and go to the end
        generator.storeLocal(info.asmIndex);
		generator.goTo(end);
		
		// Catch exception, but do nothing except popping the exception
		generator.catchException(start, end, Type.getType(Exception.class));
		generator.pop();
		generator.goTo(end);
		
		// Done, load result
		generator.mark(end);
	}
}
