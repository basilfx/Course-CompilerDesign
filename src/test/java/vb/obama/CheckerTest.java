package vb.obama;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import vb.obama.exceptions.CheckerException;

/**
 * Tests ObamaChecker.g
 * 
 * @version 1.0
 * @see ObamaChecker.g
 */
public class CheckerTest extends AbstractTest {
	/**
	 * Path to files, relative to project root
	 */
	private static String BASE_PATH = "/Users/basilfx/Desktop/Course-CompilerDesign/src/test/resources/tests/checker/";
	
	/*
	 * Imports 
	 */
	@Test
	public void testImports() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "Imports.obama"));
	}
	
	/*
	 * Constants
	 */
	@Test
	public void testConstants() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "Constants.obama"));
	}
	
	/*
	 * Methods
	 */
	@Test
	public void testMethod() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "Method.obama"));
	}
	
	@Test
	public void testMethodSingleParameter() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "MethodSingleParameter.obama"));
	}
	
	@Test
	public void testMethodMultipleParameters() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "MethodMultipleParameters.obama"));
	}
	
	@Test
	public void testMethodMixedParameters() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "MethodMixedParameters.obama"));
	}
	
	@Test
	public void testMethodMultipleMethods() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "MethodMultipleMethods.obama"));
	}
	
	@Test
	public void testMethodDuplicateNamesIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "MethodDuplicateNamesIncorrect.obama"));
	}
	
	@Test
	public void testMethodParameterUse() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "MethodParameterUse.obama"));
	}
	
	@Test(expected=CheckerException.class)
	public void testMethodParameterUseIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "MethodParameterUseIncorrect.obama"));
	}
	
	@Test
	public void testMethodReturn() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "MethodReturn.obama"));
	}
	
	@Test(expected=CheckerException.class)
	public void testMethodReturnIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "MethodReturnIncorrect.obama"));
	}
	
	/*
	 * Assignment
	 */
	@Test
	public void testAssignment() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "Assignment.obama"));
	}
	
	@Test(expected=CheckerException.class)
	public void testAssignmentIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "AssignmentIncorrect.obama"));
	}
	
	@Test
	public void testAssignmentMultiple() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "AssignmentMultiple.obama"));
	}
	
	@Test(expected=CheckerException.class)
	public void testAssignmentMultipleIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "AssignmentMultipleIncorrect.obama"));
	}
	
	/*
	 * Classes
	 */
	@Test
	public void testClass() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "Class.obama"));
	}
	
	@Test
	public void testClassMethodMix() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "ClassMethodMix.obama"));
	}
	
	/*
	 * Expressions
	 */
	@Test
	public void testExpressionLogicalOr() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "ExpressionLogicalOr.obama"));
	}
	
	@Test
	public void testExpressionLogicalAnd() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "ExpressionLogicalAnd.obama"));
	}
	
	@Test
	public void testExpressionRelational() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "ExpressionRelational.obama"));
	}
	
	@Test(expected=CheckerException.class)
	public void testExpressionRelationalIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "ExpressionRelationalIncorrect.obama"));
	}
	
	@Test
	public void testExpressionLowArithmetic() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "ExpressionLowArithmetic.obama"));
	}
	
	@Test
	public void testExpressionHighArithmetic() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "ExpressionHighArithmetic.obama"));
	}
	
	@Test
	public void testExpressionNegation() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "ExpressionNegation.obama"));
	}
	
	@Test
	public void testExpressionArithmeticOrder() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "ExpressionArithmeticOrder.obama"));
	}
	
	@Test
	public void testExpressionRelationalArithmeticOrder() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "ExpressionRelationalArithmeticOrder.obama"));
	}
	
	@Test
	public void testExpressionMultiple() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "ExpressionMultiple.obama"));
	}
	
	@Test(expected=CheckerException.class)
	public void testExpressionTypesIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "ExpressionTypesIncorrect.obama"));
	}
	
	/*
	 * If-else statements
	 */
	@Test
	public void testIfStatement() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "IfStatement.obama"));
	}
	
	@Test
	public void testIfElseStatement() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "IfElseStatement.obama"));
	}
	
	@Test
	public void testIfElseIfStatement() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "IfElseIfStatement.obama"));
	}
	
	@Test
	public void testIfElseIfElseStatement() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "IfElseIfElseStatement.obama"));
	}
	
	/*
	 * Switch statements
	 */
	@Test
	public void testSwitchCase() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "SwitchCase.obama"));
	}
	
	@Test
	public void testSwitchDefault() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "SwitchDefault.obama"));
	}
	
	@Test
	public void testSwitchCaseDefault() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "SwitchCaseDefault.obama"));
	}
	
	@Test
	public void testSwitchMixed() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "SwitchMixed.obama"));
	}
	
	@Test(expected=CheckerException.class)
	public void testSwitchMixedIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "SwitchMixedIncorrect.obama"));
	}
	
	@Test(expected=CheckerException.class)
	public void testSwitchMixedTypeIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "SwitchMixedTypeIncorrect.obama"));
	}
	
	@Test(expected=CheckerException.class)
	public void testSwitchDoubleLiterals() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "SwitchDoubleLiterals.obama"));
	}
	
	/*
	 * For-loop statements
	 */
	@Test
	public void testForLoop() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "ForLoop.obama"));
	}
	
	@Test(expected=CheckerException.class)
	public void testForLoopIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "ForLoopIncorrect.obama"));
	}
	
	/*
	 * While statements
	 */
	@Test
	public void testWhileLoop() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "WhileLoop.obama"));
	}
	
	@Test
	public void testWhileLoopForever() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "WhileLoopForever.obama"));
	}
	
	@Test(expected=CheckerException.class)
	public void testWhileLoopIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "WhileLoopIncorrect.obama"));
	}
	
	/*
	 * Built-ins
	 */
	@Test
	public void testBuiltIn() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "BuiltIn.obama"));
	}
	
	@Test
	public void testBuiltInAssign() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "BuiltInAssign.obama"));
	}
	
	@Test(expected=CheckerException.class)
	public void testBuiltInWrongMethodIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "BuiltInWrongMethodIncorrect.obama"));
	}
	
	@Test(expected=CheckerException.class)
	public void testBuiltInWrongParametersIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "BuiltInWrongParametersIncorrect.obama"));
	}
	
	@Test
	public void testBuiltInRead() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "BuiltInRead.obama"));
	}
	
	@Test
	public void testBuiltInReadExpression() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "BuiltInReadExpression.obama"));
	}
	
	@Test(expected=CheckerException.class)
	public void testBuiltInReadIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "BuiltInReadIncorrect.obama"));
	}
	
	@Test
	public void testBuiltInPrint() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "BuiltInPrint.obama"));
	}
	
	@Test
	public void testBuiltInPrintExpression() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "BuiltInPrintExpression.obama"));
	}
	
	@Test(expected=CheckerException.class)
	public void testBuiltInPrintIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker(BASE_PATH + "BuiltInPrintIncorrect.obama"));
	}
}
