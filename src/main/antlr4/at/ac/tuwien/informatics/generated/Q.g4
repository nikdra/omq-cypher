grammar Q ;

query : head ':-' body EOF ;

head : 'q(' variable (',' variable)* ')' ;

body : atom (',' atom)* ;

atom : conceptname | role | path ;

conceptname : WORD '(' variable ')' ;

role : WORD '(' left=variable ',' right=variable ')' ;

path : WORD '*'? ('/' WORD '*'?)* '(' variable ',' variable ')' ;

variable : LETTER ;

fragment LOWERCASE : [a-z] ;
fragment UPPERCASE : [A-Z] ;

LETTER : LOWERCASE ;

WORD : (LOWERCASE | UPPERCASE)+ ;

UNKNOWN_CHAR : . ;