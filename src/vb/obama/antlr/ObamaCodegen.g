tree grammar ObamaCodegen;

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
	catch (CodegenException ce) 
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
	private CodegenHelper helper;
	
	public CodegenHelper getHelper() {
		return this.helper;
	}
}

// Start
program
	@init
	{
		this.helper = new CodegenHelper();
	}
	: 	^(node=PROGRAM imports* 
			{ 
				this.helper.visitContentStart(node); 
			}
			contents*
			{
				this.helper.visitContentEnd(node);
			}
		)
	;

// Imports
imports
	:	^(node=IMPORT PACKAGE)
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
	:	method_declaration
	;

// Classes
class_declaration
	:	^(node=CLASS IDENTIFIER 
			{
				this.helper.visitClassStart(node);
			}
			class_end
			{
				this.helper.visitClassEnd(node);
			}
		)
	;

class_end
	:	class_contents* 
	;

class_contents
	:	class_var_declaration 
	|	class_method_declaration 
	;

class_method_declaration
	:	method_declaration
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
	:	^(PARAMETER_UNNAMED IDENTIFIER type_statement)
	;

method_named_parameter
	:	^(PARAMETER_NAMED IDENTIFIER type_statement IDENTIFIER)
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
			
		}
	|	^(nodeB=PARAMETER_UNNAMED expression)
		{ 
			
		}
	|	^(nodeC=PARAMETER_NONE IDENTIFIER)
		{ 
			
		}
	;

// Var declaration
var_declaration
	:	^(node=VAR type IDENTIFIER (expression)?)
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
	:	^(node=IF_ELSE_IF_ELSE 
			{
				this.helper.visitIfStatementStart(node);
			}
			(if_statement_if if_statement_else_if* if_statement_else?)
			{
				this.helper.visitIfStatementEnd(node);
			}
		)
	;
	
if_statement_if
	:	^(node=IF 
			expression 
			{
				this.helper.visitIfStatementIfStart(node);
			}
			statements_end
			{
				this.helper.visitIfStatementIfEnd(node);
			}
		)
	;

if_statement_else_if
	:	^(node=ELSE_IF 
			{
				this.helper.visitIfStatementElseIf(node);
			}
			expression 
			{
				this.helper.visitIfStatementElseIfStart(node);
			}
			statements_end
			{
				this.helper.visitIfStatementElseIfEnd(node);
			}
		)
	;

if_statement_else
	:	^(node=ELSE 
			{
				this.helper.visitIfStatementElse(node);
			}
			statements_end
		)
	;

while_statement
	:	^(node=WHILE 
			{
				this.helper.visitWhileStatement(node);
			}
			expression
			{
				this.helper.visitWhileStatementStart(node);
			}
			statements_end
			{
				this.helper.visitWhileStatementEnd(node);
			}
		)
	;

for_statement
	:	^(node=FOR expression 
			{ 
				this.helper.visitForStatementStart(node);
			}
			expression 
			{ 
				this.helper.visitForStatementCompare(node);
			}
			expression
			{ 
				this.helper.visitForStatementIncrement(node);
			} 
			statements_end
			{
				this.helper.visitForStatementEnd(node);
			}
		)
	;

switch_statement
	:	^(node=SWITCH
			expression 
			{
				this.helper.visitSwitchStatementSwitchStart(node);
			}
			(switch_statement_case* switch_statement_default?)
			{
				this.helper.visitSwitchStatementSwitchEnd(node);
			}
		)
	;

switch_statement_case
	:	^(node=CASE
			{ 
				this.helper.visitSwitchStatementCaseStart(node);
			}
			^(SCOPE method_contents*)
			{ 
				this.helper.visitSwitchStatementCaseEnd(node);
			}
		)
	;

switch_statement_default
	:	^(node=DEFAULT 
			{ 
				this.helper.visitSwitchStatementDefault(node);
			}
			^(SCOPE  method_contents*)
		)
	;

statements_end
	:	^(SCOPE method_contents*)
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
	:	^(node=IF_INLINE
			logical_or_expression
			{
				this.helper.visitInlineIfStart(node);
			}  
			logical_or_expression
			{
				this.helper.visitInlineIfOther(node);
			}  
			logical_or_expression
			{
				this.helper.visitInlineIfEnd(node);
			} 
		)
	|	logical_or_expression
	;

logical_or_expression
	:	logical_and_expression
	|	^(node=L_OR logical_or_expression logical_or_expression)
		{
			this.helper.visitLogicalOrExpression(node);
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
	|	^(PAREN expression)
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
			this.helper.visitLiteral($INT_LITERAL, $INT_LITERAL.text); 
		}
	|	STRING_LITERAL
		{
			this.helper.visitLiteral($STRING_LITERAL, $STRING_LITERAL.text);
		}
	|	CHAR_LITERAL
		{
			this.helper.visitLiteral($CHAR_LITERAL, $CHAR_LITERAL.text);
		}
	|	YES
		{
			this.helper.visitLiteral($YES, new Boolean(true));
		}
	|	NO
		{
			this.helper.visitLiteral($NO, new Boolean(false));
		}
	;

// Types
type
	:	TYPE
	;
	
type_statement
	:	type
	;

// EOF