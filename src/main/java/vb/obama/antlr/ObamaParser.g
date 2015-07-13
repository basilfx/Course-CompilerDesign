parser grammar ObamaParser;

options {
	k				= 1;
	language		= Java;
	output			= AST;
	tokenVocab		= ObamaLexer;
	ASTLabelType	= TypedNode;
}

@header{
	package vb.obama.antlr;
	
	import org.apache.log4j.Logger;
	
	import vb.obama.antlr.tree.*;
	import vb.obama.util.*;
	import java.lang.reflect.*;
	
	import static com.google.common.base.Preconditions.*;
}

@members {
	/**
	 * Message logger
	 */
	private static final Logger logger = Logger.getLogger(ObamaParser.class.getName());
	
	/**
	 * Only allow wild import of package names in import statements
	 */
	private boolean isImport = false;
	
	/**
	 * Prevent return statements in right hand side
	 */
	private boolean isAssignment = false;
}

// Start
program
	:	imports* contents* EOF -> ^(PROGRAM imports* contents*)
	;

// Imports
imports
	:	{isImport = true;} IMPORT LT node=package_path GT {isImport = false;}
		-> ^(IMPORT PACKAGE[$node.path])
	;

// Rest of file
contents
	:	class_declaration
	|	global_method_declaration
	|	global_var_declaration SEMICOLON!
	;

// Globals
global_var_declaration
	:	var_declaration
	|	const_declaration
	;
	
global_method_declaration
	:	method_declaration[(Modifier.PUBLIC + Modifier.STATIC)]
	;

// Classes
class_declaration
	:	CLASS^ IDENTIFIER class_end
	;

class_end
	:	LBRACKET! class_contents* RBRACKET!
	;

class_contents
	:	class_var_declaration SEMICOLON! 
	|	class_method_declaration
	;

class_method_declaration
	:	node=method_accessor! method_declaration[$node.modifiers]
	;

class_var_declaration
	:	var_declaration
	|	const_declaration
	;

// Method declarations
method_declaration[int modifiers]
	: 	type_statement IDENTIFIER method_parameters? method_parameters_end
		-> ^(METHOD MODIFIERS[String.valueOf($modifiers)] type_statement IDENTIFIER ^(PARAMETERS method_parameters?) method_parameters_end)
	;
	
method_accessor returns [int modifiers]
	:	MIN	{ $modifiers = Modifier.PRIVATE; }
	|	PLUS { $modifiers = Modifier.PUBLIC; }
	|	TILDE { $modifiers = Modifier.PROTECTED; } 
	;

method_parameters
	: 	method_unnamed_parameter method_other_parameters*
	;
	
method_other_parameters
	:	method_named_parameter
	| 	method_unnamed_parameter
	;

method_unnamed_parameter
	:	COLON type_statement IDENTIFIER
		-> ^(PARAMETER_UNNAMED IDENTIFIER type_statement)
	;

method_named_parameter
	:	IDENTIFIER COLON type_statement IDENTIFIER 
		-> ^(PARAMETER_NAMED IDENTIFIER type_statement IDENTIFIER)
	;
	
method_parameters_end
	:	LBRACKET method_contents* RBRACKET -> ^(SCOPE method_contents*)
	;

method_contents
	:	method_command SEMICOLON!
	|	statement_call
	|	SEMICOLON!
	;

method_command
	:	(type IDENTIFIER) => var_declaration -> var_declaration
	|	expression
	|	RETURN expression -> ^(RETURN expression)
	;

method_call
	:	LBLOCK field method_call_inner+ RBLOCK 
		-> ^(METHOD_CALL field method_call_inner+)
	;
	
method_call_inner
	:	(IDENTIFIER COLON) => IDENTIFIER COLON expression -> ^(PARAMETER_NAMED IDENTIFIER expression)
	|	COLON expression -> ^(PARAMETER_UNNAMED expression)
	|	IDENTIFIER -> ^(PARAMETER_NONE IDENTIFIER)
	;

// Var declaration
var_declaration
	:	type path=IDENTIFIER (ASSIGN expression)?
		-> ^(VAR type IDENTIFIER) ^(ASSIGN FIELD[path] expression)?
	;

const_declaration
	:	CONST type IDENTIFIER ASSIGN literal 
		-> ^(CONST type IDENTIFIER literal)
	;

// Conditional statements
statement_call
	:	if_statement
	|	while_statement
	|	for_statement
	|	switch_statement
	;

if_statement
	:	if_statement_if ((ELSE IF) => if_statement_else_if)* if_statement_else?
		-> ^(IF_ELSE_IF_ELSE if_statement_if if_statement_else_if* if_statement_else?)
	;

if_statement_if
	:	IF LPAREN expression RPAREN statements_end 
		-> ^(IF expression statements_end)
	;

if_statement_else_if
	:	ELSE IF LPAREN expression RPAREN statements_end
		-> ^(ELSE_IF expression statements_end)
	;

if_statement_else
	:	ELSE statements_end 
		-> ^(ELSE statements_end)
	;

while_statement
	:	WHILE LPAREN expression RPAREN statements_end 
		-> ^(WHILE expression statements_end)
	;

for_statement
	:	FOR LPAREN expression SEMICOLON expression SEMICOLON expression RPAREN statements_end 
		-> ^(FOR expression expression expression statements_end)
	;

switch_statement
	:	SWITCH LPAREN expression RPAREN LBRACKET switch_statement_case* switch_statement_default? RBRACKET 
		-> ^(SWITCH expression switch_statement_case* switch_statement_default?)
	;

switch_statement_case
	:	CASE literal COLON method_contents* BREAK SEMICOLON 
		-> ^(CASE[$literal.text] ^(SCOPE method_contents*))
	;

switch_statement_default
	:	DEFAULT COLON method_contents* 
		-> ^(DEFAULT ^(SCOPE method_contents*))
	;

statements_end
	:	LBRACKET method_contents* RBRACKET 
		-> ^(SCOPE method_contents*)
	;

// Expression
expression
	:	assign_expression
	;
	
assign_expression
	:	inline_if_expression (ASSIGN^ assign_expression)?
	;
	
inline_if_expression
	:	(logical_or_expression QUESTION) => (
			logical_or_expression QUESTION logical_or_expression COLON logical_or_expression
			-> ^(IF_INLINE logical_or_expression logical_or_expression logical_or_expression)
		)
	|	logical_or_expression
	;

logical_or_expression
	:	logical_and_expression (L_OR^ logical_and_expression)*
	;
	
logical_and_expression
	:	bitwise_or_expression (L_AND^ bitwise_or_expression)*
	;
	
bitwise_or_expression
	:	bitwise_and_expression (B_OR^ bitwise_and_expression)*
	;

bitwise_and_expression
	:	relational_expression (B_AND^ relational_expression)*
	;

relational_expression
	:	low_arithmetic_expression ((GT^ | LT^ | GTEQ^ | LTEQ^ | EQ^ | NEQ^) low_arithmetic_expression)*
	;

low_arithmetic_expression
	:	high_arithmetic_expression (
			(PLUS) => (PLUS^ high_arithmetic_expression) |
			(MIN) => (MIN^ high_arithmetic_expression)
		)*
	;

high_arithmetic_expression
	:	negation_expression ((MULT^ | DIV^ | MOD^) negation_expression)*
	;

negation_expression
	:	(NOT^)? other_expression
	;

other_expression
	:	literal
	|	method_call
	|	field
	| 	NEW field -> ^(NEW field)
	|	LPAREN expression RPAREN -> ^(PAREN expression)
	;
	
field
	:	BUILTIN
	|	THIS
	|	GLOBAL
	|	node=package_path -> FIELD[$node.path]
	;

literal
	:	PLUS INT_LITERAL -> INT_LITERAL
	|	MIN INT_LITERAL { $INT_LITERAL.setText("-" + $INT_LITERAL.getText()); } -> INT_LITERAL
	|	INT_LITERAL
	|	STRING_LITERAL { String t = $STRING_LITERAL.getText(); $STRING_LITERAL.setText(t.substring(2, t.length() - 1)); }
	|	CHAR_LITERAL { String t = $CHAR_LITERAL.getText(); $CHAR_LITERAL.setText(t.substring(1, 2)); }
	|	YES
	|	NO
	;

// Package names and paths
package_path returns [String path = ""]
	:	IDENTIFIER 
		{ 
			$path = $IDENTIFIER.text; 
		} 
		(
			PERIOD part=package_name
			{ 
				$path = $path + "." + $part.path;
			}
		)?
	;

package_name returns [String path = ""]
	:	{isImport}? MULT 
		{ 
			$path = "*"; 
		}
	|	part=package_path
		{ 
			$path = $part.path;
		}
	;

// Types
type returns [String type = ""]
	:	node=sub_type
		{
			$type = $node.type;
		}
		(
			array_declare
			{
				$type = $node.type + "+";
			}
		)*
		-> ^(TYPE[$type])
	;
	
sub_type returns [String type]
	:	node=package_path 
		{ 
			$type = $node.path;
		}
	|	VOID 
		{ 
			$type = "void"; 
		}
	|	INT 
		{ 
			$type = "int"; 
		}
	|	BOOL 
		{ 
			$type = "bool"; 
		}
	|	CHAR 
		{ 
			$type = "char"; 
		}
	;

type_statement
	:	LPAREN! type RPAREN!
	;
	
array_access
	:	(LBLOCK expression RBLOCK) -> ^(ARRAY expression)
	;
	
array_declare
	:	LBLOCK! RBLOCK!
	;

// EOF