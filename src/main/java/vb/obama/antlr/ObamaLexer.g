lexer grammar ObamaLexer;

// Zie ook: http://stackoverflow.com/questions/1088457/difference-between-token-definition-and-lexer-tokens
tokens {
	PROGRAM;
	
	MODIFIERS;
	
	GLOBAL_VAR;
	GLOBAL_CONST;
	GLOBAL_METHOD;
	
	CLASS_VAR;
	CLASS_CONST;
	CLASS_METHOD;
	
	WITHOUT_PARAMETERS;
	WITH_PARAMETERS;
	
	METHOD_CONTENTS;
	CLASS_CONTENTS;
	
	METHOD_VAR;
	METHOD_COMMAND;
	METHOD_ASSIGN;
	
	PAREN;
	SCOPE;
	METHOD;
	VAR;
	TYPE;
	ARRAY;
	
	FIELD_CALL;
	METHOD_CALL;
	IDENTIFIER_USE;
	
	FIELD;
	OBJECT;
	PACKAGE;
	
	PARAMETER;
	PARAMETERS;
	PARAMETER_NONE;
	PARAMETER_NAMED;
	PARAMETER_UNNAMED;
	
	IF_INLINE;
	ELSE_IF;
	IF_ELSE_IF_ELSE;
	SWITCH_BAG;
}

@header{
	package vb.obama.antlr;
}

// Interpunction
SEMICOLON			: ';';
COLON				: ':';
LBRACKET			: '{';
RBRACKET			: '}';
LPAREN				: '(';
RPAREN				: ')';
LBLOCK				: '[';
RBLOCK				: ']';
COMMA				: ',';
PERIOD				: '.';
DQUOT				: '"';
SQUOT				: '\'';
ESC					: '\\';
AT					: '@';
TILDE				: '~';

// Arithmetic operators
PLUS				: '+';
MIN					: '-';
MULT				: '*';
DIV					: '/';
MOD					: '%';

// Bitwise operators
B_AND				: '&';
B_OR				: '|';

// Logical operators
L_AND				: '&&';
L_OR				: '||';

// Relational operators
EQ					: '==';
NEQ					: '!=';
GT					: '>';
LT					: '<';
GTEQ				: '>=';
LTEQ				: '<=';

// Other operators
ASSIGN				: '=';
NOT					: '!';
QUESTION			: '?';

// Keywords
CLASS				: 'class';
THIS				: 'self';
GLOBAL				: 'global';
BUILTIN				: 'builtin';
NEW					: 'new';
BREAK				: 'break';
DEFAULT				: 'default';
RETURN				: 'return';
IMPORT				: '#import';

// Conditional Commands
IF					: 'if';
ELSE				: 'else';
FOR					: 'for';
FOREACH				: 'foreach';
WHILE				: 'while';
SWITCH				: 'switch';
CASE				: 'case';

// Constant definition
CONST				: 'const';

// Primitive datatypes
VOID				: 'void';
INT					: 'int';
BOOL				: 'bool';
CHAR				: 'char';

// YES/NO
YES					: 'YES';
NO					: 'NO';

// Literals
IDENTIFIER			: LETTER (ALPHANUM)*;
CHAR_LITERAL 		: SQUOT ALPHANUM SQUOT;
STRING_LITERAL		: AT DQUOT (options{greedy=true;} : (~(DQUOT | '\n' | '\r'))*) DQUOT;
INT_LITERAL			: DIGIT+;

// Ignoreables
COMMENT				: '/*' .* '*/' { $channel=HIDDEN; };
WS					: (' ' | '\t' | '\f' | '\r' | '\n')+  { $channel=HIDDEN; };

fragment ALPHANUM	: (LETTER | DIGIT);	
fragment LETTER		: (LOWER | UPPER);
fragment LOWER		: ('a'..'z');
fragment UPPER		: ('A'..'Z');
fragment DIGIT		: ('0'..'9');

// EOF