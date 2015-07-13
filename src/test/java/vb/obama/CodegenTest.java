package vb.obama;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;

/**
 * Tests ObamaCodegen.g
 * 
 * @version 1.0
 * @see vb/obama/antlr/ObamaCodegen.g
 */
public class CodegenTest extends AbstractTest {
	/**
	 * Simple test for var declaration and initialisation
	 */
	@Test
	public void testSingleVar() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileCodegen("codegen/SingleVar.obama"));
	} 
	
	/** 
	 * Prints out Hello World and waits for user to press enter 
	 */
	@Test
	public void testHelloWorld() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileCodegen("codegen/HelloWorld.obama"));
	}
	
	/**
	 * Tests the simple assignments
	 */
	@Test
	public void testSimpleVars() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileCodegen("codegen/SimpleVars.obama"));
	}
	
	/**
	 * Tests multiple assignments
	 */
	@Test
	public void testMultipleAssignment() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileCodegen("codegen/MultipleAssignment.obama"));
	}
	
	/**
	 * Tests most operators and prints it to screen
	 */
	@Test
	public void testOperators() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileCodegen("codegen/Operators.obama"));
	}
	
	/**
	 * Tests method calls and return values
	 */
	@Test
	public void testMethodCalls() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileCodegen("codegen/MethodCalls.obama"));
	}
	
	/**
	 * Tests some statements
	 */
	@Test
	public void testStatements() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileCodegen("codegen/Statements.obama"));
	}
	
	/**
	 * Tests the generation of classes
	 */
	@Test
	public void testClassMethodMix() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileCodegen("codegen/ClassMethodMix.obama"));
	}
}