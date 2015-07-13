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
 * @see ObamaParser.g
 */
public class ParserTest extends AbstractTest {
	/**
	 * Path to files, relative to project root
	 */
	private static String BASE_PATH = "/Users/basilfx/Desktop/Course-CompilerDesign/src/test/resources/tests/parser/";
	
	/**
	 * Empty file
	 */
	@Test
	public void testEmptyFile() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser(BASE_PATH + "EmptyFile.obama"));
	}
	
	/** 
	 * Imports 
	 */
	@Test
	public void testImports() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser(BASE_PATH + "Imports.obama"));
	}
	
	@Test
	public void testImportsIncorrect() throws RecognitionException, IOException {
		assertEquals(2, this.executeFileParser(BASE_PATH + "ImportsIncorrect.obama"));
	}
	
	/** 
	 * Methods 
	 */
	@Test
	public void testMethodSimple() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser(BASE_PATH + "MethodSimple.obama"));
	}
	
	@Test
	public void testMethodParameters() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser(BASE_PATH + "MethodParameters.obama"));
	}
	
	@Test
	public void testMethodMixedParameters() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser(BASE_PATH + "MethodMixedParameters.obama"));
	}
	
	/**
	 * Classes
	 */
	@Test
	public void testClass() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser(BASE_PATH + "Class.obama"));
	}
	
	@Test
	public void testClassIncorrect() throws RecognitionException, IOException {
		assertEquals(2, this.executeFileParser(BASE_PATH + "ClassIncorrect.obama"));
	}
	
	/**
	 * Arrays
	 */
	@Test
	public void testArraysParameters() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser(BASE_PATH + "ArraysParameters.obama"));
	}
	
	/**
	 * Return statements
	 */
	@Test
	public void testReturnMethod() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser(BASE_PATH + "ReturnMethod.obama"));
	}
	
	@Test
	public void testReturnClass() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser(BASE_PATH + "ReturnClass.obama"));
	}
	
	@Test
	public void testReturnClassIncorrect() throws RecognitionException, IOException {
		assertEquals(1, this.executeFileParser(BASE_PATH + "ReturnClassIncorrect.obama"));
	}
	
	@Test
	public void testReturnGlobalIncorrect() throws RecognitionException, IOException {
		assertEquals(1, this.executeFileParser(BASE_PATH + "ReturnGlobalIncorrect.obama"));
	}
	
	@Test
	public void testReturnRHSIncorrect() throws RecognitionException, IOException {
		assertEquals(1, this.executeFileParser(BASE_PATH + "ReturnRHSIncorrect.obama"));
	}
	
	/**
	 * Invocations
	 */
	@Test
	public void testInvokeWithout() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser(BASE_PATH + "InvokeWithout.obama"));
	}
	
	@Test
	public void testInvoke() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser(BASE_PATH + "Invoke.obama"));
	}
	
	@Test
	public void testInvokeNested() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser(BASE_PATH + "InvokeNested.obama"));
	}
	
	@Test
	public void testInvokeReturn() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser(BASE_PATH + "InvokeReturn.obama"));
	}
	
	/**
	 * For-loop
	 */
	@Test
	public void testForLoop() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser(BASE_PATH + "ForLoop.obama"));
	}
	
	@Test
	public void testForLoopIncorrect() throws RecognitionException, IOException {
		assertEquals(3, this.executeFileChecker(BASE_PATH + "ForLoopIncorrect.obama"));
	}
	
	@Test
	public void testForLoopVariableIncorrect() throws RecognitionException, IOException {
		assertEquals(4, this.executeFileChecker(BASE_PATH + "ForLoopVariableIncorrect.obama"));
	}
	
	/**
	 * If-else statements
	 */
	@Test
	public void testIfStatement() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser(BASE_PATH + "IfStatement.obama"));
	}
	
	@Test
	public void testIfElseStatement() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser(BASE_PATH + "IfElseStatement.obama"));
	}
	
	@Test
	public void testIfElseIfStatement() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser(BASE_PATH + "IfElseIfStatement.obama"));
	}
	
	@Test
	public void testIfElseIfElseStatement() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser(BASE_PATH + "IfElseIfElseStatement.obama"));
	}
	
	/**
	 * Switch statements
	 */
	@Test
	public void testSwitchCase() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser(BASE_PATH + "SwitchCase.obama"));
	}
	
	@Test
	public void testSwitchDefault() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser(BASE_PATH + "SwitchDefault.obama"));
	}
	
	@Test
	public void testSwitchCaseDefault() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser(BASE_PATH + "SwitchCaseDefault.obama"));
	}
	
	/**
	 * While-loop
	 */
	@Test
	public void testWhileLoop() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser(BASE_PATH + "WhileLoop.obama"));
	}
	
	/** 
	 * Comments 
	 */
	@Test
	public void testComments() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser(BASE_PATH + "Comments.obama"));
	}
	
	@Test
	public void testCommentsImports() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser(BASE_PATH + "CommentsImports.obama"));
	}
	
	@Test
	public void testCommentsMixed() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser(BASE_PATH + "CommentsMixed.obama"));
	}
	
	@Test
	public void testCommentsIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser(BASE_PATH + "CommentsIncorrect.obama"));
	}
	
	/**
	 * Globals 
	 */
	@Test
	public void testGlobalsVars() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser(BASE_PATH + "GlobalsVars.obama"));
	}
	
	@Test
	public void testGlobalsConsts() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileParser(BASE_PATH + "GlobalsConsts.obama"));
	}
	
	@Test
	public void testGlobalsVarsIncorrect() throws RecognitionException, IOException {
		assertEquals(1, this.executeFileParser(BASE_PATH + "GlobalsVarsIncorrect.obama"));
	}
	
	@Test
	public void testGlobalsConstsIncorrect() throws RecognitionException, IOException {
		assertEquals(1, this.executeFileParser(BASE_PATH + "GlobalsConstsIncorrect.obama"));
	}
}