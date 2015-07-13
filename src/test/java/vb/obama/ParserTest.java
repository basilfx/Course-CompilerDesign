package vb.obama;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import vb.obama.exceptions.CheckerException;

/**
 * Tests ObamaParser.g
 * 
 * @version 1.0
 * @see vb/obama/antlr/ObamaParser.g
 */
public class ParserTest extends AbstractTest {
	
	/**
	 * Empty file
	 */
	@Test
	public void testEmptyFile() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser("parser/EmptyFile.obama"));
	}
	
	/** 
	 * Imports 
	 */
	@Test
	public void testImports() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser("parser/Imports.obama"));
	}
	
	@Test
	public void testImportsIncorrect() throws RecognitionException, IOException {
		assertEquals(2, this.executeFileParser("parser/ImportsIncorrect.obama"));
	}
	
	/** 
	 * Methods 
	 */
	@Test
	public void testMethodSimple() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser("parser/MethodSimple.obama"));
	}
	
	@Test
	public void testMethodParameters() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser("parser/MethodParameters.obama"));
	}
	
	@Test
	public void testMethodMixedParameters() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser("parser/MethodMixedParameters.obama"));
	}
	
	/**
	 * Classes
	 */
	@Test
	public void testClass() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser("parser/Class.obama"));
	}
	
	@Test
	public void testClassIncorrect() throws RecognitionException, IOException {
		assertEquals(2, this.executeFileParser("parser/ClassIncorrect.obama"));
	}
	
	/**
	 * Arrays
	 */
	@Test
	public void testArraysParameters() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser("parser/ArraysParameters.obama"));
	}
	
	/**
	 * Return statements
	 */
	@Test
	public void testReturnMethod() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser("parser/ReturnMethod.obama"));
	}
	
	@Test
	public void testReturnClass() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser("parser/ReturnClass.obama"));
	}
	
	@Test
	public void testReturnClassIncorrect() throws RecognitionException, IOException {
		assertEquals(1, this.executeFileParser("parser/ReturnClassIncorrect.obama"));
	}
	
	@Test
	public void testReturnGlobalIncorrect() throws RecognitionException, IOException {
		assertEquals(1, this.executeFileParser("parser/ReturnGlobalIncorrect.obama"));
	}
	
	@Test
	public void testReturnRHSIncorrect() throws RecognitionException, IOException {
		assertEquals(1, this.executeFileParser("parser/ReturnRHSIncorrect.obama"));
	}
	
	/**
	 * Invocations
	 */
	@Test
	public void testInvokeWithout() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser("parser/InvokeWithout.obama"));
	}
	
	@Test
	public void testInvoke() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser("parser/Invoke.obama"));
	}
	
	@Test
	public void testInvokeNested() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser("parser/InvokeNested.obama"));
	}
	
	@Test
	public void testInvokeReturn() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser("parser/InvokeReturn.obama"));
	}
	
	/**
	 * For-loop
	 */
	@Test
	public void testForLoop() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser("parser/ForLoop.obama"));
	}
	
	@Test
	public void testForLoopIncorrect() throws RecognitionException, IOException {
		assertEquals(3, this.executeFileChecker("parser/ForLoopIncorrect.obama"));
	}
	
	@Test
	public void testForLoopVariableIncorrect() throws RecognitionException, IOException {
		assertEquals(4, this.executeFileChecker("parser/ForLoopVariableIncorrect.obama"));
	}
	
	/**
	 * If-else statements
	 */
	@Test
	public void testIfStatement() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser("parser/IfStatement.obama"));
	}
	
	@Test
	public void testIfElseStatement() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser("parser/IfElseStatement.obama"));
	}
	
	@Test
	public void testIfElseIfStatement() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser("parser/IfElseIfStatement.obama"));
	}
	
	@Test
	public void testIfElseIfElseStatement() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser("parser/IfElseIfElseStatement.obama"));
	}
	
	/**
	 * Switch statements
	 */
	@Test
	public void testSwitchCase() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser("parser/SwitchCase.obama"));
	}
	
	@Test
	public void testSwitchDefault() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser("parser/SwitchDefault.obama"));
	}
	
	@Test
	public void testSwitchCaseDefault() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser("parser/SwitchCaseDefault.obama"));
	}
	
	/**
	 * While-loop
	 */
	@Test
	public void testWhileLoop() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser("parser/WhileLoop.obama"));
	}
	
	/** 
	 * Comments 
	 */
	@Test
	public void testComments() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser("parser/Comments.obama"));
	}
	
	@Test
	public void testCommentsImports() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser("parser/CommentsImports.obama"));
	}
	
	@Test
	public void testCommentsMixed() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser("parser/CommentsMixed.obama"));
	}
	
	@Test
	public void testCommentsIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser("parser/CommentsIncorrect.obama"));
	}
	
	/**
	 * Globals 
	 */
	@Test
	public void testGlobalsVars() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser("parser/GlobalsVars.obama"));
	}
	
	@Test
	public void testGlobalsConsts() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser("parser/GlobalsConsts.obama"));
	}
	
	@Test
	public void testGlobalsVarsIncorrect() throws RecognitionException, IOException {
		assertEquals(1, this.executeFileParser("parser/GlobalsVarsIncorrect.obama"));
	}
	
	@Test
	public void testGlobalsConstsIncorrect() throws RecognitionException, IOException {
		assertEquals(1, this.executeFileParser("parser/GlobalsConstsIncorrect.obama"));
	}
}