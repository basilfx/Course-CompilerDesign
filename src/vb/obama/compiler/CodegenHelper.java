package vb.obama.compiler;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import sun.tools.tree.Node;
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
import vb.obama.exceptions.CodegenException;
import vb.obama.util.ReflectionUtils;

import com.google.common.collect.Lists;

/**
 * 
 * @verion 1.3
 */
public class CodegenHelper {
	/**
	 * Message logger
	 */
	private static final Logger logger = Logger.getLogger(CodegenHelper.class.getName());
	
	/**
	 * Holds all generated classes
	 */
	private List<ClassInfo> classes;
	
	/**
	 * Construct a new CodegenHelper
	 */
	public CodegenHelper() {
		this.classes = Lists.newArrayList();
	}
	
	public void visitContentStart(TypedNode node) throws CodegenException {
		this.visitClassStart(node);
	}
	
	public void visitContentEnd(TypedNode node) throws CodegenException {
		this.visitClassEnd(node);
	}
	
	public void visitClassStart(TypedNode node) throws CodegenException {
		ClassInfo info = (ClassInfo) node.getInfo();
		this.classes.add(info);
		
		// Construct a new class file writer
		info.classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		
		// Open a class
		info.classWriter.visit(
			Opcodes.V1_6, 
			info.modifiers, 
			info.name, 
			null, 
			Type.getInternalName(info.extending), 
			null
		);
		
		// Debug information
		info.classWriter.visitSource(info.file, null);

		Method instantizer = Method.getMethod("void <clinit> ()");
		GeneratorAdapter generator = new GeneratorAdapter(
			Opcodes.ACC_STATIC,
			instantizer,
			null,
			null,
			info.classWriter
		);
		generator.visitCode();
		generator.visitLineNumber(node.token.getLine(), generator.mark());
		
		// Save instance to add statics
		info.generator = generator;
		
		// Add implicit constructor
		Method constructor = Method.getMethod("void <init> ()");
		generator = new GeneratorAdapter(
			Opcodes.ACC_PUBLIC, 
			constructor, 
			null, 
			null, 
			info.classWriter
		);
		generator.visitCode();
		generator.visitLineNumber(node.token.getLine(), generator.mark());
		 
		generator.loadThis();
		generator.invokeConstructor(Type.getType(Object.class), constructor);
		generator.returnValue();
		generator.endMethod();
	}
	
	public void visitClassEnd(TypedNode node) {
		ClassInfo info = this.findClassInfo(node);
		
		// Close static instantizer
		info.generator.returnValue();
		info.generator.endMethod();
		
		// Close the class
		info.classWriter.visitEnd();
	}
	
	public void visitMethodDeclaration(TypedNode node) throws CodegenException {
		MethodInfo info = (MethodInfo) node.getInfo();
		ClassWriter classWriter = this.findClassInfo(node).classWriter;
		
		// Create a method
		Method method = info.toASM();
		GeneratorAdapter generator = new GeneratorAdapter(
			info.modifiers,
			method,
			null,
			null,
			classWriter
		);
		
		// Set the ASM generator
		info.generator = generator;
	}
	
	public void visitMethodContentStart(TypedNode node) throws CodegenException {
		GeneratorAdapter generator = findGeneratorAdapter(node);
		
		// Open a method
		generator.visitCode();
		generator.visitLineNumber(node.token.getLine(), generator.mark());
	}
	
	public void visitMethodContentEnd(TypedNode node) throws CodegenException {
		GeneratorAdapter generator = findGeneratorAdapter(node);
		MethodInfo info = findMethodInfo(node);
		
		// Close a method
		generator.returnValue();
		generator.endMethod();
		
		// Not needed anymore
		info.generator = null;
		
		logger.debug(String.format(
			"Generated method '%s'",
			info.name
		));
	}
	
	public void visitMethodReturn(TypedNode node) throws CodegenException {
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		generator.returnValue();
	}

	public void visitMethodCall(TypedNode node) throws CodegenException {
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		TypedNode field = (TypedNode) node.getChild(0);
		boolean allowPop = true;
		
		MethodCallInfo info = (MethodCallInfo) node.getInfo();
		ClassInfo classInfo = findClassInfo(node);
		
		switch (field.getNodeType()) {
			case VARIABLE:
				generator.invokeVirtual(
					Type.getType(info.owner),
					new Method(
						info.method, 
						Type.getType(info.returnType), 
						ReflectionUtils.toTypeArray(info.parameters)
					)
				);
				
				break;
			case FIELD:
				generator.invokeVirtual(
					Type.getType(info.owner),
					new Method(
						info.method, 
						Type.getType(info.returnType), 
						ReflectionUtils.toTypeArray(info.parameters)
					)
				);
				
				break;
			case FIELD_STATIC:
				generator.invokeStatic(
					Type.getType(info.owner),
					new Method(
						info.method, 
						Type.getType(info.returnType), 
						ReflectionUtils.toTypeArray(info.parameters)
					)
				);
				break;
			case FIELD_GLOBAL:
				generator.invokeStatic(
					Type.getObjectType(classInfo.name),
					new Method(
						info.method, 
						Type.getType(info.returnType), 
						ReflectionUtils.toTypeArray(info.parameters)
					)
				);
				break;
			case FIELD_THIS:
				break;
			
			case FIELD_BUILTIN:
				TypedNode parent = (TypedNode) node.getParent();
				
				// Built-ins are handled in a separate class because they generate
				// a lot of code. To keep it clean, it has been grouped with more
				// similar methods.
				BuiltIns.generate(
					generator, 
					info,
					parent.getNodeType() == NodeType.ASSIGN || parent.getNodeType() == NodeType.EXPRESSION
				);

				break;
		}
		
		// Only pop if not a void and not in expression
		if (info.returnType != void.class)
			popOrNot(node);
	}

	public void visitVarDeclaration(TypedNode node) throws CodegenException {
		checkArgument(node.getNodeType() == NodeType.VARIABLE);
		
		// Add variable
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		VariableInfo info = (VariableInfo) node.getInfo();
		info.asmIndex = generator.newLocal(Type.getType(node.getReturnType()));
		
		// Init it to default
		if (node.getReturnType().isPrimitive()) {
			generator.push(0);
		} else {
			String empty = null;
			generator.push(empty);
		}
		
		// And save it
		generator.storeLocal(info.asmIndex);
	}

	public void visitConstDeclaration(TypedNode node) throws CodegenException {
		ClassInfo info = this.findClassInfo(node);
		
		info.generator.putStatic(
			Type.getObjectType(info.name),
			node.getChild(1).getText(),
			Type.getType(node.getReturnType())
		);
	}

	public void visitAssignExpression(TypedNode node) throws CodegenException {
		TypedNode left = (TypedNode) node.getChild(0);

		TypedNode parent = (TypedNode) node.getParent();
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		
		// In case of multiple assignment, duplicate the right hand side value
		if (parent.getNodeType() == NodeType.ASSIGN) {
			generator.dup();
		}
		
		// And assign it
		if (left.getNodeType() == NodeType.ASSIGN_LHS) {
			VariableInfo info = (VariableInfo) left.getInfo();
			generator.storeLocal(info.asmIndex, Type.getType(node.getReturnType()));
		}
	}
	
	public void visitNewExpression(TypedNode node) {
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		generator.newInstance(Type.getType(node.getReturnType()));
		generator.dup();
		
		popOrNot(node);
	}

	public void visitBitwiseOrExpression(TypedNode node) throws CodegenException {
		this.visitArithmeticOperator(node, GeneratorAdapter.OR);
	}

	public void visitBitwiseAndExpression(TypedNode node) throws CodegenException {
		this.visitArithmeticOperator(node, GeneratorAdapter.AND);
	}
	
	public void visitLogicalOrExpression(TypedNode node) throws CodegenException {
		this.visitArithmeticOperator(node, GeneratorAdapter.OR);
	}
	
	public void visitLogicalAndExpression(TypedNode node) throws CodegenException {
		this.visitArithmeticOperator(node, GeneratorAdapter.AND);
	}

	public void visitGTExpression(TypedNode node) throws CodegenException {
		this.visitBinaryOperator(node, GeneratorAdapter.GT);
	}

	public void visitLTExpression(TypedNode node) throws CodegenException {
		this.visitBinaryOperator(node, GeneratorAdapter.LT);
	}

	public void visitGTEQExpression(TypedNode node) throws CodegenException {
		this.visitBinaryOperator(node, GeneratorAdapter.GE);
	}

	public void visitLTEQExpression(TypedNode node) throws CodegenException {
		this.visitBinaryOperator(node, GeneratorAdapter.LE);
	}

	public void visitEQExpression(TypedNode node) throws CodegenException {
		this.visitBinaryOperator(node, GeneratorAdapter.EQ);
	}

	public void visitNEQExpression(TypedNode node) throws CodegenException {
		this.visitBinaryOperator(node, GeneratorAdapter.NE);
	}

	public void visitPlusExpression(TypedNode node) throws CodegenException {
		this.visitArithmeticOperator(node, GeneratorAdapter.ADD);
	}

	public void visitMinExpression(TypedNode node) throws CodegenException {
		this.visitArithmeticOperator(node, GeneratorAdapter.SUB);
	}

	public void visitMultExpression(TypedNode node) throws CodegenException {
		this.visitArithmeticOperator(node, GeneratorAdapter.MUL);
	}

	public void visitDivExpression(TypedNode node) throws CodegenException {
		this.visitArithmeticOperator(node, GeneratorAdapter.DIV);
	}

	public void visitModExpression(TypedNode node) throws CodegenException {
		this.visitArithmeticOperator(node, GeneratorAdapter.REM);
	}

	public void visitNotExpression(TypedNode node) throws CodegenException {
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		generator.not();
		popOrNot(node);
	}

	/*
	 * FIELDS
	 */
	
	public void visitField(TypedNode node) throws CodegenException {
		GeneratorAdapter generator = findGeneratorAdapter(node);
		
		if (node.getNodeType() == NodeType.VARIABLE) {
			VariableInfo info = (VariableInfo) node.getInfo();
			generator.loadLocal(info.asmIndex);
		} else if (node.getNodeType() == NodeType.CONST) {
			ClassInfo info = findClassInfo(node);
			
			generator.getStatic(
				Type.getObjectType(info.name),
				node.getText(),
				Type.getType(node.getReturnType())
			);
		} else if (node.getNodeType() == NodeType.FIELD) {
			FieldInfo info = (FieldInfo) node.getInfo();
			
			generator.getStatic(
				Type.getType(info.owner),
				info.name,
				Type.getType(info.type)
			);
		} else if (node.getNodeType() == NodeType.METHOD_PARAMETER) {
			// ASM library handles parameter management by itself. We only have
			// to specify the number of which parameter to use.
			ParameterInfo info = (ParameterInfo) node.getInfo();
			generator.loadArg(info.index);
		}
		
		popOrNot(node);
	}
	
	public void visitThis(TypedNode node) throws CodegenException {
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		generator.loadThis();
	}
	
	public void visitGlobal(TypedNode node) throws CodegenException {
		
	}
	
	public void visitBuiltin(TypedNode node) throws CodegenException {
		
	}

	public void visitLiteral(TypedNode node, Object value) throws CodegenException {
		TypedNode parent = (TypedNode) node.getParent();
		GeneratorAdapter generator = null;
		
		if (parent.getNodeType() == NodeType.CONST) {
			// Constants can only be declared in the class
			generator = findClassInfo(node).generator;
		} else {
			// Others only in methods
			generator = findMethodInfo(node).generator;
		}
		
		Class<?> type = node.getReturnType();
		
		if (type.equals(int.class)) {
			generator.push(Integer.parseInt((String) value));
		} else if (type.equals(boolean.class)) {
			generator.push(((Boolean) value).booleanValue());
		} else if (type.equals(char.class)) {
			generator.push(Character.getNumericValue(((String) value).charAt(0)));
		} else if (type.equals(String.class)) {
			generator.push((String) value);
		} else {
			throw new CodegenException("Unsupported literal");
		}
	}

	public void visitIfStatementStart(TypedNode node) throws CodegenException {
		IfElseIfElseInfo info = (IfElseIfElseInfo) node.getInfo();
		
		// Go to end of statements
		info.end = new Label();
		info.next = new Label();
	}
	
	public void visitIfStatementEnd(TypedNode node) throws CodegenException {
		IfElseIfElseInfo info = (IfElseIfElseInfo) node.getInfo();
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		
		// Go to end of statements
		generator.mark(info.end);
		
		// If there are no else-if-statements or else-statements, then stitch
		// next as the ending node too
		if (node.getChildCount() == 1) generator.mark(info.next);
	}
	
	public void visitIfStatementIfStart(TypedNode node) throws CodegenException {
		TypedNode parent = (TypedNode) node.getParent();
		IfElseIfElseInfo parentInfo = (IfElseIfElseInfo) parent.getInfo();
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		
		// Jump to next statement if evaluated false
		generator.ifZCmp(GeneratorAdapter.EQ, parentInfo.next);
	}
	
	public void visitIfStatementIfEnd(TypedNode node) throws CodegenException {
		TypedNode parent = (TypedNode) node.getParent();
		IfElseIfElseInfo parentInfo = (IfElseIfElseInfo) parent.getInfo();
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		
		// Go to end of statements
		generator.goTo(parentInfo.end);
	}

	public void visitIfStatementElseIf(TypedNode node) throws CodegenException {
		TypedNode parent = (TypedNode) node.getParent();
		IfElseIfElseInfo parentInfo = (IfElseIfElseInfo) parent.getInfo();
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		
		generator.mark(parentInfo.next);
		parentInfo.next = new Label();
	}
	
	public void visitIfStatementElseIfStart(TypedNode node) throws CodegenException {
		// Stitch all endings to each other
		TypedNode parent = (TypedNode) node.getParent();
		IfElseIfElseInfo parentInfo = (IfElseIfElseInfo) parent.getInfo();
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		
		// Jump to next statement if evaluated false
		generator.ifZCmp(GeneratorAdapter.EQ, parentInfo.next);
	}
	
	public void visitIfStatementElseIfEnd(TypedNode node) throws CodegenException {
		TypedNode parent = (TypedNode) node.getParent();
		IfElseIfElseInfo parentInfo = (IfElseIfElseInfo) parent.getInfo();
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		
		// Go to end of statements
		generator.goTo(parentInfo.end);
	}

	public void visitIfStatementElse(TypedNode node) throws CodegenException {
		// Stitch all endings to each other
		TypedNode parent = (TypedNode) node.getParent();
		IfElseIfElseInfo parentInfo = (IfElseIfElseInfo) parent.getInfo();
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		
		generator.mark(parentInfo.next);
	}

	public void visitWhileStatement(TypedNode node) throws CodegenException {
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		WhileInfo info = (WhileInfo) node.getInfo();
		
		info.start = new Label();
		info.end = new Label();
		
		generator.mark(info.start);
	}
	
	public void visitWhileStatementStart(TypedNode node) throws CodegenException {
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		WhileInfo info = (WhileInfo) node.getInfo();
		
		generator.ifZCmp(GeneratorAdapter.EQ, info.end);
	}
	
	public void visitWhileStatementEnd(TypedNode node) throws CodegenException {
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		WhileInfo info = (WhileInfo) node.getInfo();
		
		generator.goTo(info.start);
		generator.mark(info.end);
	}

	public void visitForStatementStart(TypedNode node) throws CodegenException {
		ForLoopInfo info = (ForLoopInfo) node.getInfo();
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		
		info.compare = new Label();
		info.end = new Label();
		info.increment = new Label();
		info.start = new Label();
		
		// Got to the code
		generator.goTo(info.start);
		
		// Mark where our expression is evaluated
		generator.mark(info.compare);
	}
	
	public void visitForStatementCompare(TypedNode node) throws CodegenException {
		ForLoopInfo info = (ForLoopInfo) node.getInfo();
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		
		// Jump to end if expression is false
		generator.ifZCmp(GeneratorAdapter.EQ, info.end);
		generator.goTo(info.start);
		
		// Mark where our variable is incremented
		generator.mark(info.increment);
	}
	
	public void visitForStatementIncrement(TypedNode node) throws CodegenException {
		ForLoopInfo info = (ForLoopInfo) node.getInfo();
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		
		// Mark where our inner program is evaluated
		generator.goTo(info.compare);
		generator.mark(info.start);
	}

	public void visitForStatementEnd(TypedNode node) throws CodegenException {
		ForLoopInfo info = (ForLoopInfo) node.getInfo();
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		
		generator.goTo(info.increment);
		generator.mark(info.end);
	}
	
	public void visitSwitchStatementSwitchStart(TypedNode node) throws CodegenException {
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		SwitchInfo info = (SwitchInfo) node.getInfo();
		info.end = new Label();
		
		List<Label> switchCases = Lists.newArrayList();
		Label defaultCase = null;
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		
		// Walk through each of the nodes
		for (int i = 1; i < node.getChildCount(); i++) {
			TypedNode caseNode = (TypedNode) node.getChild(i);
			SwitchCaseInfo caseInfo = (SwitchCaseInfo) caseNode.getInfo();
			caseInfo.label = new Label();
			
			if (caseNode.getNodeType() == NodeType.SWITCH_DEFAULT) {
				defaultCase = caseInfo.label;
			} else {
				min = Math.min(caseInfo.value, min);
				max = Math.max(caseInfo.value, max);
				switchCases.add(caseInfo.label);
			}
		}
		
		// Generate instruction
		generator.visitTableSwitchInsn(
			min, 
			max, 
			defaultCase, 
			switchCases.toArray(new Label[switchCases.size()])
		);
	}
	
	public void visitSwitchStatementSwitchEnd(TypedNode node) throws CodegenException {
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		SwitchInfo info = (SwitchInfo) node.getInfo();
		generator.mark(info.end);
	}

	public void visitSwitchStatementCaseStart(TypedNode node) throws CodegenException {
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		SwitchCaseInfo info = (SwitchCaseInfo) node.getInfo();
		
		// Create switch statement
		generator.mark(info.label);
	}
	
	public void visitSwitchStatementCaseEnd(TypedNode node) throws CodegenException {
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		SwitchInfo parentInfo = (SwitchInfo) ((TypedNode) node.getParent()).getInfo();
		
		// Create switch statement
		generator.goTo(parentInfo.end);
	}

	public void visitSwitchStatementDefault(TypedNode node) throws CodegenException {
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		SwitchCaseInfo info = (SwitchCaseInfo) node.getInfo();
		
		// Create switch statement
		generator.mark(info.label);
	}
	
	/*
	 * INLINE-IF EXPRESSION
	 */
	
	public void visitInlineIfStart(TypedNode node) {
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		InlineIfInfo info = (InlineIfInfo) node.getInfo();
		
		// Construct markers
		info.end = new Label();
		info.other = new Label();
		
		// Execute comparision which, if false, evaluates the other expression
		generator.ifZCmp(GeneratorAdapter.EQ, info.other);
	}
	
	public void visitInlineIfOther(TypedNode node) {
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		InlineIfInfo info = (InlineIfInfo) node.getInfo();
		
		// Go to the end
		generator.goTo(info.end);
		
		// Create new label
		generator.mark(info.other);
	}
	
	public void visitInlineIfEnd(TypedNode node) {
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		InlineIfInfo info = (InlineIfInfo) node.getInfo();
		
		// Create new label
		generator.mark(info.end);
		
		popOrNot(node);
	}
	
	/*
	 * BINARY AND ARITHMETIC HELPERS
	 */
	
	private void visitArithmeticOperator(TypedNode node, int operator) {
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		generator.math(operator, Type.getType(node.getReturnType()));
		
		popOrNot(node);
	}
	
	private void visitBinaryOperator(TypedNode node, int operator) {
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		
		Label labelTrue = new Label();
		Label labelDone = new Label();
		
		generator.ifCmp(Type.getType(node.getReturnType()), operator, labelTrue);
		generator.push(false);
		generator.goTo(labelDone);
		generator.mark(labelTrue);
		generator.push(true);
		generator.mark(labelDone);
		
		popOrNot(node);
	}
	
	/*
	 * CLASS HELPERS
	 */
	
	/**
	 * Determine if the result of an expression should be popped or not, and pop
	 * the result if required.
	 * 
	 * A result should be discarded if the direct parent of an expression is a
	 * scope, method, class and the expression is not assign expression.
	 * 
	 * @param node Node to check
	 */
	private void popOrNot(TypedNode node) {
		GeneratorAdapter generator = this.findGeneratorAdapter(node);
		TypedNode parent = (TypedNode) node.getParent();
		boolean condition = false;
		
		condition = condition || parent.getNodeType() == NodeType.METHOD;
		condition = condition || parent.getNodeType() == NodeType.CLASS;
		condition = condition || parent.getNodeType() == NodeType.SCOPE;
				
		// Pop the result
		if (condition) {
			generator.pop();
		}
	}
	
	/**
	 * Utility method. Finds the first available ClassInfo that is associated
	 * with the class the node is child of.
	 * 
	 * @param node 
	 * @return first available ClassInfo
	 * @requires node != null && node(.getParent()*).getNodeType() == NodeType.CLASS
	 */
	private ClassInfo findClassInfo(TypedNode node) {
		TypedNode current = checkNotNull(node);
		
		while (true) {
			if (current == null) {
				return null;
			} else if (current.getNodeType() == NodeType.CLASS) {
				break;
			}
			
			current = (TypedNode) current.getParent();
		}
		
		return (ClassInfo) current.getInfo();
	}
	
	/**
	 * Utility method. Finds the first available GeneratorAdapter that is associated
	 * with a method.
	 * 
	 * @param node 
	 * @return first available GeneratorAdapter
	 * @requires node != null && node(.getParent()*).getNodeType() == NodeType.METHOD
	 */
	private GeneratorAdapter findGeneratorAdapter(TypedNode node) {
		return this.findMethodInfo(node).generator;
	}
	
	/**
	 * Utility method. Finds the first available MethodInfo that is associated
	 * with a method.
	 * 
	 * @param node 
	 * @return first available MethodInfo
	 * @requires node != null && node(.getParent()*).getNodeType() == NodeType.METHOD
	 */
	private MethodInfo findMethodInfo(TypedNode node) {
		TypedNode current = checkNotNull(node);
		
		while (true) {
			if (current == null) {
				return null;
			} else if (current.getNodeType() == NodeType.METHOD) {
				break;
			}
			
			current = (TypedNode) current.getParent();
		}
		
		return (MethodInfo) current.getInfo();
	}
	
	/**
	 * Generates all files that can be generated from the source. Overwrites 
	 * existing files.
	 * 
	 * @param path Output path
	 * @throws IOException
	 * @requires path != null
	 */
	public List<File> toClasses(String path) throws IOException {
		checkNotNull(path);
		
		// Generate result list
		List<File> result = Lists.newArrayList();
		
		// Walk throuh all defined classes
		for (ClassInfo info : this.classes) {
			ClassWriter classWriter = info.classWriter;

			// Generate byte code
			byte[] content = classWriter.toByteArray();
			File file = new File(path, info.name + ".class");
			
			// Remove old files
			if (file.exists()) {
				file.delete();
				
				logger.info(String.format(
					"Overwriting existing file '%s'",
					file.toString()
				));
			}
			
			// Write content
			FileOutputStream stream = new FileOutputStream(file);
			stream.write(content);
			stream.close();
			
			// Add it to the list of files
			result.add(file);
			
			// Done
			logger.info(String.format("Written %d bytes to '%s'", content.length, file.toString()));
		}

		// Done
		logger.debug("Class export completed");
		return result;
	}
}
