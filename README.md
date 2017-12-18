# Translator Java to C++

This Java to C++ translator fully works for test inputs 000 to 050.

The given task to translate Java programs to C++ was divided into 5 phases. The translator
currently supports translation of header files and implementation files for all 51 inputs
(000 to 050) given in src/test/java/inputs/. This means that the translator support dynamic
dispath, method overriding, method overloading, all runtime checks including those for arrays,
Java casts, and multidimensional arrays. Overall, this is a robust translator capable of
translating a variety of test inputs successfully.

It should be noted that the translator does not support more complicated translations such
as those that involve other Java packages (ex. java.util) or access to static variables
through a child class. This behavior can be seen when translating homework2 under inputs
folder. This file, named LinkedList.java, creates a linked list and is not supported under
our current translator.

You may run each phase separately or simply run the entire translator. The commands follow
bellow.

Before running, initiate a SBT shell, compile and run Boot.java

### To run phase x on SBT, type:

`runxtc -printPhasex <source file>`

The output for phases 1, 2 and 4 are displayed in the shell. 
The output for phases 3 and 5 are in the output directory.

### To view the name mangling scheme, type:

`runxtc -printMangling <source file>`

### To run the tests for phase x, type:

`test-only *Phasex*`

### To run all tests, type,

`test`

### To run translator, type:

`runxtc -translate <source file>`

### To run the translated code, type:

`cpp`

### Note:

You may run into errors when translating inputs 042 to 050.
This is due to an SBT error that we have been unable to figure
out but the translation will still work. You can continue with
cpp on these inputs after running -translate.

### To format translated C++ code into prettier output, type:

`formatc`

### To format all code (including source) into prettier format, type:

`format`

### To generate the documentation for phase x, open a terminal shell and type:

`javadoc Phasex`