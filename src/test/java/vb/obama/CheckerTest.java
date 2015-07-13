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
 * @see vb/obama/antlr/ObamaChecker.g
 */
public class CheckerTest extends AbstractTest {
	/*
	 * Imports 
	 */
	@Test
	public void testImports() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/Imports.obama"));
	}
	
	/*
	 * Constants
	 */
	@Test
	public void testConstants() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/Constants.obama"));
	}
	
	/*
	 * Methods
	 */
	@Test
	public void testMethod() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/Method.obama"));
	}
	
	@Test
	public void testMethodSingleParameter() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/MethodSingleParameter.obama"));
	}
	
	@Test
	public void testMethodMultipleParameters() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/MethodMultipleParameters.obama"));
	}
	
	@Test
	public void testMethodMixedParameters() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/MethodMixedParameters.obama"));
	}
	
	@Test
	public void testMethodMultipleMethods() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/MethodMultipleMethods.obama"));
	}
	
	@Test
	public void testMethodDuplicateNamesIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/MethodDuplicateNamesIncorrect.obama"));
	}
	
	@Test
	public void testMethodParameterUse() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/MethodParameterUse.obama"));
	}
	
	@Test(expected=CheckerException.class)
	public void testMethodParameterUseIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/MethodParameterUseIncorrect.obama"));
	}
	
	@Test
	public void testMethodReturn() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/MethodReturn.obama"));
	}
	
	@Test(expected=CheckerException.class)
	public void testMethodReturnIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/MethodReturnIncorrect.obama"));
	}
	
	/*
	 * Assignment
	 */
	@Test
	public void testAssignment() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/Assignment.obama"));
	}
	
	@Test(expected=CheckerException.class)
	public void testAssignmentIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/AssignmentIncorrect.obama"));
	}
	
	@Test
	public void testAssignmentMultiple() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/AssignmentMultiple.obama"));
	}
	
	@Test(expected=CheckerException.class)
	public void testAssignmentMultipleIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/AssignmentMultipleIncorrect.obama"));
	}
	
	/*
	 * Classes
	 */
	@Test
	public void testClass() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/Class.obama"));
	}
	
	@Test
	public void testClassMethodMix() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/ClassMethodMix.obama"));
	}
	
	/*
	 * Expressions
	 */
	@Test
	public void testExpressionLogicalOr() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/ExpressionLogicalOr.obama"));
	}
	
	@Test
	public void testExpressionLogicalAnd() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/ExpressionLogicalAnd.obama"));
	}
	
	@Test
	public void testExpressionRelational() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/ExpressionRelational.obama"));
	}
	
	@Test(expected=CheckerException.class)
	public void testExpressionRelationalIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/ExpressionRelationalIncorrect.obama"));
	}
	
	@Test
	public void testExpressionLowArithmetic() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/ExpressionLowArithmetic.obama"));
	}
	
	@Test
	public void testExpressionHighArithmetic() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/ExpressionHighArithmetic.obama"));
	}
	
	@Test
	public void testExpressionNegation() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/ExpressionNegation.obama"));
	}
	
	@Test
	public void testExpressionArithmeticOrder() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/ExpressionArithmeticOrder.obama"));
	}
	
	@Test
	public void testExpressionRelationalArithmeticOrder() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/ExpressionRelationalArithmeticOrder.obama"));
	}
	
	@Test
	public void testExpressionMultiple() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/ExpressionMultiple.obama"));
	}
	
	@Test(expected=CheckerException.class)
	public void testExpressionTypesIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/ExpressionTypesIncorrect.obama"));
	}
	
	/*
	 * If-else statements
	 */
	@Test
	public void testIfStatement() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/IfStatement.obama"));
	}
	
	@Test
	public void testIfElseStatement() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/IfElseStatement.obama"));
	}
	
	@Test
	public void testIfElseIfStatement() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/IfElseIfStatement.obama"));
	}
	
	@Test
	public void testIfElseIfElseStatement() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/IfElseIfElseStatement.obama"));
	}
	
	/*
	 * Switch statements
	 */
	@Test
	public void testSwitchCase() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/SwitchCase.obama"));
	}
	
	@Test
	public void testSwitchDefault() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/SwitchDefault.obama"));
	}
	
	@Test
	public void testSwitchCaseDefault() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/SwitchCaseDefault.obama"));
	}
	
	@Test
	public void testSwitchMixed() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/SwitchMixed.obama"));
	}
	
	@Test(expected=CheckerException.class)
	public void testSwitchMixedIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/SwitchMixedIncorrect.obama"));
	}
	
	@Test(expected=CheckerException.class)
	public void testSwitchMixedTypeIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/SwitchMixedTypeIncorrect.obama"));
	}
	
	@Test(expected=CheckerException.class)
	public void testSwitchDoubleLiterals() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/SwitchDoubleLiterals.obama"));
	}
	
	/*
	 * For-loop statements
	 */
	@Test
	public void testForLoop() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/ForLoop.obama"));
	}
	
	@Test(expected=CheckerException.class)
	public void testForLoopIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/ForLoopIncorrect.obama"));
	}
	
	/*
	 * While statements
	 */
	@Test
	public void testWhileLoop() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/WhileLoop.obama"));
	}
	
	@Test
	public void testWhileLoopForever() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/WhileLoopForever.obama"));
	}
	
	@Test(expected=CheckerException.class)
	public void testWhileLoopIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/WhileLoopIncorrect.obama"));
	}
	
	/*
	 * Built-ins
	 */
	@Test
	public void testBuiltIn() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/BuiltIn.obama"));
	}
	
	@Test
	public void testBuiltInAssign() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/BuiltInAssign.obama"));
	}
	
	@Test(expected=CheckerException.class)
	public void testBuiltInWrongMethodIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/BuiltInWrongMethodIncorrect.obama"));
	}
	
	@Test(expected=CheckerException.class)
	public void testBuiltInWrongParametersIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/BuiltInWrongParametersIncorrect.obama"));
	}
	
	@Test
	public void testBuiltInRead() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/BuiltInRead.obama"));
	}
	
	@Test
	public void testBuiltInReadExpression() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/BuiltInReadExpression.obama"));
	}
	
	@Test(expected=CheckerException.class)
	public void testBuiltInReadIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/BuiltInReadIncorrect.obama"));
	}
	
	@Test
	public void testBuiltInPrint() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/BuiltInPrint.obama"));
	}
	
	@Test
	public void testBuiltInPrintExpression() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/BuiltInPrintExpression.obama"));
	}
	
	@Test(expected=CheckerException.class)
	public void testBuiltInPrintIncorrect() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileChecker("checker/BuiltInPrintIncorrect.obama"));
	}
}
