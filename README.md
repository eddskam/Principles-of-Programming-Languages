# Principles-of-Programming-Languages
This is a compilation of some of the projects I did in my CS224 (Principles of Programming Languages) course I took at Goucher College. The course material involved studying the mechanisms that make programing languages work, illustrated with the use of Haskell, Java and Prolog.

Goal:
The aim of this course was to wite a compiler for the Jack language. Each project is a step towards this goal

Project Outline:

Project 1: Jack Tokenizer - 
This is the first step in writing the jack compiler. 
The tokenizer chunks the characters in the file into a list of tokens.
Each token is of type identifier, number, string or symbol

Project 2: Jack Parser -
This is step 2 and involves writing a Parser for the Jack language.
We start with a grammar which describes the syntax of the language.
The Parser then groups the tokens into a syntax tree which we will represent using xml.

Project 3: Jack Symbol Table -
This step involves writing a symbol table, which stores information on a variable represented by an identifier
we encounter when compiling code, and helps us to know what category the variable falls under (such as a field,
an argument to a function, or perhaps a local variable). It also stores information on the type of variable as well. 
