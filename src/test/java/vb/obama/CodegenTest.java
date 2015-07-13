package vb.obama;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;

/**
 * Tests ObamaCodegen.g
 * 
 * @version 1.0
 * @see ObamaCodegen.g
 */
public class CodegenTest extends AbstractTest {
	/**
	 * Path to files, relative to project root
	 */
	private static String BASE_PATH = "/Users/basilfx/Desktop/Course-CompilerDesign/src/test/resources/tests/codegen/";
	
	/**
	 * Path to output directory, relative to project root
	 */
	private static String OUTPUT_PATH = "tmp/";
	
	/**
	 * Simple test for var declaration and initialisation
	 */
	@Test
	public void testSingleVar() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileCodegen(BASE_PATH + "SingleVar.obama", OUTPUT_PATH));
	} 
	
	/** 
	 * Prints out Hello World and waits for user to press enter 
	 */
	@Test
	public void testHelloWorld() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileCodegen(BASE_PATH + "HelloWorld.obama", OUTPUT_PATH));
	}
	
	/**
	 * Tests the simple assignments
	 */
	@Test
	public void testSimpleVars() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileCodegen(BASE_PATH + "SimpleVars.obama", OUTPUT_PATH));
	}
	
	/**
	 * Tests multiple assignments
	 */
	@Test
	public void testMultipleAssignment() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileCodegen(BASE_PATH + "MultipleAssignment.obama", OUTPUT_PATH));
	}
	
	/**
	 * Tests most operators and prints it to screen
	 */
	@Test
	public void testOperators() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileCodegen(BASE_PATH + "Operators.obama", OUTPUT_PATH));
	}
	
	/**
	 * Tests method calls and return values
	 */
	@Test
	public void testMethodCalls() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileCodegen(BASE_PATH + "MethodCalls.obama", OUTPUT_PATH));
	}
	
	/**
	 * Tests some statements
	 */
	@Test
	public void testStatements() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileCodegen(BASE_PATH + "Statements.obama", OUTPUT_PATH));
	}
	
	/**
	 * Tests the generation of classes
	 */
	@Test
	public void testClassMethodMix() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileCodegen(BASE_PATH + "ClassMethodMix.obama", OUTPUT_PATH));
	}
}