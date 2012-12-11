package vb.obama.compiler;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.objectweb.asm.Opcodes;

import vb.obama.antlr.tree.NodeType;
import vb.obama.antlr.tree.TypedNode;
import vb.obama.antlr.tree.info.ClassInfo;
import vb.obama.antlr.tree.info.FieldInfo;
import vb.obama.antlr.tree.info.ForLoopInfo;
import vb.obama.antlr.tree.info.IfElseIfElseInfo;
import vb.obama.antlr.tree.info.InlineIfInfo;
import vb.obama.antlr.tree.info.MethodCallInfo;
import vb.obama.antlr.tree.info.MethodInfo;
import vb.obama.antlr.tree.info.ParameterInfo;
import vb.obama.antlr.tree.info.SwitchCaseInfo;
import vb.obama.antlr.tree.info.SwitchInfo;
import vb.obama.antlr.tree.info.VariableInfo;
import vb.obama.antlr.tree.info.WhileInfo;
import vb.obama.exceptions.CheckerException;
import vb.obama.exceptions.SymbolTableException;
import vb.obama.util.ReflectionUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * To minimize the lines of Java in ObamaChecker.g, this class is used to check
 * constraints.
 * 
 * @version 1.2
 */
public class CheckerHelper {
	
	/**
	 * Message logger
	 */
	private static final Logger logger = Logger.getLogger(CheckerHelper.class.getName());
	
	/**
	 * Reference to the symbol table
	 */
	private SymbolTable table = null;
	
	/**
	 * List of imported packages. Used to resolve types
	 */
	private List<Import> imports = null;
	
	/**
	 * List of defined classes. Used to resolve custom types
	 */
	private List<String> classes = null;
	
	/**
	 * Name of the input file
	 */
	private String fileName;
	
	/**
	 * Name of the main class.
	 */
	private String className;
	
	/**
	 * Construct a new CheckerHelper, used to off load the ANTLR file from Java
	 * code. Throws an CheckerException if a valid class name cannot be 
	 * determined from the input file.
	 * 
	 * @param table Symbol table
	 * @param inputFile Source file
	 * @requires table != null && inputFile != null
	 * @throws CheckerException
	 */
	public CheckerHelper(SymbolTable table, String inputFile) throws CheckerException {
		checkNotNull(table);
		checkNotNull(inputFile);
		
		this.imports = Lists.newArrayList();
		this.classes = Lists.newArrayList();
		this.table = table;
		this.fileName = inputFile;
		
		this.className = new File(inputFile).getName();
		if (this.className.indexOf('.') > 0)
			this.className = this.className.substring(0, this.className.indexOf('.'));
		
		// Check class name
		boolean valid = Pattern.matches("[a-zA-Z$_][a-zA-Z$_0-9]*", this.className);
		if (!valid) {
			throw new CheckerException(String.format(
				"Class name '%s' is not a valid class name",
				this.className
			));
		}
	}
	
	public void openScope() {
		this.table.openScope();
		logger.debug(String.format("Ascended scope: level=%d", this.table.getCurrentLevel()));
	}
	
	public void closeScope() {
		this.table.closeScope();
		logger.debug(String.format("Descended scope: level=%d", this.table.getCurrentLevel()));
	}
	
	public void visitContentStart(TypedNode node) throws CheckerException {
		node.setNodeType(NodeType.CLASS);
		
		// Create the main class
		ClassInfo info = new ClassInfo();
		info.modifiers = Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER;
		info.file = this.fileName;
		info.name = this.className;
		info.extending = Object.class;
		node.setInfo(info);
	}
	
	public void visitContentEnd(TypedNode node) throws CheckerException {
		// Verify the current level is back to start
		if (this.table.getCurrentLevel() != -1) {
			throw new CheckerException(String.format("Scope is not closed! Current level: %d", this.table.getCurrentLevel()));
		}
	}
	
	/**
	 * @requires node.getChildCount() == 1
	 */
	public void visitImport(TypedNode node) {
		checkNotNull(node.getChildCount() == 1);
		node.setNodeType(NodeType.IMPORT);
		
		String name = node.getChild(0).getText();
		this.imports.add(new Import(name));
		
		logger.debug(String.format("Package import: path='%s'", name));
	}
	
	/**
	 * @requires node.getChildCount() == 5
	 */
	@SuppressWarnings("unchecked")
	public void visitMethodDeclaration(TypedNode node) throws CheckerException {
		checkArgument(node.getChildCount() == 5);
		node.setNodeType(NodeType.METHOD);
	
		int modifiers = Integer.parseInt(node.getChild(0).getText());
		int index = 0;
		String methodName = node.getChild(2).getText();
		Class<?> returnType = ((TypedNode) node.getChild(1)).getReturnType();
		node.setReturnType(returnType);
		
		MethodInfo info = new MethodInfo(methodName, returnType);
		info.setModifiers(modifiers);
		node.setInfo(info);
		
		TypedNode parameters = (TypedNode) node.getChild(3);
		parameters.setNodeType(NodeType.METHOD_PARAMETER_BAG);
		
		if (parameters.getChildCount() > 0) {
			for (TypedNode parameter : (List<TypedNode>) parameters.getChildren()) {
				String name = null;
				String keyword = null;
				Class<?> type = null;
				
				if (parameter.getNodeType() == NodeType.METHOD_PARAMETER_NAMED) {
					keyword = ((TypedNode) parameter.getChild(0)).getText();
					name = ((TypedNode) parameter.getChild(2)).getText();
					type = ((TypedNode) parameter.getChild(1)).getReturnType();
				} else if (parameter.getNodeType() == NodeType.METHOD_PARAMETER_UNNAMED) {
					keyword = "_u" + info.getParameters().size();
					name = ((TypedNode) parameter.getChild(0)).getText();
					type = ((TypedNode) parameter.getChild(1)).getReturnType();
				} else {
					throw new CheckerException("Expected named or unnamed parameter", node);
				}
				
				// Change it's type since it is named anyway
				parameter.setNodeType(NodeType.METHOD_PARAMETER);
				parameter.setReturnType(type);
				parameter.setInfo(new ParameterInfo(name, keyword, index));
				index++;
				
				// For debugging only
				info.addParameters(type);
			}
		}
		
		// Now add it to the symbol table
		IdEntry entry = new IdEntry();
		entry.setNode(node);
		
		try {
			this.table.enter(methodName, entry);
		} catch (SymbolTableException e) {
			throw new CheckerException(e.getMessage());
		}
		
		// Done
		logger.debug(String.format(
			"Method declaration: modifiers=%d returnType='%s' name='%s' parameterTypes=%s", 
			modifiers, 
			returnType, 
			methodName, 
			ReflectionUtils.prettyTypesList(info.getParameters())
		));
	}
	
	/**
	 * @requires node.getChildCount() == 5
	 */
	@SuppressWarnings("unchecked")
	public void visitMethodContentStart(TypedNode node) throws CheckerException {
		checkArgument(node.getChildCount() == 5);
		
		// Get the parameters node
		TypedNode parameters = (TypedNode) node.getChild(3);
		checkArgument(parameters.getNodeType() == NodeType.METHOD_PARAMETER_BAG);
		
		// Open new scope
		this.openScope();
		
		// Inject the method parameters into the symbol table, if any
		if (parameters.getChildCount() > 0) {
			for (TypedNode parameter : (List<TypedNode>) parameters.getChildren()) {
				checkArgument(parameter.getNodeType() == NodeType.METHOD_PARAMETER);
				
				IdEntry entry = new IdEntry();
				entry.setNode(parameter);
				ParameterInfo info = (ParameterInfo) parameter.getInfo();
				
				try {
					this.table.enter(info.getName(), entry);
				} catch (SymbolTableException exception) {
					// This shouldn't fail since we've just opened a new level
				}
			}
		}
		
		// Child as scope
		((TypedNode) node.getChild(4)).setNodeType(NodeType.SCOPE);
	}
	
	public void visitMethodContentEnd(TypedNode node) throws CheckerException {
		this.closeScope();
	}
	
	/**
	 * @requires node.getChildCount() == 1
	 */
	public void visitMethodReturn(TypedNode node) throws CheckerException {
		checkArgument(node.getChildCount() == 1);
		TypedNode expression = (TypedNode) node.getChild(0);
		
		// A return can only happen in a method, but we may nested, so walk up
		// until we see a method node
		TypedNode current = node;
		
		while (true) {
			current = (TypedNode) current.getParent();
			
			if (current == null) { // Have we reached the top?
				throw new CheckerException("Return statement not in method");
			} else if (current.getNodeType() == NodeType.METHOD) { // Or the method node
				break;
			}
		}
		
		// Found node, now check types
		if (expression.getReturnType() == current.getReturnType()) {
			logger.debug(String.format(
				"Method return statement: returnType='%s' methodReturnType='%s",
				expression.getReturnType(),
				current.getReturnType()
			));
		} else {
			throw new CheckerException(String.format(
				"Return type '%s' not compatible with method return type '%s'",
				expression.getReturnType(),
				current.getReturnType()
			));
		}
	}
	
	/**
	 * @requires node.getChildCount() == 1
	 */
	public void visitClass(TypedNode node) throws CheckerException {
		checkNotNull(node.getChildCount() == 1);
		node.setNodeType(NodeType.CLASS);
		
		// Construct a new info object
		String name = node.getChild(0).getText();
		ClassInfo info = new ClassInfo();
		
		info.name = name;
		info.file = this.fileName;
		info.extending = Object.class;
		info.modifiers = Opcodes.ACC_PUBLIC;
		
		// Add entry to symbol table
		IdEntry entry = new IdEntry();
		entry.setNode(node);
		
		try {
			this.table.enter(name, entry);
		} catch (SymbolTableException exception) {
			throw new CheckerException(exception.getMessage());
		}
		
		// Define it as a custom type
		this.classes.add(name);
		
		// Done
		node.setInfo(info);
		logger.debug(String.format("Class declaration: name='%s'", name));
	}
	
	/**
	 * @requires node.getChildCount() >= 1
	 */
	public void visitMethodCall(TypedNode node) throws CheckerException {
		checkArgument(node.getChildCount() >= 1);
		
		MethodCallInfo info = new MethodCallInfo();
		TypedNode field = (TypedNode) node.getChild(0);
		TypedNode method = (TypedNode) node.getChild(1);
		
		String name = method.getChild(0).getText();
		List<Class<?>> parameterTypes = Lists.newArrayList();
		List<TypedNode> parameterNodes = Lists.newArrayList();
		
		for (int i = 1; i < node.getChildCount(); i++) {
			TypedNode parameter = (TypedNode) node.getChild(i);

			switch (parameter.getNodeType()) {
				case METHOD_PARAMETER_NAMED:
					parameterTypes.add(((TypedNode) parameter.getChild(1)).getReturnType());
					parameterNodes.add((TypedNode) parameter.getChild(1));
					break;
				case METHOD_PARAMETER_UNNAMED:
					parameterTypes.add(((TypedNode) parameter.getChild(0)).getReturnType());
					parameterNodes.add((TypedNode) parameter.getChild(0));
					break;
				case METHOD_NO_PARAMETERS:
					break;
			}
		}
		
		// Check if method is callable
		if (field.getNodeType() == NodeType.FIELD || field.getNodeType() == NodeType.FIELD_STATIC || field.getNodeType() == NodeType.VARIABLE) {
			Class<?> type = field.getReturnType();
			boolean found = false;
			
			List<Method> candidates = Lists.newArrayList();
			
			for (Method m : type.getMethods()) {
				if (!m.getName().equals(name)) continue;
				if (!Modifier.isPublic(m.getModifiers())) continue; 
	
				// Can only call static fields on a static object
				if (field.getNodeType() == NodeType.FIELD_STATIC)
					if (!Modifier.isStatic(m.getModifiers())) continue;
				
				candidates.add(m);
			}
			
			// Loop all candidates and find the best one
			for (int i = 0; i < candidates.size(); i++) {
				Method m = candidates.get(i);
				
				boolean condition = m.isVarArgs() ? 
					parameterTypes.size() >= m.getParameterTypes().length :
					parameterTypes.size() == m.getParameterTypes().length;
					
				if (condition) {
					node.setReturnType(m.getReturnType());
					found = true;
					break;
				} else {
					// No correct match found
					if (i == candidates.size() - 1) {
						throw new CheckerException(String.format(
							"Method '%s' parameters do not match", 
							method.getChild(0).getText()
						));
					}
				}
			}
			
			// No matching method found
			if (found == false) {	
				throw new CheckerException(String.format(
					"Static field '%s' has no callable methods", 
					field.getText()
				));
			}
			
			// No errors up here
			info.parameters = parameterTypes;
			info.method = name;
			info.returnType = node.getReturnType();
			info.owner = field.getReturnType();
			
			node.setNodeType(NodeType.METHOD_CALL_STATIC);
		} else if (field.getNodeType() == NodeType.FIELD_GLOBAL) {
			// Retrieve corresponding method
			IdEntry entry = this.table.retrieve(name);
			
			if (entry == null) {
				throw new CheckerException(String.format(
					"Method '%s' in global scope not (yet) defined",
					name
				));
			}
			
			// Get method info
			MethodInfo methodInfo = (MethodInfo) (entry.getNode().getInfo());
			
			// Now match parameters and check if they are correct
			if (parameterTypes.size() == methodInfo.parameters.size()) {
				for (int i = 0; i < parameterTypes.size(); i++) {
					Class<?> parameter = parameterTypes.get(i);
					Class<?> methodParameter = methodInfo.parameters.get(i);
				
					// Check if parameter types are equal
					if (!parameter.equals(methodParameter)) {
						throw new CheckerException(String.format(
							"Parameter %d of global method '%s' mismatch ('%s' vs '%s')",
							i,
							name,
							parameter.getName(),
							methodParameter.getName()
						));
					}
				}
			} else {
				throw new CheckerException(String.format(
					"Global method '%s' signature mismatch",
					name
				));
			}
			
			// Set info
			info.parameters = parameterTypes;
			info.method = name;
			info.returnType = methodInfo.returnType;
			
			// Done
			node.setReturnType(methodInfo.returnType);
			node.setNodeType(NodeType.METHOD_CALL_GLOBAL);
		} else if (field.getNodeType() == NodeType.FIELD_THIS) {
			node.setReturnType(void.class);
			node.setNodeType(NodeType.METHOD_CALL_THIS);
		} else if (field.getNodeType() == NodeType.FIELD_BUILTIN) {
			BuiltIns.checkBuiltIn(name, node, info, parameterTypes, parameterNodes);
			
			// Check method name
			info.parameters = parameterTypes;
			info.parameterNodes = parameterNodes;
			info.method = name;
			info.returnType = node.getReturnType();
		}
		
		// Done
		node.setInfo(info);
		
		logger.debug(String.format(
			"Method call: parameterTypes=%s", 
			ReflectionUtils.prettyTypesList(parameterTypes)
		));
	}
	
	/**
	 * @requires node.getChildCount() == 3
	 */
	public void visitMethodParameterNamed(TypedNode node) {
		checkArgument(node.getChildCount() == 3);
		node.setNodeType(NodeType.METHOD_PARAMETER_NAMED);
	}

	/**
	 * @requires node.getChildCount() == 2
	 */
	public void visitMethodParameterUnnamed(TypedNode node) {
		checkArgument(node.getChildCount() == 2);
		node.setNodeType(NodeType.METHOD_PARAMETER_UNNAMED);
	}

	/**
	 * @requires node.getChildCount() == 1
	 */
	public void visitMethodCallNoParameters(TypedNode node) throws CheckerException {
		checkArgument(node.getChildCount() == 1);
		node.setNodeType(NodeType.METHOD_NO_PARAMETERS);
	}
	
	/**
	 * @requires node.getChildCount() == 2
	 */
	public void visitMethodCallParameterNamed(TypedNode node) {
		checkArgument(node.getChildCount() == 2);
		node.setNodeType(NodeType.METHOD_PARAMETER_NAMED);
	}
	
	/**
	 * @requires node.getChildCount() == 1
	 */
	public void visitMethodCallParameterUnnamed(TypedNode node) {
		checkArgument(node.getChildCount() == 1);
		node.setNodeType(NodeType.METHOD_PARAMETER_UNNAMED);
	}
	
	/**
	 * @requires node.getChildCount() == 2
	 */
	public void visitVarDeclaration(TypedNode node) throws CheckerException {
		checkArgument(node.getChildCount() == 2);
		
		node.setNodeType(NodeType.VARIABLE);
		node.setInfo(new VariableInfo());
		
		TypedNode type = (TypedNode) node.getChild(0);
		TypedNode identifier = (TypedNode) node.getChild(1);
		
		IdEntry entry = new IdEntry();
		entry.setNode(node);
		node.setReturnType(type.getReturnType());
		identifier.setReturnType(type.getReturnType());
		
		try {
			this.table.enter(identifier.getText(), entry);
		} catch (SymbolTableException e) {
			throw new CheckerException(e.getMessage());
		}
		
		// Done
		if (type.getNodeType() == NodeType.TYPE) {
			logger.debug(String.format(
				"Variable declaration: name='%s' type='%s'", 
				identifier.getText(), 
				type.getReturnType().getName()
			));
		} else if (type.getNodeType() == NodeType.OBAMA_TYPE) {
			logger.debug(String.format(
				"Custom variable declaration: name='%s' type='%s'", 
				identifier.getText(), 
				type.getText()
			));
		}
	}
	
	/**
	 * @requires node.getChildCount() == 3
	 */
	public void visitConstDeclaration(TypedNode node) throws CheckerException {
		node.setNodeType(NodeType.CONST);
		
		Class<?> constType = ((TypedNode) node.getChild(0)).getReturnType();
		Class<?> valueType = ((TypedNode) node.getChild(2)).getReturnType();
		String id = ((TypedNode) node.getChild(1)).getText();

		if (!constType.equals(valueType)) {
			throw new CheckerException(String.format("Constant type '%s' does not match constant value '%s'", constType.getSimpleName(), valueType.getSimpleName()));
		}
		
		IdEntry entry = new IdEntry();
		entry.setNode(node);
		node.setReturnType(constType);
		
		try {
			this.table.enter(id, entry);
		} catch (SymbolTableException e) {
			throw new CheckerException(e.getMessage());
		}
		
		logger.debug(String.format(
			"Const declaration: name='%s' constType='%s' valueType='%s' value='%s'", 
			id, 
			constType.getName(), 
			valueType.getName(),
			node.getChild(2).getText()
		));
	}
	
	/**
	 * @requires node.getChildCount() == 0
	 */
	public void visitType(TypedNode node) throws CheckerException {
		checkArgument(node.getChildCount() == 0);
		String name = node.getText();
		
		// First, assume it is a custom class
		if (this.classes.contains(name)) {
			node.setNodeType(NodeType.OBAMA_TYPE);
			node.setReturnType(Object.class);
			
			logger.debug(String.format("Custom type: type='%s'", name));
			return;
		}
		
		// Second, assume it is a non-custom class
		Class<?> type = this.findType(name);
		node.setNodeType(NodeType.TYPE);
		node.setReturnType(type);
		
		logger.debug(String.format("Type: type='%s'", type.getName()));
	}
	
	/**
	 * @requires node.getChildCount() == 0
	 */
	public void visitThis(TypedNode node) {
		checkArgument(node.getChildCount() == 0);
		node.setNodeType(NodeType.FIELD_THIS);
	}
	
	/**
	 * @requires node.getChildCount() == 0
	 */
	public void visitGlobal(TypedNode node) {
		checkArgument(node.getChildCount() == 0);
		node.setNodeType(NodeType.FIELD_GLOBAL);
	}
	
	/**
	 * @requires node.getChildCount() == 0
	 */
	public void visitBuiltin(TypedNode node) {
		checkArgument(node.getChildCount() == 0);
		node.setNodeType(NodeType.FIELD_BUILTIN);
	}
	
	/**
	 * @requires node.getChildCount() == 2
	 */
	public void visitAssignExpression(TypedNode node) throws CheckerException {
		checkArgument(node.getChildCount() == 2);
		node.setNodeType(NodeType.ASSIGN);
		
		TypedNode left = (TypedNode) node.getChild(0);
		TypedNode right = (TypedNode) node.getChild(1);
		
		// It is only possible to assign to a variable
		if (left.getNodeType() != NodeType.VARIABLE && left.getNodeType() != NodeType.METHOD_PARAMETER) {
			throw new CheckerException("Left hand side of assignment must be a variable");
		}
		
		// Change its node type to LHS so it will not be loaded on the stack
		left.setNodeType(NodeType.ASSIGN_LHS);
		
		// Check the types
		node.setReturnType(this.verifyNodeTypeExpression(node));
	}
	
	public void visitLogicalOrExpression(TypedNode node) throws CheckerException {
		node.setNodeType(NodeType.EXPRESSION);
		node.setReturnType(boolean.class);
		this.checkType(this.verifyNodeTypeExpression(node), new Class<?>[] { boolean.class });
	}
	
	public void visitLogicalAndExpression(TypedNode node) throws CheckerException {
		node.setNodeType(NodeType.EXPRESSION);
		node.setReturnType(boolean.class);
		this.checkType(this.verifyNodeTypeExpression(node), new Class<?>[] { boolean.class });
	}
	
	public void visitBitwiseOrExpression(TypedNode node) throws CheckerException {
		node.setNodeType(NodeType.EXPRESSION);
		node.setReturnType(this.checkType(this.verifyNodeTypeExpression(node)));
	}
	
	public void visitBitwiseAndExpression(TypedNode node) throws CheckerException {
		node.setNodeType(NodeType.EXPRESSION);
		node.setReturnType(this.checkType(this.verifyNodeTypeExpression(node)));
	}
	
	public void visitGTExpression(TypedNode node) throws CheckerException {
		node.setNodeType(NodeType.EXPRESSION);
		node.setReturnType(boolean.class);
		this.checkType(this.verifyNodeTypeExpression(node));
	}
	
	public void visitLTExpression(TypedNode node) throws CheckerException {
		node.setNodeType(NodeType.EXPRESSION);
		node.setReturnType(boolean.class);
		this.checkType(this.verifyNodeTypeExpression(node));
	}
	
	public void visitGTEQExpression(TypedNode node) throws CheckerException {
		node.setNodeType(NodeType.EXPRESSION);
		node.setReturnType(boolean.class);
		this.checkType(this.verifyNodeTypeExpression(node));
	}
	
	public void visitLTEQExpression(TypedNode node) throws CheckerException {
		node.setNodeType(NodeType.EXPRESSION);
		node.setReturnType(boolean.class);
		this.checkType(this.verifyNodeTypeExpression(node));
	}
	
	public void visitEQExpression(TypedNode node) throws CheckerException {
		node.setNodeType(NodeType.EXPRESSION);
		node.setReturnType(boolean.class);
		this.checkType(this.verifyNodeTypeExpression(node));
	}
	
	public void visitNEQExpression(TypedNode node) throws CheckerException {
		node.setNodeType(NodeType.EXPRESSION);
		node.setReturnType(boolean.class);
		this.checkType(this.verifyNodeTypeExpression(node));
	}
	
	public void visitPlusExpression(TypedNode node) throws CheckerException {
		node.setNodeType(NodeType.EXPRESSION);
		node.setReturnType(this.checkType(this.verifyNodeTypeExpression(node)));
	}
	
	public void visitMinExpression(TypedNode node) throws CheckerException {
		node.setNodeType(NodeType.EXPRESSION);
		node.setReturnType(this.checkType(this.verifyNodeTypeExpression(node)));
	}
	
	public void visitMultExpression(TypedNode node) throws CheckerException {
		node.setNodeType(NodeType.EXPRESSION);
		node.setReturnType(this.checkType(this.verifyNodeTypeExpression(node)));
	}
	
	public void visitDivExpression(TypedNode node) throws CheckerException {
		node.setNodeType(NodeType.EXPRESSION);
		node.setReturnType(this.checkType(this.verifyNodeTypeExpression(node)));
	}
	
	public void visitModExpression(TypedNode node) throws CheckerException {
		node.setNodeType(NodeType.EXPRESSION);
		node.setReturnType(this.checkType(this.verifyNodeTypeExpression(node), new Class<?>[] { int.class }));
	}
	
	/**
	 * @requires node.getChildCount() == 1
	 */
	public void visitNotExpression(TypedNode node) throws CheckerException {
		checkNotNull(node.getChildCount() == 1);
		TypedNode expression = (TypedNode) node.getChild(0);
		
		this.checkType(expression.getReturnType(), new Class<?>[] { boolean.class });
		node.setNodeType(NodeType.EXPRESSION);
		node.setReturnType(boolean.class);
	}
	
	/**
	 * @requires node.getChildCount() == 0
	 */
	public void visitField(TypedNode node) throws CheckerException {
		checkArgument(node.getChildCount() == 0);
		String identifier = node.getText();
		int dot = identifier.indexOf('.');
		
		// First, assume it is an static accessible field
        if (dot > 0) {
	        String owner = identifier.substring(0, dot);
	        String field = identifier.substring(dot + 1);
	        Class<?> haystack = null;
	        
	        try {
	        	haystack = this.findType(owner);
	        } catch (Exception exception) { }
	        
	        if (haystack != null) {
		        try {
		            Field result = haystack.getField(field);
		            
		            // Construct info object
		            FieldInfo info = new FieldInfo();
		            info.name = field;
		            info.type = result.getType();
		            info.owner = haystack;
		            
		            // Set parameters
		            node.setReturnType(result.getType());
		            node.setNodeType(NodeType.FIELD);
		            node.setInfo(info);
		            
		            // Done
		            return;
		        } catch (NoSuchFieldException exception) {
		            throw new CheckerException(String.format(
		                "Field '%s' does not exist in '%s'",
		                field,
		                owner
		            ));
		        }
	        }
        }
        
        // Second, assume it is a static class
        String owner = identifier;
        Class<?> result = null;
        
        try {
        	result = this.findType(owner);
        } catch (Exception exception) { }
        
        if (result != null) {
        	// Construct info object
            FieldInfo info = new FieldInfo();
            info.name = identifier;
            info.owner = result;
            
            // Set parameters
            node.setReturnType(result);
            node.setNodeType(NodeType.FIELD_STATIC);
            node.setInfo(info);
            
            // Done
            return;
        }
		
		// Third, assume it is an defined variable
		IdEntry entry = this.table.retrieve(identifier);
		
		if (entry != null) {
			node.setInfo(entry.getNode().getInfo());
			node.setReturnType(entry.getNode().getReturnType());
			node.setNodeType(entry.getNode().getNodeType());
			return;
		}
		
		// And fail
		throw new CheckerException(String.format("Unreferenced identifier '%s'", identifier));
	}
	
	/**
	 * @requires node.getChildCount() == 1
	 */
	public void visitNewExpression(TypedNode node) {
		checkArgument(node.getChildCount() == 1);
		
		Class<?> type = ((TypedNode) node.getChild(0)).getReturnType();
		node.setNodeType(NodeType.NEW);
		node.setReturnType(type != null ? type : Object.class);
	}
	
	/**
	 * @requires node.getChildCount() == 1
	 */
	public void visitParenExpression(TypedNode node) {
		checkArgument(node.getChildCount() == 1);
		
		node.setNodeType(NodeType.PAREN);
		node.setReturnType(((TypedNode) node.getChild(0)).getReturnType());
	}
	
	/**
	 * @requires node.getChildCount() == 2
	 */
	public void visitLiteral(TypedNode node, Object value) {
		Class<?> type = (Class<?>) value; 
		node.setNodeType(NodeType.LITERAL);
		node.setReturnType(type);
		
		logger.debug(String.format(
			"Literal: type='%s' value='%s'", 
			type.getName(),
			value != null ? value.toString() : "<null>"
		));
	}

	/**
	 * @requires node.getChildCount() >= 1
	 */
	public void visitIfStatement(TypedNode node) throws CheckerException {
		checkArgument(node.getChildCount() >= 1);
		node.setInfo(new IfElseIfElseInfo());
	}

	/**
	 * @requires node.getChildCount() == 2
	 */
	public void visitIfStatementIf(TypedNode node) throws CheckerException {
		checkArgument(node.getChildCount() == 2);
		
		// Expression must be an boolean
		TypedNode condition = (TypedNode) node.getChild(0);
		checkType(condition.getReturnType(), new Class<?>[] { boolean.class });
		
		// Child as scope
		((TypedNode) node.getChild(1)).setNodeType(NodeType.SCOPE);
		
		logger.debug("If statement");
	}

	/**
	 * @requires node.getChildCount() == 2
	 */
	public void visitIfStatementElseIf(TypedNode node) throws CheckerException {
		checkArgument(node.getChildCount() == 2);

		// Expression must be an boolean
		TypedNode condition = (TypedNode) node.getChild(0);
		checkType(condition.getReturnType(), new Class<?>[] { boolean.class });
		
		// Child as scope
		((TypedNode) node.getChild(1)).setNodeType(NodeType.SCOPE);
		
		logger.debug("Else-if statement");
	}

	public void visitIfStatementElse(TypedNode node) throws CheckerException {
		node.setNodeType(NodeType.IF_ELSE);
		
		// Child as scope
		((TypedNode) node.getChild(0)).setNodeType(NodeType.SCOPE);
		
		logger.debug("Else statement");
	}

	/**
	 * @requires node.getChildCount() == 2
	 */
	public void visitWhileStatement(TypedNode node) throws CheckerException {
		checkArgument(node.getChildCount() == 2);
		node.setInfo(new WhileInfo());
		
		// Expression must be an boolean
		TypedNode condition = (TypedNode) node.getChild(0);
		checkType(condition.getReturnType(), new Class<?>[] { boolean.class });
		
		// Child as scope
		((TypedNode) node.getChild(1)).setNodeType(NodeType.SCOPE);
		
		logger.debug("While statement");
	}

	/**
	 * @requires node.getChildCount() == 4
	 */
	public void visitForStatement(TypedNode node) throws CheckerException {
		checkArgument(node.getChildCount() == 4);
		
		TypedNode variable = (TypedNode) node.getChild(0);
		checkType(variable.getReturnType());
		
		TypedNode condition = (TypedNode) node.getChild(1);
		checkType(condition.getReturnType(), new Class<?>[] { boolean.class });
		
		TypedNode expression = (TypedNode) node.getChild(2);
		checkType(expression.getReturnType(), new Class<?>[] { variable.getReturnType() });
		
		// Create info for the for-loop
		node.setInfo(new ForLoopInfo());
		
		// Child as scope
		((TypedNode) node.getChild(3)).setNodeType(NodeType.SCOPE);
		
		logger.debug(String.format("For loop: variableType='%s'", variable.getReturnType().getName()));
	}

	/**
	 * @requires node.getChildCount() >= 1
	 */
	public void visitSwitchStatementSwitch(TypedNode node) throws CheckerException {
		checkArgument(node.getChildCount() >= 1);
		
		node.setInfo(new SwitchInfo());
		logger.debug("Switch statement");
	}

	/**
	 * @requires node.getChildCount() == 1
	 */
	public void visitSwitchStatementCase(TypedNode node) throws CheckerException {
		checkArgument(node.getChildCount() == 1);
		node.setNodeType(NodeType.SWITCH_CASE);
		
		String text = node.getText();
		int literal = 0;
		
		// Parse the integer value of the literal. First assume it is an integer
		try {
			literal = Integer.parseInt(text);
		} catch (NumberFormatException exception) {
			// Next, assume it is an character if length of text == 1
			if (text != null && text.length() == 1) {
				literal = (int) text.charAt(0);
			} else {
				// Failed
				throw new CheckerException(String.format(
					"Literal '%s' not parsible as integer",
					text
				));
			}
		}
		
		// Check expression type
		this.checkType(((TypedNode) node.getParent().getChild(0)).getReturnType());
		
		// Check for duplicates
		SwitchInfo parentInfo = (SwitchInfo) ((TypedNode) node.getParent()).getInfo();
		
		if (parentInfo.literals.contains(literal)) {
			throw new CheckerException(String.format(
				"Duplicate literal '%s'", 
				text
			));
		}
		
		// Set info
		SwitchCaseInfo info = new SwitchCaseInfo();
		node.setInfo(info);
		info.value = literal;
		
		// Add literal as seen
		parentInfo.literals.add(literal);
		logger.debug(String.format("Switch case: value='%s'", literal));
		
		// And child as scope
		((TypedNode) node.getChild(0)).setNodeType(NodeType.SCOPE);
	}

	/**
	 * @requires node.getChildCount() == 1
	 */
	public void visitSwitchStatementDefault(TypedNode node) throws CheckerException {
		checkArgument(node.getChildCount() == 1);
		
		node.setInfo(new SwitchCaseInfo());
		node.setNodeType(NodeType.SWITCH_DEFAULT);
		logger.debug("Switch default");
		
		// And child as scope
		((TypedNode) node.getChild(0)).setNodeType(NodeType.SCOPE);
	}
	
	/*
	 * INLINE-IF EXPRESSION
	 */
	
	/**
	 * @requires node.getChildCount() == 3
	 */
	public void visitInlineIf(TypedNode node) throws CheckerException {
		checkNotNull(node.getChildCount() == 3);
		node.setInfo(new InlineIfInfo());
		
		// Check expression boolean
		TypedNode condition = (TypedNode) node.getChild(0);
		checkType(condition.getReturnType(), new Class<?>[] { boolean.class });
		
		// Results must be the same type
		Class<?> expression = (Class<?>) ((TypedNode) node.getChild(1)).getReturnType();
		Class<?> expressionOther = (Class<?>) ((TypedNode) node.getChild(2)).getReturnType();
		
		if (!expression.equals(expressionOther)) {
			throw new CheckerException(String.format(
				"Inline-if expression '%s' does not match '%s'",
				expression.getName(),
				expressionOther.getName()
			));
		}
		
		// Done
		node.setReturnType(expression);
		logger.debug(String.format(
			"Inline-if expression: expression='%s' expressionOther='%s'",
			expression.getName(),
			expressionOther.getName()
		));
	}
	
	/*
	 * CLASS HELPERS
	 */
	
	/**
	 * Check if a type is a primitive type
	 * 
	 * @param type Input type
	 * @return The input type
	 * @throws CheckerException If type is not a primitive type
	 * @requires type != null
	 */
	private Class<?> checkType(Class<?> type) throws CheckerException {
		checkNotNull(type);
		
		if (type.isPrimitive()) {
			return type;
		} else {
			throw new CheckerException(String.format(
				"Type '%s' not valid (primitives only)", 
				type.getName()
			));
		}
	}
	
	/**
	 * Check if a type is in a list of allowed types.
	 * 
	 * @param type Input type
	 * @param allowed List of allowed types
	 * @return The input type
	 * @throws CheckerException If type is not in the list of allowed types
	 */
	private Class<?> checkType(Class<?> type, Class<?>[] allowed) throws CheckerException {
		List<Class<?>> haystack = Lists.newArrayList(allowed);
		
		if (haystack.contains(type)) {
			return type;
		} else {
			throw new CheckerException(String.format(
				"Type '%s' not valid (valid is '%s')",
				type.getName(),
				ReflectionUtils.prettyTypesList(Lists.newArrayList(allowed))
			));
		}
	}
	
	/**
	 * Verify the types of an expression and return the compatible type under a
	 * specific operator. Throws an exception if types are not compatible, for
	 * example a String + Integer.
	 * 
	 * @param node Expression node
	 * @requires node.getChildCount() == 2
	 * @throws CheckerException If types are incompatible
	 */
	private Class<?> verifyNodeTypeExpression(TypedNode node) throws CheckerException {
		checkArgument(node.getChildCount() == 2);
		
		Class<?> left = ((TypedNode) node.getChild(0)).getReturnType();
		Class<?> right = ((TypedNode) node.getChild(1)).getReturnType();
		
		Class<?> result = this.matchTypes(left, right);
		
		if (result != null) {
			logger.debug(String.format(
				"Checked node types: left='%s' right='%s' operator='%s' result='%s'",
				left.getName(), 
				right.getName(), 
				node.getText(),
				result.getName()
			));
			
			return result;
		} else {
			throw new CheckerException(String.format(
				"Type '%s' not compatible with '%s' with operator '%s'", 
				left.getName(),
				right.getName(), 
				node.getText()
			));
		}
	}
	
	/**
	 * Match two types and return the compatible type. The compatible types
	 * are defined in a lookup table. For example, an integer and a character
	 * will result in a character. If two types are the same, the same type
	 * will be returned
	 * 
	 * @param left Left type
	 * @param right Right type
	 * @return Compatible type
	 * @requires left != null && right != null
	 */
	private Class<?> matchTypes(Class<?> left, Class<?> right) {
		checkNotNull(left);
		checkNotNull(right);
		
		// Same types match always
		if (left.equals(right)) return left;
		
		// Construct mapping table
		Map<LeftRightType, Class<?>> matches = Maps.newHashMap();		
		matches.put(new LeftRightType(int.class, char.class), char.class);

		// Find match, or null if none
		return matches.get(new LeftRightType(left, right));
	}
	
	/**
	 * Try to resolve an identifier to a type. If it fails, the exceptions will
	 * be passed to the caller. It takes the imports in account, which means that
	 * if you look for ArrayList and have imported java.util.* or the full path
	 * java.util.Arraylist, the correspond ArrayList will be returned.
	 * 
	 * @param identifier Name of the type
	 * @return Type corresponding to the identifier
	 * @throws CheckerException
	 */
	private Class<?> findType(String identifier) throws CheckerException {
		// Find the class via reflection
		try {
			return ReflectionUtils.findType(identifier, this.imports);
		} catch (IllegalArgumentException exception) {
			throw new CheckerException(exception.getMessage());
		} catch (ClassNotFoundException exception) {
			throw new CheckerException(exception.getMessage());
		}
	}
}
