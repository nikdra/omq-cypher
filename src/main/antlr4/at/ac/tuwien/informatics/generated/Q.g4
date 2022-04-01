grammar Q ;

query : head ':-' body EOF ;

head : 'q(' variable (',' variable)* ')' ;

variable : WORD ;

body : atom (',' atom )* ;

atom : conceptname | roles | path;

conceptname : words '(' variable ')' ;

roles : properties '(' left=variable ',' right=variable ')' ;

path : elements '(' left=variable ',' right=variable ')' ;

elements : pathElement ('/' pathElement)* ;

pathElement : arbitraryLengthPathElement | singleLengthPathElement ;

arbitraryLengthPathElement : rolename '*' | '(' rolename ('|' rolename)+ ')' '*';

singleLengthPathElement : rolename | '(' rolename ('|' rolename)+ ')' ;

properties : property | '(' property ('|' property)+ ')' ;

property : rolename | inverse ;

rolename : words ;

inverse : words'-' ;

words : WORD ('_' WORD)* ;

WORD : LETTER+ ;

fragment LETTER : ('a'..'z' | 'A'..'Z') ;

UNKNOWN_CHAR : . ;