grammar Q ;

query : head ':-' body EOF ;

head : 'q(' variable (',' variable)* ')' ;

body : atom (',' atom)* ;

atom : conceptname | role | path ;

conceptname : WORDS '(' variable ')' ;

role : WORDS '(' left=variable ',' right=variable ')' ;

path : elements '(' left=variable ',' right=variable ')' ;

elements : pathElement ('/' pathElement )* ;

pathElement : arbitraryLengthPathElement | singleLengthPathElement ;

arbitraryLengthPathElement : WORDS '*' | '(' WORDS ('|' WORDS)+ ')' '*' ;

singleLengthPathElement : WORDS | '(' WORDS ('|' WORDS)+ ')' ;

variable : LETTER ;

fragment LOWERCASE : [a-z] ;
fragment UPPERCASE : [A-Z] ;

LETTER : LOWERCASE ;

WORDS : (LOWERCASE | UPPERCASE)+ ('_' WORDS)*;

UNKNOWN_CHAR : . ;