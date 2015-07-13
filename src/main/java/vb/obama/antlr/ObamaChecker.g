tree grammar ObamaChecker;

options {
	k				= 1;
	language		= Java;
	output			= AST;
	tokenVocab		= ObamaLexer;
	ASTLabelType	= TypedNode;
}

@header {
	package vb.obama.antlr;
	
	import org.apache.log4j.Logger;
	import vb.obama.antlr.tree.*;
	
	import vb.obama.util.*;
	import vb.obama.compiler.*;
	import vb.obama.exceptions.*;
	
	import static com.google.common.base.Preconditions.*;
}

@rulecatch {
	catch (CheckerException ce) 
	{
		throw ce;
	}
	catch (RecognitionException re) 
	{
		reportError(re);
		recover(input,re);
	}
}

@members {
	/**
	 * Message logger
	 */
	private static final Logger logger = Logger.getLogger(ObamaChecker.class.getName());

	private SymbolTable table;
	
	/**
	 * Reference to the checker for internal use and Java code minimalisation
	 */
	private CheckerHelper helper;
	
	private String inputFile;
	
	/**
	 * @requires table != null;
	 */
	public void setSymbolTable(SymbolTable table) {
		this.table = checkNotNull(table);
	}
	
	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}
}

// Start
program
	@init
	{
		this.helper = new CheckerHelper(this.table, this.inputFile);
	}
	: 	^(node=PROGRAM imports* 
			(
				{ 
					this.helper.visitContentStart(node);
					this.helper.openScope();
				}
				(contents*)
				{
					this.helper.closeScope();
					this.helper.visitContentEnd(node);
				}
			)
		)
	;

// Imports
imports
	:	^(node=IMPORT PACKAGE)
		{ 
			this.helper.visitImport(node); 
		}
	;

// Rest of file
contents
	:	class_declaration
	|	global_method_declaration
	|	global_var_declaration
	;

// Globals
global_var_declaration
	:	var_declaration
	|	const_declaration
	;

global_method_declaration
	: 	method_declaration
	;

// Classes
class_declaration
	:	^(node=CLASS IDENTIFIER 
			{
				this.helper.visitClass(node);
			}
			class_end
			{
				
			}
		)
	;

class_end
	:	{ 
			this.helper.openScope(); 
		} 
		(class_contents*) 
		{ 
			this.helper.closeScope(); 
		} 
	;

class_contents
	:	class_var_declaration 
	|	class_method_declaration 
	;

class_method_declaration
	:	 method_declaration
	;

class_var_declaration
	:	var_declaration
	|	const_declaration
	;

// Method declarations
method_declaration
	: 	^(node=METHOD MODIFIERS type_statement IDENTIFIER ^(PARAMETERS method_parameters?)
			{
				this.helper.visitMethodDeclaration(node);
				this.helper.visitMethodContentStart(node);
			}
			method_parameters_end
			{
				this.helper.visitMethodContentEnd(node);
			}
		)
	;

method_parameters
	: 	method_unnamed_parameter method_other_parameters*
	;
	
method_other_parameters
	:	method_named_parameter
	| 	method_unnamed_parameter
	;

method_unnamed_parameter
	:	^(node=PARAMETER_UNNAMED IDENTIFIER type_statement) 
		{ 
			this.helper.visitMethodParameterUnnamed(node); 
		}
	;

method_named_parameter
	:	^(node=PARAMETER_NAMED IDENTIFIER type_statement IDENTIFIER)
		{ 
			this.helper.visitMethodParameterNamed(node); 
		}
	;

method_parameters_end
	:	^(node=SCOPE method_contents*)
	;

method_contents
	:	method_command
	|	statement_call
	;

method_command
	:	var_declaration
	|	expression
	|	^(node=RETURN expression)
		{
			this.helper.visitMethodReturn(node);
		}
	;

method_call
	:	^(node=METHOD_CALL field method_call_inner+)
		{
			this.helper.visitMethodCall(node);
		}
	;
	
method_call_inner	
	:	^(nodeA=PARAMETER_NAMED IDENTIFIER expression) 
		{ 
			this.helper.visitMethodCallParameterNamed(nodeA);
		}
	|	^(nodeB=PARAMETER_UNNAMED expression)
		{ 
			this.helper.visitMethodCallParameterUnnamed(nodeB);
		}
	|	^(nodeC=PARAMETER_NONE IDENTIFIER)
		{ 
			this.helper.visitMethodCallNoParameters(nodeC);
		}
	;

// Var declaration
var_declaration
	:	^(node=VAR type IDENTIFIER)
		{
			this.helper.visitVarDeclaration(node);
		}
	;
	
const_declaration
	:	^(node=CONST type IDENTIFIER literal)
		{
			this.helper.visitConstDeclaration(node);
		}
	;

// Conditional statements
statement_call
	:	if_statement
	|	while_statement
	|	for_statement
	|	switch_statement
	;

if_statement
	:	^(node=IF_ELSE_IF_ELSE if_statement_if if_statement_else_if* if_statement_else?)
		{
			this.helper.visitIfStatement(node);
		}
	;

if_statement_if
	:	^(node=IF expression 
			{
				this.helper.visitIfStatementIf(node);
			}
		statements_end)
	;

if_statement_else_if
	:	^(node=ELSE_IF expression 
			{
				this.helper.visitIfStatementElseIf(node);
			}
		statements_end)
	;

if_statement_else
	:	^(node=ELSE 
			{
				this.helper.visitIfStatementElse(node);
			}
		statements_end)
	;

while_statement
	:	^(node=WHILE expression 
			{
				this.helper.visitWhileStatement(node);
			}
		statements_end)
	;

for_statement
	:	^(node=FOR expression expression expression 
			{
				this.helper.visitForStatement(node);
			}
		statements_end)
	;

switch_statement
	:	^(node=SWITCH expression 
			{
				this.helper.visitSwitchStatementSwitch(node);
			}
			(switch_statement_case* switch_statement_default?)
		)
	;

switch_statement_case
	:	^(node=CASE
			{ 
				this.helper.visitSwitchStatementCase(node);
				this.helper.openScope(); 
			}
			^(SCOPE method_contents*)
			{ 
				this.helper.closeScope(); 
			}
		)
	;

switch_statement_default
	:	^(node=DEFAULT 
			{ 
				this.helper.visitSwitchStatementDefault(node);
				this.helper.openScope(); 
			}
			^(SCOPE method_contents*)
			{ 
				this.helper.closeScope(); 
			}
		)
	;

statements_end
	:	{ 
			this.helper.openScope(); 
		}
		^(SCOPE  method_contents*)
		{ 
			this.helper.closeScope(); 
		}
	;

// Expression
expression
	:	assign_expression
	;
	
assign_expression
	:	inline_if_expression
	|	^(node=ASSIGN assign_expression assign_expression)
		{
			this.helper.visitAssignExpression(node);
		}
	;
	
inline_if_expression
	:	^(node=IF_INLINE logical_or_expression logical_or_expression logical_or_expression)
		{
			this.helper.visitInlineIf(node);
		}
	|	logical_or_expression
	;

logical_or_expression
	:	logical_and_expression
	|	^(node=L_OR logical_or_expression logical_or_expression)
		{
			this.helper.visitLogicalAndExpression(node);
		}
	;
	
logical_and_expression
	:	bitwise_or_expression
	|	^(node=L_AND logical_and_expression logical_and_expression)
		{
			this.helper.visitLogicalAndExpression(node);
		}
	;

bitwise_or_expression
	:	bitwise_and_expression
	| 	^(node=B_OR bitwise_or_expression bitwise_or_expression)
		{
			this.helper.visitBitwiseOrExpression(node);
		}
	;

bitwise_and_expression
	:	relational_expression
	|	^(node=B_AND bitwise_and_expression bitwise_and_expression)
		{
			this.helper.visitBitwiseAndExpression(node);
		}
	;

relational_expression
	:	low_arithmetic_expression
	|	^(node=GT relational_expression relational_expression)
		{
			this.helper.visitGTExpression(node);
		}
	|	^(node=LT relational_expression relational_expression)
		{
			this.helper.visitLTExpression(node);
		}
	|	^(node=GTEQ relational_expression relational_expression)
		{
			this.helper.visitGTEQExpression(node);
		}
	|	^(node=LTEQ relational_expression relational_expression)
		{
			this.helper.visitLTEQExpression(node);
		}
	|	^(node=EQ relational_expression relational_expression)
		{
			this.helper.visitEQExpression(node);
		}
	|	^(node=NEQ relational_expression relational_expression)
		{
			this.helper.visitNEQExpression(node);
		}
	;

low_arithmetic_expression
	:	high_arithmetic_expression 
	|	^(node=PLUS low_arithmetic_expression low_arithmetic_expression)
		{
			this.helper.visitPlusExpression(node);
		}
	|	^(node=MIN low_arithmetic_expression low_arithmetic_expression)
		{
			this.helper.visitMinExpression(node);
		}
	;

high_arithmetic_expression
	:	negation_expression 
	|	^(node=MULT high_arithmetic_expression high_arithmetic_expression)
		{
			this.helper.visitMultExpression(node);
		}
	|	^(node=DIV high_arithmetic_expression high_arithmetic_expression)
		{
			this.helper.visitDivExpression(node);
		}
	|	^(node=MOD high_arithmetic_expression high_arithmetic_expression)
		{
			this.helper.visitModExpression(node);
		}
	;

negation_expression
	:	other_expression
	|	^(node=NOT other_expression)
		{
			this.helper.visitNotExpression(node);
		}
	;

other_expression
	:	literal
	|	method_call
	|	field
	|	^(node=NEW field)
		{
			this.helper.visitNewExpression(node);
		}
	|	^(node=PAREN expression) 
		{
			this.helper.visitParenExpression(node);
		}
	;
	
field
	:	node=BUILTIN
		{
			this.helper.visitBuiltin(node);
		}
	|	node=THIS
		{
			this.helper.visitThis(node);
		}
	|	node=GLOBAL
		{
			this.helper.visitGlobal(node);
		}
	|	node=FIELD
		{
			this.helper.visitField(node);
		}
	;

literal
	:	INT_LITERAL
		{ 
			this.helper.visitLiteral($INT_LITERAL, int.class); 
		}
	|	STRING_LITERAL
		{
			this.helper.visitLiteral($STRING_LITERAL, String.class);
		}
	|	CHAR_LITERAL
		{
			this.helper.visitLiteral($CHAR_LITERAL, char.class);
		}
	|	YES
		{
			this.helper.visitLiteral($YES, boolean.class);
		}
	|	NO
		{
			this.helper.visitLiteral($NO, boolean.class);
		}
	;

// Types
type
	:	node=TYPE
		{
			this.helper.visitType(node);
		}

	;

type_statement
	:	type
	;

// EOF