package vb.obama;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import vb.obama.util.ProcessRunner;

/**
 * Tests the project requirements, as far as they are implemented
 * @version 1.0
 */
public class RequirementsTest extends AbstractTest {
	/**
	 * Path to files, relative to project root
	 */
	private static String BASE_PATH = "data/tests/requirements/";
	
	/**
	 * Path to output directory, relative to project root
	 */
	private static String OUTPUT_PATH = "tmp/";
	
	/** 
	 * Demonstrates recursion by calculating factorial 5.
	 * 
	 * Expected output: 120
	 * @see data/tests/requirements/Recursion.obama
	 */
	@Test
	public void testRecursion() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileCodegen(BASE_PATH + "Recursion.obama", OUTPUT_PATH));
		assertEquals("120", ProcessRunner.runJavaFile("Recursion", OUTPUT_PATH));
	}
	
	/** 
	 * Demonstrates multiple assignment by setting three variables to 10 and 
	 * calculate their sum.
	 * 
	 * Expected output: 30 
	 * @see data/tests/requirements/MultipleAssignment.obama
	 */
	@Test
	public void testMultipleAssignment() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileCodegen(BASE_PATH + "MultipleAssignment.obama", OUTPUT_PATH));
		assertEquals("30", ProcessRunner.runJavaFile("MultipleAssignment", OUTPUT_PATH));
	}
	
	/** 
	 * Demonstrates the inline-if statement by checking for a condition and return
	 * the correct value.
	 * 
	 * Expected output: False
	 * @see data/tests/requirements/InlineIf.obama
	 */
	@Test
	public void testInlineIf() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileCodegen(BASE_PATH + "InlineIf.obama", OUTPUT_PATH));
		assertEquals("False", ProcessRunner.runJavaFile("InlineIf", OUTPUT_PATH));
	}
	
	/**
	 * Demonstrates if-else by checking a condition and then output a text,
	 * related to the condition. Then, the same, but by taking the negation of
	 * the condition
	 * 
	 * Expected output: False True
	 * @see data/tests/requirements/IfElse.obama
	 */
	@Test
	public void testIfElse() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileCodegen(BASE_PATH + "IfElse.obama", OUTPUT_PATH));
		assertEquals("False True", ProcessRunner.runJavaFile("IfElse", OUTPUT_PATH));
	}
	
	/**
	 * Demonstrates the While-do loop by counting from 10 to 1, checking if the
	 * next number is bigger then 10.
	 * 
	 * Expected output: 10 9 8 7 6 5 4 3 2 1
	 * @see data/tests/requirements/WhileDo.obama
	 */
	@Test
	public void testWhileDo() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileCodegen(BASE_PATH + "WhileDo.obama", OUTPUT_PATH));
		assertEquals("10 9 8 7 6 5 4 3 2 1 ", ProcessRunner.runJavaFile("WhileDo", OUTPUT_PATH));
	}
	
	/**
	 * Demonstrates the use of the built-in method print, which allows us to 
	 * print a value or value list. In case of a single value, the value can
	 * be directly used as an expression.
	 * 
	 * Expected output: 3 2 1 1 2
	 * @see data/tests/requirements/Print.obama
	 */
	@Test
	public void testPrint() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileCodegen(BASE_PATH + "Print.obama", OUTPUT_PATH));
		assertEquals("32112", ProcessRunner.runJavaFile("Print", OUTPUT_PATH));
	}
	
	/**
	 * Demonstrates the use of the built-in method read, which reads user input
	 * and parses it as an integer. The method allows one or more parameters.
	 * With only one parameter, this parameter will be filled by the read, and
	 * returned. With more parameters, it will return void.
	 * 
	 * Expected output: The second integer is bigger than first integer
	 * @see data/tests/requirements/Read.obama
	 */
	@Test
	public void testRead() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileCodegen(BASE_PATH + "Read.obama", OUTPUT_PATH));
	}
	
	/**
	 * Test some expressions without assigning them or using them. The results 
	 * are directly popped if they are not used somewhere in the expression. 
	 * If it worked, the stack is clean at the end and displays a message. Also,
	 * the program should compile because ASM checks for invalid stacks.
	 * 
	 * Expected output: Stack clean :)
	 * @see data/tests/requirements/PopTest.obama 
	 */
	@Test
	public void testPopTest() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileCodegen(BASE_PATH + "PopTest.obama", OUTPUT_PATH));
		assertEquals("Stack clean :)", ProcessRunner.runJavaFile("PopTest", OUTPUT_PATH));
	}
	
	/**
	 * Tests the initialization of an ArrayList and add a few elements to the 
	 * list and then print the list. This demonstrates the use of the new Object
	 * expression and the use of casting and accessing fields of instances.
	 * 
	 * Expected output: [One, Two, Three]
	 * @see data/tests/requirements/NewExpression.obama 
	 */
	@Test
	public void testNewExpression() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileCodegen(BASE_PATH + "NewExpression.obama", OUTPUT_PATH));
		assertEquals("[One, Two, Three]", ProcessRunner.runJavaFile("NewExpression", OUTPUT_PATH));
	}
	
	/**
	 * A test that is similar to testNewExpression, but it also includes a
	 * constructor with a parameter. It outputs the size of the file that is
	 * currently run. The file size may vary because it depends on the location
	 * of the source file (which is included in the bytecode).
	 * 
	 * Expected output: The file FileInfo.class is 715 bytes big
	 * @see data/tests/requirements/FileInfo.obama 
	 */
	@Test
	public void testFileInfo() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileCodegen(BASE_PATH + "FileInfo.obama", OUTPUT_PATH));
		String output = ProcessRunner.runJavaFile("FileInfo", OUTPUT_PATH);
		assertEquals(true, output.contains("The file FileInfo.class is"));
		assertEquals(true, output.contains("bytes big"));
	}
	
	/**
	 * A test that tests the correct operator types and outputs, also testing the
	 * operator priority.
	 * 
	 * Expected output: 15 -5 5 50 50 2 42 5 0 false true false true true true 
	 * 					true false true false true
	 * 
	 * @see data/tests/requirements/Operators.obama 
	 */
	@Test
	public void testOperators() throws RecognitionException, IOException {
		assertEquals(0, this.executeFileCodegen(BASE_PATH + "Operators.obama", OUTPUT_PATH));
		assertEquals("15 -5 5 50 50 2 42 5 0 false true false true true true true false true false true", 
				ProcessRunner.runJavaFile("Operators", OUTPUT_PATH));
	}
}
