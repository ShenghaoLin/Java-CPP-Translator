# Translator Java to C++

This is a work in progress.

The given task to translate Java programs to C++ was divided in 5 phases. The translator
currently supports translation of header files for the first 20 test inputs given in
src/test/java/inputs/. Furthermore, translation of implementation and main files are also
supported for some inputs. However, this is conditional on the input for now since Phases 4
and 5 are not fully working yet. For example, the translator will correctly translate 
src/test/java/inputs/Input/Input.java (which is the exercise from java-lang-in-class) but
may not translate other inputs due to lack of functionality in Phase 4. Thus, phases 4 and 5
are still a work in progress for the first 20 inputs.

The translator currently supports translations up to the Object initialization class done
in class. Arrays and null checking are not supported. As stated above, the translation is 
correct for the first 20 inputs for their header files. For the implementation and main
files the translation is still condiitonal on the contents of the input.

You may run each separately or simply run the entire translator. The commands follow bellow.

Before running, initiate a SBT shell, compile and run Boot.java

### To run phase x on SBT, type:

`runxtc -printPhasex <source file>`

The output for phases 1, 2 and 4 are prompted in the shell. 
The output for phases 3 and 4 are in the output directory.

### To run the tests for phase x, type:

`test-only *Phasex*` 

### To run translator, type:

`runxtc -translate [path/to/input]`

### To run the translated code, type:

`cpp`

### To format translated C++ code into prettier output, type:

`formatc`

### To generate the documentation for phase x, open a terminal shell and type:

`javadoc Phasex`

